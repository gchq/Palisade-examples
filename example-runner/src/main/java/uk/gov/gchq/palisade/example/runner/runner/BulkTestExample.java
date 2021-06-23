/*
 * Copyright 2019 Crown Copyright
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

package uk.gov.gchq.palisade.example.runner.runner;

import akka.Done;
import akka.stream.javadsl.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.runner.config.AkkaClientWrapper;
import uk.gov.gchq.palisade.example.runner.config.BulkConfiguration;
import uk.gov.gchq.syntheticdatagenerator.CreateData;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * A class to test if the Palisade data path can handle retrieving many thousands of resources in a single request. This class
 * will create the given number of resources in the examples/resources/data directory and then try to retrieve them.
 */
public final class BulkTestExample implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTestExample.class);
    private static final Integer NUMBER_OF_EMPLOYEES = 10;

    /**
     * Set by the destruct method to ensure this doesn't happen via a shutdown thread as well.
     */
    private static final AtomicBoolean HAS_DESTRUCTION_OCCURRED = new AtomicBoolean(false);

    private final BulkConfiguration configuration;
    private final AkkaClientWrapper<Employee> client;

    /**
     * Default constructor with 2 arguments
     *
     * @param configuration the configuration details
     * @param client the akka client being used
     */
    public BulkTestExample(final BulkConfiguration configuration, final AkkaClientWrapper<Employee> client) {
        this.configuration = configuration;
        this.client = client;
    }

    /**
     * Ensures that the removal/restoration of the original data directory occurs if the VM is closed. This intercepts
     * things like SIGTERM (Ctrl-C) events, but not abnormal termination.
     *
     * @param shouldDelete whether the deletion should occur at all
     * @param directory    the directory path for the original data
     */
    private static void configureShutdownHook(final boolean shouldDelete, final String directory) {
        //register shutdown hook in case someone tries to terminate the VM gracefully
        if (shouldDelete) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    removeBulkData(directory);
                } catch (IOException e) {
                    LOGGER.error("Exception on shutdown hook", e);
                }
            }));
        }
    }

    /**
     * Remove the generated directory and replace the original one.
     *
     * @param directory the original directory
     * @throws IOException for any filesystem errors
     */
    private static void removeBulkData(final String directory) throws IOException {
        if (HAS_DESTRUCTION_OCCURRED.compareAndSet(false, true)) {
            Path dir = Paths.get(directory);
            Path newLocation = generateNewDirectoryName(dir);

            //remove existing files
            try (Stream<Path> paths = Files.list(dir)) {
                paths.forEach((Path path) -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            //remove original
            Files.deleteIfExists(dir);
            LOGGER.info("Deleted {}", dir);
            //copy back
            Files.move(newLocation, dir);
            LOGGER.info("Moved {} to {}", newLocation, dir);
        } else {
            LOGGER.info("Directory already deleted");
        }
    }

    /**
     * Create the bulk data directory. This will move any existing directory to a different name by adding ".spare" to the
     * end of the name.
     *
     * @throws IOException for any file system error
     */
    private void createBulkData() throws IOException {
        Path dir = Paths.get(configuration.getDirectory());
        Path newLocation = generateNewDirectoryName(dir);
        if (Files.exists(dir) && !Files.isDirectory(dir)) {
            throw new IllegalArgumentException(configuration.getDirectory() + " is not a directory");
        }
        moveDataDir(dir, newLocation);

        //call the HR Data generator
        CreateData.main(configuration.getDirectory(), NUMBER_OF_EMPLOYEES.toString(), configuration.getQuantity().toString());

        //check for existence of the file we need
        Path startFile = dir.resolve("employee_file0.avro");
        if (!Files.exists(startFile)) {
            throw new IOException("Creation of employee file failed, couldn't find file " + startFile);
        }

        //copy the files out n times
        cloneFiles(configuration.getCopies(), startFile);
    }

    /**
     * Creates the path for the backup of the data directory.
     *
     * @param dir the data directory for the files
     * @return the new path
     */
    private static Path generateNewDirectoryName(final Path dir) {
        return dir.resolveSibling(dir.getFileName() + ".spare");
    }

    /**
     * Copy the given file a number of times in the same directory.
     *
     * @param numCopies    the number of copies of the file needed
     * @param originalFile the file being copied
     * @throws IOException for any IO errors
     */
    private static void cloneFiles(final int numCopies, final Path originalFile) throws IOException {
        if (numCopies < 0) {
            throw new IllegalArgumentException("Can't have fewer than 0 copies");
        }
        //now copy that out as many times as necessary
        for (int i = 1; i < numCopies; i++) {
            Path newFile = originalFile.resolveSibling("employee_file" + i + ".avro");
            Files.copy(originalFile, newFile);
            if (i % 10 == 0) {
                LOGGER.info("Wrote {}", newFile);
            }
        }
        LOGGER.info("Done");
    }

    /**
     * Move the original directory to a new place and recreate the original one.
     *
     * @param source      the directory to move
     * @param newLocation the renamed location
     * @throws IOException for any file system error
     */
    private static void moveDataDir(final Path source, final Path newLocation) throws IOException {
        //move it
        if (Files.exists(source)) {
            //new location
            LOGGER.info("Moving {} to {}", source, newLocation);
            Files.move(source, newLocation, StandardCopyOption.ATOMIC_MOVE);
        }

        //make the directory
        LOGGER.info("Create directory {}", source);
        Files.createDirectory(source);
    }

    /**
     * The runner method to run a test of how many resources/files can be read in a single request
     *
     * @param args command-line arguments
     * @throws IOException for any file system error
     */
    public void run(final String[] args) throws IOException {
        // Ensure we clean up if a SIGTERM occurs
        configureShutdownHook(configuration.isShouldDelete(), configuration.getDirectory());

        // Create some bulk data (unless flag set)
        try {
            if (configuration.isShouldCreate()) {
                createBulkData();
            }

            // Run example
            client.<CompletionStage<Done>>execute("Alice", configuration.getDirectory(), Purpose.SALARY.name())
                    .apply(Sink.foreach(record -> LOGGER.info("{}", record)))
                    .toCompletableFuture().join();

        } finally {
            if (configuration.isShouldDelete()) {
                removeBulkData(configuration.getDirectory());
            }
        }

        System.exit(0);
    }
}
