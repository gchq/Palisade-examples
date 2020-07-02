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
import uk.gov.gchq.palisade.example.perf.util.PerfException;
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
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Creates the files needed by other parts of the performance testing application.
 */
public class CreateAction implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAction.class);

    private final String directoryName;
    private final long small;
    private final int large;
    private final int manyUnique;
    private final int manyDuplicates;

    /**
     * Runner for creating a fake data-set for performance trials
     *
     * @param directoryName  directory to store created data files
     * @param small          number of records in the 'small' file
     * @param large          number of records in the 'large' file
     * @param manyUnique     number of unique files in the 'many' directory (each file contains a single record)
     * @param manyDuplicates number of duplicates to make of the unique dataset (for performance reasons)
     */
    public CreateAction(final String directoryName, final int small, final int large, final int manyUnique, final int manyDuplicates) {
        if (manyUnique < 1 || manyDuplicates < 1) {
            throw new IllegalArgumentException("Number of created 'many' resources must be strictly positive");
        }
        this.directoryName = directoryName;
        this.small = small;
        this.large = large;
        this.manyUnique = manyUnique;
        this.manyDuplicates = manyDuplicates;
    }

    /**
     * Copy a number of employee avro files in the with-policy directory to the no-policy directory
     *
     * @param fileSet parameters for file and directory names
     * @return whether the operations completed successfully
     */
    private static boolean copyNoPolicyDataset(final Map.Entry<PerfFileSet, PerfFileSet> fileSet) {
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
                files.forEach((Path file) -> {
                    try {
                        Files.copy(
                                file,
                                manyDirNoPolicy.resolve(file.getFileName()),
                                StandardCopyOption.REPLACE_EXISTING
                        );
                    } catch (IOException e) {
                        LOGGER.error("Exception occurred while copying file {}", file);
                        LOGGER.error("Exception was: ", e);
                        throw new PerfException(e);
                    }
                });
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Exception occurred while copying no-policy data", e);
            return false;
        }
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
        Path masterCopy = manyDir.resolve(String.format(PerfUtils.MANY_DUP_DIR_FORMAT, 0));

        // make result writers
        CreateDataFile smallWriter = new CreateDataFile(small, 0, smallFile.toFile());
        CreateDataFile largeWriter = new CreateDataFile(large, 1, largeFile.toFile());
        // stream of writers for many files
        long seedFrom = 2L;
        Stream<CreateDataFile> manyWriters = LongStream.range(0, manyUnique)
                .mapToObj((long i) -> new CreateDataFile(
                        1,
                        seedFrom + i,
                        masterCopy
                                .resolve(String.format(PerfUtils.MANY_FILE_FORMAT, i)).toFile()
                ));

        // submit tasks
        LOGGER.info("Going to create {} records in file {}, {} records in file {}", small, PerfUtils.SMALL_FILE_NAME, large, PerfUtils.LARGE_FILE_NAME);
        LOGGER.info("Going to create {} records in directory {}, then duplicate {} times", manyUnique, masterCopy, manyDuplicates);
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
            boolean manyComplete = manyFutures.allMatch((Future<Boolean> future) -> {
                try {
                    return future.get();
                } catch (ExecutionException | InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            });
            LOGGER.info("Many files written successfully: {}", manyComplete);

            LOGGER.info("Duplicating many files");
            LongStream.range(0, manyDuplicates)
                    .forEach((long i) -> {
                        try {
                            Files.copy(
                                    masterCopy,
                                    masterCopy.resolveSibling(String.format(PerfUtils.MANY_DUP_DIR_FORMAT, i)),
                                    StandardCopyOption.REPLACE_EXISTING
                            );
                        } catch (IOException e) {
                            throw new PerfException(e);
                        }
                    });
            return true;
        } catch (ExecutionException e) {
            LOGGER.error("Exception occurred while creating with-policy data", e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PerfException(e);
        } finally {
            // ensure executor shutdown
            tasks.shutdownNow();
        }
    }

    /**
     * Run this action, throw a runtime exception if there were errors
     * First creates a dataset in the with-policy directory, then copies it to the no-policy directory
     *
     * @throws PerfException if an error occurred
     */
    public void run() {
        // get the sizes and paths
        Path directory = Path.of(directoryName);
        Map.Entry<PerfFileSet, PerfFileSet> fileSet = PerfUtils.getFileSet(directory);

        if (createWithPolicyDataset(fileSet)) {
            LOGGER.info("Successfully created with-policy dataset, copying to no-policy");
        } else {
            throw new PerfException(new IOException("Failed to create with-policy dataset"));
        }

        if (copyNoPolicyDataset(fileSet)) {
            LOGGER.info("Successfully copied no-policy dataset");
        } else {
            throw new PerfException(new IOException("Failed to copy no-policy dataset"));
        }
    }
}
