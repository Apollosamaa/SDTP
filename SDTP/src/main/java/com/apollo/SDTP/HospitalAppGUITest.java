package com.apollo.SDTP;

import com.apollo.SDTP.model.Admission;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class HospitalAppGUITest {

    @Mock
    private RestTemplate restTemplate;

    private HospitalAppGUI hospitalAppGUI;

    @Before
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Create an instance of HospitalAppGUI
        hospitalAppGUI = new HospitalAppGUI();

        // Set the mocked RestTemplate
        hospitalAppGUI.setRestTemplate(restTemplate);
    }

    // Unit testing
    @Test
    public void testRefreshAdmittedPatients_Success() {
        // Prepare mock response
        List<Admission> mockAdmissions = new ArrayList<>();
        Admission admission = new Admission();
        // Set up admission data
        // For brevity, admission data setup is omitted
        mockAdmissions.add(admission);
        ResponseEntity<List<Admission>> mockResponseEntity = new ResponseEntity<>(mockAdmissions, HttpStatus.OK);

        // Set up mock behavior for the RestTemplate exchange method
        when(restTemplate.exchange(eq("http://localhost:8080/f2"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponseEntity);

        // Call the method to test
        hospitalAppGUI.refreshAdmittedPatients();
    }

    // Edge Testing
    // Test if list was empty
    @Test
    public void testRefreshAdmittedPatients_EmptyResponse() {
        // Prepare mock response with an empty list
        List<Admission> emptyList = Collections.emptyList();
        ResponseEntity<List<Admission>> mockResponseEntity = new ResponseEntity<>(emptyList, HttpStatus.OK);

        // Set up mock behavior
        when(restTemplate.exchange(eq("http://localhost:8080/f2"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponseEntity);

        // Call the method to test
        hospitalAppGUI.refreshAdmittedPatients();

        // Assert that the text area displays a message indicating no admissions
        assertEquals("No currently admitted patients.", hospitalAppGUI.getAdmittedPatientsTextArea().getText());
    }

    // Test if list was overloaded
    @Test
    public void testRefreshAdmittedPatients_OverloadedResponse() {
        // Create a list of Admission objects with a large number of entries
        List<Admission> mockAdmissions = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            mockAdmissions.add(new Admission());
        }

        // Prepare mock response with the large list of admissions
        ResponseEntity<List<Admission>> mockResponseEntity = new ResponseEntity<>(mockAdmissions, HttpStatus.OK);

        // Set up mock behavior
        when(restTemplate.exchange(eq("http://localhost:8080/f2"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponseEntity);

        // Call the method to test
        hospitalAppGUI.refreshAdmittedPatients();
    }


}
