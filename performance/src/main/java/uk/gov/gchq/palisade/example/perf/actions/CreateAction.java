/*
 * Copyright 2020 Crown Copyright
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

    /**
     * Runner for creating a fake data-set for performance trials
     *
     * @param directoryName directory to store created data files
     * @param small         number of records in the 'small' file
     * @param large         number of records in the 'large' file
     * @param many          number of files in the 'many' directory (each file contains a single record)
     */
    public CreateAction(final String directoryName, final int small, final int large, final int many) {
        this.directoryName = directoryName;
        this.small = small;
        this.large = large;
        this.many = many;
    }

    /**
     * Create a number of employee avro files in the with-policy directory
     *
     * @param fileSet parameters for file and directory names
     * @return whether the operations completed successfully
     */
    private boolean createWithPolicyDataset(final Map.Entry<PerfFileSet, PerfFileSet> fileSet) {
        ExecutorService tasks = Executors.newFixedThreadPool(3, Util.createDaemonThreadFactory());

        Path smallFile = fileSet.getKey().smallFile;
        Path largeFile = fileSet.getKey().largeFile;
        Path manyDir = fileSet.getKey().manyDir;

        // make a result writers
        CreateDataFile smallWriter = new CreateDataFile(small, 0, smallFile.toFile());
        CreateDataFile largeWriter = new CreateDataFile(large, 1, largeFile.toFile());
        Stream<CreateDataFile> manyWriters = IntStream.range(0, many)
                .mapToObj(i -> new CreateDataFile(1, 2L + i, manyDir.resolve(String.format(PerfUtils.MANY_FILE_FORMAT, i)).toFile()));

        // submit tasks
        LOGGER.info("Going to create {} records in file {}, {} records in file {} and {} resources in {}", small, PerfUtils.SMALL_FILE_NAME, large, PerfUtils.LARGE_FILE_NAME, many, PerfUtils.MANY_FILE_DIR);
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
            return true;
        } catch (ExecutionException e) {
            LOGGER.error("Exception occurred while creating with-policy data", e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            // ensure executor shutdown
            tasks.shutdownNow();
        }
    }

    /**
     * Copy a number of employee avro files in the with-policy directory to the no-policy directory
     *
     * @param fileSet parameters for file and directory names
     * @return whether the operations completed successfully
     */
    private boolean copyNoPolicyDataset(final Map.Entry<PerfFileSet, PerfFileSet> fileSet) {
        Path smallFile = fileSet.getKey().smallFile;
        Path largeFile = fileSet.getKey().largeFile;
        Path manyDir = fileSet.getKey().manyDir;

        Path smallFileNoPolicy = fileSet.getValue().smallFile;
        Path largeFileNoPolicy = fileSet.getValue().largeFile;
        Path manyDirNoPolicy = fileSet.getValue().manyDir;

        try {
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
            return true;
        } catch (IOException e) {
            LOGGER.error("Exception occurred while copying no-policy data", e);
            return false;
        }
    }

    /**
     * Run this action and return a error/success code
     * First creates a dataset in the with-policy directory, then copies it to the no-policy directory
     *
     * @return 0 if completed successfully, error code otherwise
     */
    public int getAsInt() {
        // get the sizes and paths
        Path directory = Path.of(directoryName);
        Map.Entry<PerfFileSet, PerfFileSet> fileSet = PerfUtils.getFileSet(directory);

        boolean createSuccess = createWithPolicyDataset(fileSet);
        boolean copySuccess = false;
        if (createSuccess) {
            copySuccess = copyNoPolicyDataset(fileSet);
        }

        return createSuccess && copySuccess ? 0 : 1;
    }
}
