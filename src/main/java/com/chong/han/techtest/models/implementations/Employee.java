package com.chong.han.techtest.models.implementations;

import com.chong.han.techtest.exceptions.InvalidOperationException;
import com.chong.han.techtest.models.interfaces.Personnel;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by CHONG HAN on 14/04/2017.
 */
public class Employee implements Personnel {

  static final Comparator<Employee> StartDateComparator = new Comparator<Employee>() {
    @Override
    public int compare(Employee employee1, Employee employee2) {
      LocalDate date1 = employee1.startDate;
      LocalDate date2 = employee2.startDate;
      return date1.compareTo(date2);
    }
  };
  private final long employeeNumber;
  private final LocalDate startDate;
  private String firstname;
  private String lastname;
  private Role role;
  private boolean holiday = false;
  private Collection<Employee> reports = new CopyOnWriteArrayList<Employee>();
  private Employee superior;


  public Employee(long employeeNumber, String firstname, String lastname, Role role,
      LocalDate startDate) {
    setFirstname(firstname);
    setLastname(lastname);
    setRole(role);
    this.startDate = startDate;
    this.employeeNumber = employeeNumber;
  }

  public long getEmployeeNumber() {
    return employeeNumber;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public Collection<Employee> getReports() {
    return reports;
  }

  public void setReports(Collection<Employee> reports) {
    this.reports = reports;
  }

  public Employee getSuperior() {
    if (this.role == Role.CEO) {
      throw new InvalidOperationException();
    }
    return superior;
  }

  public void setSuperior(Employee superior) {
    if (this.role == Role.CEO) {
      throw new InvalidOperationException();
    }
    this.superior = superior;
  }

  public Employee getSeniorReport() {
    Collections.sort((CopyOnWriteArrayList) this.reports, StartDateComparator);

    return getReports().iterator().next();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Employee employee = (Employee) o;

    return employeeNumber == employee.employeeNumber;
  }

  @Override
  public int hashCode() {
    return (int) (employeeNumber ^ (employeeNumber >>> 32));
  }

  public Employee findEmployee(long employeeNumber) {
    if (this.getEmployeeNumber() == employeeNumber) {
      return this;
    } else if (this.reports.size() > 0) {
      for (Employee report : reports) {
        Employee temp = report.findEmployee(employeeNumber);
        if (temp != null) {
          return temp;
        }
      }
    }
    return null;
  }

  public boolean isHoliday() {
    return holiday;
  }

  public void setHoliday(boolean holiday) {
    this.holiday = holiday;
  }

  @Override
  public Employee addDirectReport(Personnel directReport) {
    reports.add((Employee) directReport);
    return this;
  }

  @Override
  public Employee removeDirectReport(long employeeNumber) {
    if (reports.removeIf(e -> e.getEmployeeNumber() == employeeNumber)) {
      return this;
    }
    throw new InvalidOperationException();
  }

  @Override
  public int getLevel() {
    if (role == Role.CEO) {
      return 1;
    }
    return this.getSuperior().getLevel() + 1;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getEmployeeNumber() + ", ");
    sb.append(getFirstname() + getLastname() + ", ");
    sb.append(getRole() + ", ");
    sb.append(getStartDate());
    sb.append("\n");
    sb.append("reports: ");
    if (getReports().size() > 0) {
      for (Employee e : reports) {
        sb.append(e.getEmployeeNumber() + ", ");
      }
    }
    return sb.toString();
  }
}
