package com.apollo.SDTP.controller;

import com.apollo.SDTP.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class EmployeesController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getEmployees() {
        String url = "https://web.socem.plymouth.ac.uk/COMP2005/api/Employees"; // Assuming employees endpoint
        Employee[] allEmployees = restTemplate.getForObject(url, Employee[].class);
        return ResponseEntity.ok(Arrays.asList(allEmployees));
    }
}
