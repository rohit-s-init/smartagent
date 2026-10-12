package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import dto.LeadDTO;

public class LeadRepositoryTest {

    @Test()
    public void testGetLeadById() {

        // testing the get method for existing phone number
        LeadRepository leadRepository = new LeadRepository();

        LeadDTO lead = leadRepository.getLeadByPhoneNumber("9999999991");

        assertNotEquals(0, lead.getId());
    }

    @Test
    public void testGetLeadByPhoneNumberNew() {

        // testing the get method for new phone number
        LeadRepository leadRepository = new LeadRepository();

        LeadDTO lead = leadRepository.getLeadByPhoneNumber("8459781390");

        assertNotEquals(0, lead.getId());

        leadRepository.deleteByPhoneNumber("8459781390");

    }

    @Test 
    public void updateLeadTest() {
        LeadRepository leadRepository = new LeadRepository();

        LeadDTO lead = leadRepository.getLeadByPhoneNumber("8459781390");

        lead.setName("Updated Name Rohit");
        lead.setLocation("Updated Location Pune new");
        lead.setSchooling("Updated Schooling done school");

        leadRepository.update(lead);

        LeadDTO updatedLead = leadRepository.getLeadByPhoneNumber("8459781390");

        assertEquals(lead.getName(), updatedLead.getName());
        assertEquals(lead.getLocation(), updatedLead.getLocation());
        assertEquals(lead.getSchooling(), updatedLead.getSchooling());

        leadRepository.deleteByPhoneNumber("9999999992");

    }

}
