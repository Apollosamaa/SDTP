package com.apollo.SDTP;

import com.apollo.SDTP.model.Admission;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HospitalAppGUI extends JFrame {
    private JTextArea admittedPatientsTextArea;
    private JButton refreshButton;

    private RestTemplate restTemplate;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Constructor
    public HospitalAppGUI() {
        setTitle("Hospital Admitted Patients");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize RestTemplate
        restTemplate = new RestTemplate();

        // Initialize components
        admittedPatientsTextArea = new JTextArea();
        admittedPatientsTextArea.setEditable(false);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAdmittedPatients();
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(admittedPatientsTextArea), BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
    }

    // Method to refresh admitted patients
    // Method to refresh admitted patients
    void refreshAdmittedPatients() {
        // Call the /f2 endpoint to get currently admitted patients
        ResponseEntity<List<Admission>> response = restTemplate.exchange(
                "http://localhost:8080/f2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Admission>>() {});

        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            List<Admission> currentlyAdmittedPatients = response.getBody();

            if (currentlyAdmittedPatients.isEmpty()) {
                admittedPatientsTextArea.setText("No currently admitted patients.");
            } else {
                // Update the text area with admitted patients' information
                StringBuilder sb = new StringBuilder();
                sb.append("Currently Admitted Patients:\n");
                for (Admission admission : currentlyAdmittedPatients) {
                    sb.append("Admission ID: ").append(admission.getId()).append("\n");
                    sb.append("Admission Date: ").append(admission.getAdmissionDate()).append("\n");
                    sb.append("Discharge Date: ").append(admission.getDischargeDate()).append("\n");
                    sb.append("Patient ID: ").append(admission.getPatientID()).append("\n");
                    sb.append("-------------------------------------\n");
                }
                admittedPatientsTextArea.setText(sb.toString());
            }
        } else {
            admittedPatientsTextArea.setText("Failed to retrieve currently admitted patients.");
        }
    }


    // Getter method for admittedPatientsTextArea
    public JTextArea getAdmittedPatientsTextArea() {
        return admittedPatientsTextArea;
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HospitalAppGUI gui = new HospitalAppGUI();
                gui.setVisible(true);
            }
        });
    }
}
