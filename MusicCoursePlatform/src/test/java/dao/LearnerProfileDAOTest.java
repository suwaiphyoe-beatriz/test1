package dao;

import model.LearnerProfile;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LearnerProfileDAOTest {

    private static LearnerProfileDAO learnerProfileDAO;
    private static UserDAO userDAO;
    private static User testUser;
    private static LearnerProfile testProfile;

    @BeforeAll
    static void setUp() {
        learnerProfileDAO = new LearnerProfileDAO();
        userDAO = new UserDAO();
        
        testUser = new User("test_learner_" + System.currentTimeMillis(), 
                           "hashedpassword", 
                           "test_learner_" + System.currentTimeMillis() + "@test.com", 
                           "LEARNER");
        userDAO.create(testUser);
    }

    @AfterAll
    static void tearDown() {
        if (testProfile != null && testProfile.getLearnerProfileId() > 0) {
            learnerProfileDAO.delete(testProfile.getLearnerProfileId());
        }
        if (testUser != null && testUser.getUserId() > 0) {
            userDAO.delete(testUser.getUserId());
        }
    }

    @Test
    @Order(1)
    void testCreate() {
        testProfile = new LearnerProfile(testUser.getUserId(), "Piano");
        
        boolean result = learnerProfileDAO.create(testProfile);
        
        assertTrue(result);
        assertTrue(testProfile.getLearnerProfileId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {
        LearnerProfile found = learnerProfileDAO.findById(testProfile.getLearnerProfileId());
        
        assertNotNull(found);
        assertEquals(testProfile.getLearnerProfileId(), found.getLearnerProfileId());
        assertEquals("Piano", found.getInstrument());
    }

    @Test
    @Order(3)
    void testFindByUserId() {
        LearnerProfile found = learnerProfileDAO.findByUserId(testUser.getUserId());
        
        assertNotNull(found);
        assertEquals(testUser.getUserId(), found.getUserId());
    }

    @Test
    @Order(4)
    void testFindAll() {
        List<LearnerProfile> profiles = learnerProfileDAO.findAll();
        
        assertNotNull(profiles);
        assertTrue(profiles.size() >= 1);
    }

    @Test
    @Order(5)
    void testUpdate() {
        testProfile.setInstrument("Guitar");
        
        boolean result = learnerProfileDAO.update(testProfile);
        
        assertTrue(result);
        
        LearnerProfile updated = learnerProfileDAO.findById(testProfile.getLearnerProfileId());
        assertEquals("Guitar", updated.getInstrument());
    }

    @Test
    @Order(6)
    void testDelete() {
        User tempUser = new User("temp_learner_" + System.currentTimeMillis(), 
                                "hashedpassword", 
                                "temp_learner_" + System.currentTimeMillis() + "@test.com", 
                                "LEARNER");
        userDAO.create(tempUser);
        
        LearnerProfile toDelete = new LearnerProfile(tempUser.getUserId(), "Violin");
        learnerProfileDAO.create(toDelete);
        
        boolean result = learnerProfileDAO.delete(toDelete.getLearnerProfileId());
        
        assertTrue(result);
        assertNull(learnerProfileDAO.findById(toDelete.getLearnerProfileId()));
        
        userDAO.delete(tempUser.getUserId());
    }
}
