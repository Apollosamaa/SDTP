package com.apollo.SDTP.controller;

import com.apollo.SDTP.model.Admission;
import com.apollo.SDTP.model.Allocation;
import com.apollo.SDTP.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AdmissionsController {
    @Autowired
    private RestTemplate restTemplate;
    public AdmissionsController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    //f1
    @GetMapping("/f1/{patientId}")
    public ResponseEntity<List<Admission>> getAdmissionsByPatient(@PathVariable("patientId") int patientId) {
        String url = "https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions";

        // Fetch all admissions
        Admission[] allAdmissions = restTemplate.getForObject(url, Admission[].class);

        List<Admission> filteredAdmissions = new ArrayList<>();
        for (Admission admission : allAdmissions) {
            if (admission.getPatientID() == patientId) {
                filteredAdmissions.add(admission);
            }
        }

        return ResponseEntity.ok(filteredAdmissions);
    }

    //f2
    @GetMapping("/f2")
    public ResponseEntity<List<Admission>> getCurrentlyAdmittedPatients() {
        String url = "https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions";
        Admission[] allAdmissions = restTemplate.getForObject(url, Admission[].class);

        List<Admission> admittedPatients = new ArrayList<>();
        for (Admission admission : allAdmissions) {
            // Check for discharge date with special value (can be adapted based on your model)
            if (admission.getDischargeDate() == null || admission.getDischargeDate().equals("0001-01-01T00:00:00")) {
                admittedPatients.add(admission);
            }
        }

        return ResponseEntity.ok(admittedPatients);
    }

    //f3
    @GetMapping("/f3")
    public ResponseEntity<String> getStaffWithMostAdmissions() throws ParseException {
        String allocationsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations";
        String employeesUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Employees"; // Assuming employees endpoint

        Allocation[] allAllocations = restTemplate.getForObject(allocationsUrl, Allocation[].class);
        Employee[] allEmployees = restTemplate.getForObject(employeesUrl, Employee[].class); // Assuming employees endpoint

        // Group allocations by employee ID and count occurrences
        Map<Integer, Long> allocationCountByStaff = Arrays.stream(allAllocations)
                .collect(Collectors.groupingBy(Allocation::getEmployeeID, Collectors.counting()));

        // Find employee ID with the most allocations
        int employeeWithMostAllocations = allocationCountByStaff.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0); // Handle no allocations case (optional)

        String message;
        if (employeeWithMostAllocations > 0) {
            // Find staff name using employee ID
            Employee staffMember = Arrays.stream(allEmployees)
                    .filter(employee -> employee.getId() == employeeWithMostAllocations)
                    .findFirst()
                    .orElse(null); // Handle missing employee data (optional)

            if (staffMember != null) {
                message = "Staff member with the most admissions: ID " + employeeWithMostAllocations +
                        ", Name: " + staffMember.getForename() + " " + staffMember.getSurname() + "\n" +
                        "Allocations:\n";

                // Filter allocations for the identified staff member
                List<Allocation> staffAllocations = Arrays.stream(allAllocations)
                        .filter(allocation -> allocation.getEmployeeID() == employeeWithMostAllocations)
                        .collect(Collectors.toList());

                // Append allocation details
                for (Allocation allocation : staffAllocations) {
                    // Format allocation details as needed
                    String allocationDetails = "- Admission ID: " + allocation.getAdmissionID() +
                            ", Start Time: " + allocation.getStartTime() +
                            ", End Time: " + allocation.getEndTime() + "\n";
                    message += allocationDetails;
                }
            } else {
                message = "Staff member with ID " + employeeWithMostAllocations + " not found in employee data.";
            }
        } else {
            message = "No allocations data found";
        }

        return ResponseEntity.ok(message);
    }

    //f4
    @GetMapping("/f4")
    public ResponseEntity<List<Employee>> getStaffWithNoAdmissions() throws ParseException {
        String allocationsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations";
        String employeesUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Employees"; // Assuming employees endpoint

        // Fetch all allocations and employees data
        Allocation[] allAllocations = restTemplate.getForObject(allocationsUrl, Allocation[].class);
        Employee[] allEmployees = restTemplate.getForObject(employeesUrl, Employee[].class); // Assuming employees endpoint

        // Convert allocations to a set of employee IDs for efficient lookup
        Set<Integer> allocationEmployeeIds = Arrays.stream(allAllocations)
                .map(Allocation::getEmployeeID)
                .collect(Collectors.toSet());

        // Filter employees who have no allocation IDs (i.e., no admissions)
        List<Employee> staffWithNoAdmissions = Arrays.stream(allEmployees)
                .filter(employee -> !allocationEmployeeIds.contains(employee.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(staffWithNoAdmissions);
    }

}
