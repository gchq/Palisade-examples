package uk.gov.gchq.palisade.example.configurator;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.resource.ChildResource;
import uk.gov.gchq.palisade.resource.ParentResource;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SystemResource;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class ExampleConfiguratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfiguratorTest.class);

    @Test
    public void createFileResourceTest() {
        // Given
        final Path pom = Path.of(System.getProperty("user.dir")).resolve("pom.xml");
        LOGGER.info("Testing createFileResource and resolveParents for file: {}", pom);

        // When
        FileResource fileResource = ExampleConfigurator.createFileResource(pom.toString());

        // Then
        LinkedList<Resource> parents = getAllParents(fileResource);
        // System at the 'top'
        assertThat(parents.getFirst(), Matchers.instanceOf(SystemResource.class));
        parents.removeFirst();
        // File at the 'bottom'
        assertThat(parents.getLast(), Matchers.instanceOf(FileResource.class));
        parents.removeLast();
        // Directories in the 'middle'
        parents.forEach(resource -> assertThat(resource, Matchers.instanceOf(DirectoryResource.class)));
    }

    private LinkedList<Resource> getAllParents(Resource resource) {
        if (resource instanceof ChildResource) {
            LinkedList<Resource> parents = getAllParents(((ChildResource) resource).getParent());
            parents.addLast(resource);
            LOGGER.debug("Add {}", resource);
            return parents;
        } else {
            LOGGER.debug("Top {}", resource);
            return new LinkedList<>(Collections.singleton(resource));
        }
    }
}
