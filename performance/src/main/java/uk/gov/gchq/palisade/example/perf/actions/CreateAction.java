/*
 * Copyright 2018 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.palisade.example.perf.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.Util;
import uk.gov.gchq.palisade.example.hrdatagenerator.CreateDataFile;
import uk.gov.gchq.palisade.example.perf.trial.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.util.PerfUtils;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.util.ResourceBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntSupplier;

/**
 * Creates the files needed by other parts of the performance testing application.
 */
public class CreateAction implements IntSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAction.class);

    private final int small;
    private final int large;
    private final String filename;

    public CreateAction(final String filename, final int small, final int large) {
        this.filename = filename;
        this.small = small;
        this.large = large;
    }

    public int getAsInt() {
        // get the sizes and paths
        DirectoryResource directory = (DirectoryResource) ResourceBuilder.create(filename);
        ExecutorService tasks = Executors.newFixedThreadPool(2, Util.createDaemonThreadFactory());

        // make a result writers
        CreateDataFile smallWriter = new CreateDataFile(small, 0, new File(PerfUtils.getSmallFile(directory).getId()));
        CreateDataFile largeWriter = new CreateDataFile(large, 1, new File(PerfUtils.getLargeFile(directory).getId()));

        // submit tasks
        LOGGER.info("Going to create {} records in file {} and {} records in file {} in sub-directory", small, PerfUtils.SMALL_FILE_NAME, large, PerfUtils.LARGE_FILE_NAME);
        Future<Boolean> smallFuture = tasks.submit(smallWriter);
        Future<Boolean> largeFuture = tasks.submit(largeWriter);
        LOGGER.info("Creation tasks submitted...");

        Map.Entry<PerfFileSet, PerfFileSet> fileSet = PerfUtils.getFileSet(directory);

        // wait for completion
        try {
            boolean smallComplete = smallFuture.get();
            LOGGER.info("Small file written successfully: {}", smallComplete);
            File smallFile = new File(fileSet.getKey().smallFile.getId());
            File smallFileNoPolicy = new File(fileSet.getValue().smallFile.getId());
            boolean largeComplete = largeFuture.get();
            LOGGER.info("Large file written successfully: {}", largeComplete);
            File largeFile = new File(fileSet.getKey().largeFile.getId());
            File largeFileNoPolicy = new File(fileSet.getValue().largeFile.getId());

            // copy the files to no policy variants
            LOGGER.info("Copying small file");
            Files.copy(smallFile.toPath(), smallFileNoPolicy.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Copying large file");
            Files.copy(largeFile.toPath(), largeFileNoPolicy.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // indicate success in exit code
            return (smallComplete && largeComplete) ? 0 : 1;
        } catch (ExecutionException | IOException e) {
            LOGGER.error("Exception occurred while running {}", CreateAction.class);
            LOGGER.error("Exception was: ", e);
            return 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            // ensure executor shutdown
            tasks.shutdownNow();
        }
    }
}
