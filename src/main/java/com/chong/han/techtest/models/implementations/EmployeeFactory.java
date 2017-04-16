package com.chong.han.techtest.models.implementations;

import java.time.LocalDate;

/**
 * Created by CHONG HAN on 14/04/2017.
 */
public class EmployeeFactory {

  private long employeeNumber; // Unique id

  public EmployeeFactory() {
    employeeNumber = 0;
  }

  public Employee createEmployee(String firstName, String lastName, Role role, LocalDate startDate) {
    return new Employee(employeeNumber++, firstName, lastName, role, startDate);
  }

}
