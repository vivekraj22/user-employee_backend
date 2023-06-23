package com.finolitech.model;

public class Employee {

    private static int counter = 0;

    private int empId;

    private int userId;

    private String role;

    private int salary;

    public Employee() {
        this.empId = ++counter;
    }

    public Employee(int userId, String role, int salary) {
        this.empId = ++counter;
        this.userId = userId;
        this.role = role;
        this.salary = salary;
    }

    public Employee(int empId, int userId, String role, int salary) {
        this.empId = empId;
        this.userId = userId;
        this.role = role;
        this.salary = salary;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

}