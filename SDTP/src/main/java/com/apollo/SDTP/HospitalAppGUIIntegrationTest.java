package com.apollo.SDTP;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class HospitalAppGUIIntegrationTest {

    @Mock
    private RestTemplate restTemplate;

    private HospitalAppGUI hospitalAppGUI;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Set up mock behavior for restTemplate
        when(restTemplate.exchange(eq("http://localhost:8080/f2"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponseEntity());

        // Initialize HospitalAppGUI with mocked restTemplate
        hospitalAppGUI = new HospitalAppGUI();
        hospitalAppGUI.setRestTemplate(restTemplate);
    }

    @Test
    public void testRefreshAdmittedPatients_EmptyResponse() {
        // Call the method to test
        hospitalAppGUI.refreshAdmittedPatients();

        // Assert the behavior when the response is empty
        assertEquals("No currently admitted patients.", hospitalAppGUI.getAdmittedPatientsTextArea().getText());
    }

    // Helper method to create a mock response entity with empty list
    private ResponseEntity mockResponseEntity() {
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }
}
