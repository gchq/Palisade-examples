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
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.util.PerfUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Creates the files needed by other parts of the performance testing application.
 */
public class CreateAction implements IntSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAction.class);

    private final String directoryName;
    private final int small;
    private final int large;
    private final int many;

    public CreateAction(final String directoryName, final int small, final int large, final int many) {
        this.directoryName = directoryName;
        this.small = small;
        this.large = large;
        this.many = many;
    }

    public int getAsInt() {
        // get the sizes and paths
        Path directory = Path.of(directoryName);
        ExecutorService tasks = Executors.newFixedThreadPool(3, Util.createDaemonThreadFactory());

        Map.Entry<PerfFileSet, PerfFileSet> fileSet = PerfUtils.getFileSet(directory);

        Path smallFile = fileSet.getKey().smallFile;
        Path smallFileNoPolicy = fileSet.getValue().smallFile;
        Path largeFile = fileSet.getKey().largeFile;
        Path largeFileNoPolicy = fileSet.getValue().largeFile;
        Path manyDir = fileSet.getKey().manyDir;
        Path manyDirNoPolicy = fileSet.getValue().manyDir;

        // make a result writers
        CreateDataFile smallWriter = new CreateDataFile(small, 0, smallFile.toFile());
        CreateDataFile largeWriter = new CreateDataFile(large, 1, largeFile.toFile());
        Stream<CreateDataFile> manyWriters = IntStream.range(0, many)
                .mapToObj(i -> new CreateDataFile(1, 2L + i, manyDir.resolve(String.format(PerfUtils.MANY_FILE_FORMAT, i)).toFile()));

        // submit tasks
        LOGGER.info("Going to create {} records in file {} and {} records in file {} in sub-directory", small, PerfUtils.SMALL_FILE_NAME, large, PerfUtils.LARGE_FILE_NAME);
        Future<Boolean> smallFuture = tasks.submit(smallWriter);
        Future<Boolean> largeFuture = tasks.submit(largeWriter);
        Stream<Future<Boolean>> manyFutures = manyWriters.map(tasks::submit);
        LOGGER.info("Creation tasks submitted...");

        // wait for completion
        try {
            boolean smallComplete = smallFuture.get();
            LOGGER.info("Small file written successfully: {}", smallComplete);
            boolean largeComplete = largeFuture.get();
            LOGGER.info("Large file written successfully: {}", largeComplete);
            boolean manyComplete = manyFutures.allMatch(future -> {
                try {
                    return future.get();
                } catch (ExecutionException | InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            });
            LOGGER.info("Many files written successfully: {}", manyComplete);

            boolean smallParentCreated = smallFileNoPolicy.toFile().mkdirs();
            if (!smallParentCreated && !smallFileNoPolicy.getParent().toFile().exists()) {
                LOGGER.warn("Failed to create parent directory {}", smallFileNoPolicy);
            }
            boolean largeParentCreated = largeFileNoPolicy.toFile().mkdirs();
            if (!largeParentCreated && !largeFileNoPolicy.getParent().toFile().exists()) {
                LOGGER.warn("Failed to create parent directory {}", largeFileNoPolicy);
            }
            boolean manyParentCreated = manyDirNoPolicy.toFile().mkdirs();
            if (!manyParentCreated && !manyDirNoPolicy.toFile().exists()) {
                LOGGER.warn("Failed to create parent directory {}", manyDirNoPolicy);
            }

            // copy the files to no policy variants
            LOGGER.info("Copying small file");
            Files.copy(smallFile, smallFileNoPolicy, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Copying large file");
            Files.copy(largeFile, largeFileNoPolicy, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Copying many files dir");
            try (Stream<Path> files = Files.walk(manyDir)) {
                files.forEach(file -> {
                    try {
                        Files.copy(file, manyDirNoPolicy.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        LOGGER.error("Exception occurred while copying file {}", file);
                        LOGGER.error("Exception was: ", e);
                        throw new RuntimeException(e);
                    }
                });
            }

            // indicate success in exit code
            return (smallComplete && largeComplete && manyComplete) ? 0 : 1;
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
