package com.apollo.SDTP.model;

public class Allocation {
    private int id;
    private int admissionID;
    private int employeeID;
    private String startTime;
    private String endTime;

    public Allocation(int id, int admissionID, int employeeID) {
        this.id = id;
        this.admissionID = admissionID;
        this.employeeID = employeeID;
    }

    public int getId() {
        return id;
    }

    public int getAdmissionID() {
        return admissionID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
