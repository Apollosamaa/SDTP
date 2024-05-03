package com.apollo.SDTP;

import com.apollo.SDTP.controller.AdmissionsController;
import com.apollo.SDTP.model.Admission;
import com.apollo.SDTP.model.Allocation;
import com.apollo.SDTP.model.Employee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class AdmissionsControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AdmissionsController admissionsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Unit testing for F1 - getAdmissionsByPatient
    @Test
    public void testGetAdmissionsByPatient_Success() {
        // Mocked response data
        Admission mockAdmission = new Admission();
        mockAdmission.setId(2); // Set the ID to match the actual data
        mockAdmission.setAdmissionDate("2020-12-07T22:14:00");
        mockAdmission.setDischargeDate("0001-01-01T00:00:00");
        mockAdmission.setPatientID(1);

        // Mock the RestTemplate behavior
        when(restTemplate.getForObject(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions"), eq(Admission[].class)))
                .thenReturn(new Admission[]{mockAdmission});

        // Call the method to test
        ResponseEntity<List<Admission>> response = admissionsController.getAdmissionsByPatient(1);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Admission> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        Admission actualAdmission = responseBody.get(0);
        assertEquals(mockAdmission.getId(), actualAdmission.getId());
        assertEquals(mockAdmission.getAdmissionDate(), actualAdmission.getAdmissionDate());
        assertEquals(mockAdmission.getDischargeDate(), actualAdmission.getDischargeDate());
        assertEquals(mockAdmission.getPatientID(), actualAdmission.getPatientID());
    }

    // Unit testing for F2 - getCurrentlyAdmittedPatients
    @Test
    public void testGetCurrentAdmittedPatients_Success() {
        // Mocked response data
        List<Admission> mockAdmissions = Arrays.asList(new Admission());

        // Mock the RestTemplate behavior
        when(restTemplate.getForObject(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions"), eq(Admission[].class)))
                .thenReturn(mockAdmissions.toArray(new Admission[0]));

        // Call the method to test
        ResponseEntity<List<Admission>> response = admissionsController.getCurrentlyAdmittedPatients();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAdmissions, response.getBody());
    }

    // Unit testing for F3 - getStaffWithMostAdmissions
    @Test
    public void testGetStaffWithMostAdmissions_Success() throws ParseException {
        // Mocked response data for allocations and employees
        Allocation[] mockAllocations = {new Allocation(1, 1,4), new Allocation(2, 1,4), new Allocation(3, 2,2)};
        Employee[] mockEmployees = {new Employee(1, "John", "Doe"), new Employee(2, "Jane", "Smith"),
                new Employee(3, "Alice", "Jones"), new Employee(4, "Sarah", "Jones")};

        // Mock the RestTemplate behavior to return the mock data
        when(restTemplate.getForObject(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations"), eq(Allocation[].class)))
                .thenReturn(mockAllocations);
        when(restTemplate.getForObject(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Employees"), eq(Employee[].class)))
                .thenReturn(mockEmployees);

        // Call the method to test
        ResponseEntity<String> response = admissionsController.getStaffWithMostAdmissions();

        // Print the actual response for debugging
        System.out.println("Actual Response: " + response.getBody());

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Staff member with the most admissions: ID 4, Name: Jones Sarah"));
    }

    // Unit testing for F4 - getStaffWithNoAdmissions
    @Test
    public void testGetStaffWithNoAdmissions_Success() throws ParseException {
        // Mocked response data for allocations and employees
        Allocation[] mockAllocations = {new Allocation(1, 1,2), new Allocation(2, 1,3), new Allocation(3, 2,2)};
        Employee[] mockEmployees = {new Employee(1, "John", "Doe"), new Employee(2, "Jane", "Smith"), new Employee(3, "Alice", "Jones")};

        // Mock the RestTemplate behavior
        when(restTemplate.getForObject(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations"), eq(Allocation[].class)))
                .thenReturn(mockAllocations);
        when(restTemplate.getForObject(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Employees"), eq(Employee[].class)))
                .thenReturn(mockEmployees);

        // Call the method to test
        ResponseEntity<List<Employee>> response = admissionsController.getStaffWithNoAdmissions();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Employee> staffWithNoAdmissions = response.getBody();
        assertNotNull(staffWithNoAdmissions);
        assertEquals(1, staffWithNoAdmissions.size());
        assertEquals("Doe", staffWithNoAdmissions.get(0).getForename());
        assertEquals("John", staffWithNoAdmissions.get(0).getSurname());
    }
}
