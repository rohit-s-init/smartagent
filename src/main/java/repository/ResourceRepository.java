package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dto.ResourceDTO;
import model.Resource;

public class ResourceRepository {

    private SessionFactory sessionFactory;
    
    public ResourceRepository() {
        this.sessionFactory = config.DatabaseConfig.getSessionFactory();
    }
    

    public ResourceDTO getResourceById(Long id) {
        Session session = sessionFactory.openSession();
        Resource resource = session.get(Resource.class, id);
        session.close();
        return new ResourceDTO(resource.getId(), resource.getCategory(), resource.getTitle(), resource.getDescription());   
    }
}
