package com.chong.han.techtest.services.implementations;

import com.chong.han.techtest.exceptions.InvalidOperationException;
import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import com.chong.han.techtest.services.interfaces.Organisation;
import java.util.ArrayList;

/**
 * Created by CHONG HAN on 15/04/2017.
 */
public class OrganisationManager implements Organisation {

  private final Employee ceo;

  public OrganisationManager(Employee ceo) {
    if (ceo.getLevel() != 1) {
      throw new InvalidOperationException();
    }
    this.ceo = ceo;
  }

  @Override
  public Organisation addEmployee(Employee newEmployee, long managerEmployeeNumber) {

    Employee targetManager = ceo.findEmployee(managerEmployeeNumber);

    if (targetManager == null) {
      throw new InvalidOperationException();
    }

    targetManager.addDirectReport(newEmployee);
    newEmployee.setSuperior(targetManager);

    // when an EMPLOYEE gets a subordinate, he gets promoted to MANAGER
    if (targetManager.getRole() == Role.EMPLOYEE) {
      targetManager.setRole(Role.MANAGER);
    }

    return this;
  }

  @Override
  public Organisation changeTeam(long employeeNumber, long newManagerNumber) {
    Employee newManager = ceo.findEmployee(newManagerNumber);
    Employee employee = ceo.findEmployee(employeeNumber);

    if (employee == null | newManager == null | employee.getRole() == Role.CEO) {
      throw new InvalidOperationException();
    }

    employee.getSuperior().getReports().removeIf(e -> e.getEmployeeNumber() == employeeNumber);

    if (employee.getReports().size() > 0) {
      promoteSeniorSub(employee, employee.getSuperior(), employee.getSeniorReport());
    }

    employee.setReports(new ArrayList<Employee>());
    this.addEmployee(employee, newManagerNumber);

    return null;
  }

  private void promoteSeniorSub(Employee previousManager, Employee newManager, Employee seniorSub) {
    if (seniorSub.getReports().size() == 0) {
      seniorSub.setReports(previousManager.getReports());
      seniorSub.getReports().removeIf(e -> e.getEmployeeNumber() == seniorSub.getEmployeeNumber());
      seniorSub.setSuperior(newManager);
    } else {
      promoteSeniorSub(seniorSub, seniorSub, seniorSub.getSeniorReport());
      for (Employee report : previousManager.getReports()) {
        seniorSub.addDirectReport(report);
        seniorSub.getReports()
            .removeIf(e -> e.getEmployeeNumber() == seniorSub.getEmployeeNumber());
      }
      seniorSub.setSuperior(newManager);
    }

  }

  @Override
  public Organisation goOnHoliday(long employee) {
    return null;
  }

  @Override
  public Organisation comeBackFromHoliday(long employee) {
    return null;
  }

  @Override
  public Organisation promoteEmployee(long employee) {
    return null;
  }

  @Override
  public String vicePresidentDirectorPromotionWaitingListToString() {
    return null;
  }
}
