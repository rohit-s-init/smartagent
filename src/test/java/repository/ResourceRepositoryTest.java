package repository;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import dto.ResourceDTO;

public class ResourceRepositoryTest {
    
    @Test 
    public void testGetResourceById() {
        ResourceRepository resourceRepository = new ResourceRepository();
        ResourceDTO resource = resourceRepository.getResourceById(9L);
        assertNotEquals(0, resource.getId());
        assertNotNull(resource.getTitle());
    
    }

}
