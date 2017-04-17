package com.chong.han.techtest.services.implementations;

import com.chong.han.techtest.exceptions.InvalidOperationException;
import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import com.chong.han.techtest.services.interfaces.Organisation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

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

    employee.getSuperior().removeDirectReport(employeeNumber); // remove reference from exManager

    if (employee.getReports().size() > 0) {
      promoteSeniorSub(employee, employee.getSuperior(), employee.getSeniorReport());

      // seniorSub now takeover the old role and report to previous boss
      employee.getSuperior().addDirectReport(employee.getSeniorReport());
      employee.getSeniorReport().setSuperior(employee.getSuperior());
      employee.setReports(new CopyOnWriteArrayList<>());
    }
    this.addEmployee(employee, newManagerNumber);

    return this;
  }

  private void promoteSeniorSub(Employee previousManager, Employee newManager, Employee seniorSub) {
    if (seniorSub.getReports().size() == 0) {
      // add exManager's reports
      for (Employee report : previousManager.getReports()) {
        seniorSub.addDirectReport(report);
      }
      // remove self from the report list
      seniorSub.getReports().removeIf(e -> e.getEmployeeNumber() == seniorSub.getEmployeeNumber());
      seniorSub.setSuperior(newManager);
    } else {
      promoteSeniorSub(seniorSub, seniorSub, seniorSub.getSeniorReport());
      // add exManagers reports
      for (Employee report : previousManager.getReports()) {
        if (report.getEmployeeNumber() != seniorSub.getEmployeeNumber()) {
          seniorSub.addDirectReport(report);
          // sub of previous Manager now reports to new seniorSub
          report.getSuperior().removeDirectReport(report.getEmployeeNumber());
          report.setSuperior(seniorSub);
        }
      }
      seniorSub.setSuperior(newManager);
    }
  }

  public Employee getCurrentSuperior(long employeeNumber) {
    Employee employee = ceo.findEmployee(employeeNumber);

    if (employee == null) {
      throw new InvalidOperationException();
    }

    if (employee.getSuperior().isHoliday()) {
      return getCurrentSuperior(employee.getSuperior().getEmployeeNumber());
    } else {
      return employee.getSuperior();
    }
  }

  public Collection<Employee> getCurrentReport(long employeeNumber) {
    Employee employee = ceo.findEmployee(employeeNumber);

    if (employee == null) {
      throw new InvalidOperationException();
    }

    ArrayList<Employee> reports = new ArrayList<>();

    for (Employee report : employee.getReports()) {
      if (!report.isHoliday()) {
        reports.add(report);
      } else {
        reports.addAll(this.getCurrentReport(report.getEmployeeNumber()));
      }
    }
    return reports;
  }

  @Override
  public Organisation goOnHoliday(long employee) {
    Employee holidayEmployee = ceo.findEmployee(employee);
    if (holidayEmployee == null | holidayEmployee.getRole() == Role.CEO) {
      throw new InvalidOperationException();
    }

    holidayEmployee.setHoliday(true);

    return this;
  }

  @Override
  public Organisation comeBackFromHoliday(long employee) {
    Employee holidayEmployee = ceo.findEmployee(employee);
    if (holidayEmployee == null | holidayEmployee.getRole() == Role.CEO) {
      throw new InvalidOperationException();
    }

    holidayEmployee.setHoliday(false);
    return this;
  }

  @Override
  public Organisation promoteEmployee(long employee) {
    Employee promoteEmployee = ceo.findEmployee(employee);
    if (promoteEmployee == null | promoteEmployee.getLevel() == 2) {
      throw new InvalidOperationException();
    }

    this.changeTeam(employee, promoteEmployee.getSuperior().getSuperior().getEmployeeNumber());
    return this;
  }

  @Override
  public String vicePresidentDirectorPromotionWaitingListToString() {
    return null;
  }
}
