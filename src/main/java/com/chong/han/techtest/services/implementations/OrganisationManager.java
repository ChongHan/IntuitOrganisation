package com.chong.han.techtest.services.implementations;

import com.chong.han.techtest.exceptions.InvalidOperationException;
import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import com.chong.han.techtest.services.interfaces.Organisation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by CHONG HAN on 15/04/2017.
 */
public class OrganisationManager implements Organisation {

  private static final int DIRECTOR_PROMOTION_REQUIRED_SUB_COUNT = 20;
  private static final int DIRECTOR_PROMOTION_RQUIRED_MANAGER_COUNT = 2;
  private static final int VICEPRESIDENT_PROMOTION_REQUIRED_SUB_COUNT = 41;
  private static final int VICEPRESIDENT_PROMOTION_RQUIRED_DIRECTOR_COUNT = 4;
  private final Employee ceo;

  public OrganisationManager(Employee ceo) {
    if (!ceo.getRole().equals(Role.CEO)) {
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

    boolean employeeExists = employee != null;
    boolean managerExists = newManager != null;
    boolean isCeo = employee.getRole() == Role.CEO;
    // manager is not his current manager
    if (!employeeExists || !managerExists || isCeo) {
      throw new InvalidOperationException(String
          .format("employeeExists=%b managerExists=%b isCeo=%b", employeeExists, managerExists,
              isCeo));
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

  private void promoteSeniorSub(Employee promotedManager, Employee subordinateNewManager,
      Employee autoPromotedSubordinate) {
    boolean seniorHasNobodyReportingToThem = autoPromotedSubordinate.getReports().size() == 0;
    if (seniorHasNobodyReportingToThem) {
      subordinateTakesOnSeniorsSubordinates(promotedManager, autoPromotedSubordinate,
          subordinateNewManager);
    } else {
      // We need to promote this managers subordinates
      promoteSeniorSub(autoPromotedSubordinate, autoPromotedSubordinate,
          autoPromotedSubordinate.getSeniorReport());
      // add exManagers reports
      addAutoPromotedSubordinateReports(promotedManager, autoPromotedSubordinate,
          subordinateNewManager);
    }
  }

  private void addAutoPromotedSubordinateReports(Employee promotedManager,
      Employee autoPromotedSubordinate, Employee subordinateNewManager) {
    for (Employee report : promotedManager.getReports()) {
      if (report.getEmployeeNumber() != autoPromotedSubordinate.getEmployeeNumber()) {
        autoPromotedSubordinate.addDirectReport(report);
        // sub of previous Manager now reports to new seniorSub
        report.getSuperior().removeDirectReport(report.getEmployeeNumber());
        report.setSuperior(autoPromotedSubordinate);
      }
    }
    autoPromotedSubordinate.setSuperior(subordinateNewManager);
  }

  private void subordinateTakesOnSeniorsSubordinates(Employee promotedManager,
      Employee autoPromotedSubordinate, Employee subordinateNewManager) {
    // take over promoted manager's reports
    for (Employee managersReport : promotedManager.getReports()) {
      // exclude autoPromotedSubordinate itself
      if (managersReport.getEmployeeNumber() != autoPromotedSubordinate.getEmployeeNumber()) {
        autoPromotedSubordinate.addDirectReport(managersReport);
        managersReport.setSuperior(autoPromotedSubordinate);
        // remove old reports from the promotedManager
        subordinateNewManager.getReports().remove(managersReport);
      }
    }
    autoPromotedSubordinate.setSuperior(subordinateNewManager);
  }

  public Employee getCurrentSuperior(long employeeNumber) {
    Employee employee = ceo.findEmployee(employeeNumber);

    if (employee == null) {
      throw new InvalidOperationException(
          String.format("Employee %d doesn't exist", employeeNumber));
    }

    boolean isCurrentSuperiorOnHoliday = employee.getSuperior().isHoliday();
    if (isCurrentSuperiorOnHoliday) {
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
  public Collection<Employee> promotionWaitingList(Role role) {
    Collection<Employee> promotionWaitingList = new ArrayList<>();

    if (ceo.getReports().size() > 0) {
      for (Employee report : ceo.getReports()) {
        promotionWaitingList.addAll(promotionCandidateList(report, role));
      }
    }

    promotionWaitingList.removeIf(
        e -> e.getRole() == Role.VICE_PRESIDENT); // no need to promote vice_president to director
    Collections.sort((ArrayList) promotionWaitingList, Employee.START_DATE_COMPARATOR);
    return promotionWaitingList;
  }

  private Collection<Employee> promotionCandidateList(Employee employee,
      Role role) {
    Collection<Employee> promotionWaitingList = new ArrayList<>();
    boolean hasEnoughSubordinates = false;
    boolean hasEnoughManagingSubordinates = false;
    if (role == Role.DIRECTOR) {
      hasEnoughSubordinates =
          countAllSub(employee.getEmployeeNumber()) >= DIRECTOR_PROMOTION_REQUIRED_SUB_COUNT;
      hasEnoughManagingSubordinates =
          countSubRole(employee, Role.MANAGER) >= DIRECTOR_PROMOTION_RQUIRED_MANAGER_COUNT;
    } else if (role == Role.VICE_PRESIDENT) {
      hasEnoughSubordinates =
          countAllSub(employee.getEmployeeNumber()) >= VICEPRESIDENT_PROMOTION_REQUIRED_SUB_COUNT;
      hasEnoughManagingSubordinates =
          countSubRole(employee, Role.DIRECTOR) >= VICEPRESIDENT_PROMOTION_RQUIRED_DIRECTOR_COUNT;
    }

    return findAllPromotionCandidates(employee, hasEnoughSubordinates,
        hasEnoughManagingSubordinates, promotionWaitingList, role);
  }

  private Collection<Employee> findAllPromotionCandidates(Employee rootEmployee,
      boolean hasEnoughSubordinates, boolean hasEnoughManagingSubordinates,
      Collection<Employee> promotionWaitingList, Role role) {
    if (!hasEnoughSubordinates) {
      return promotionWaitingList;
    } else if (hasEnoughManagingSubordinates) {
      promotionWaitingList
          .add(rootEmployee); // employee meets the requirements to become a director
      addAllDirectorCandidates(promotionWaitingList, rootEmployee, role);
    }
    return promotionWaitingList;
  }

  private void addAllDirectorCandidates(Collection<Employee> candidateList, Employee manager,
      Role role) {
    for (Employee report : manager.getReports()) {
      candidateList.addAll(promotionCandidateList(report, role));
    }
  }

  public int countAllSub(long employeeNumber) {
    Employee employee = ceo.findEmployee(employeeNumber);

    if (employee == null) {
      throw new InvalidOperationException();
    }
    if (employee.getReports().size() == 0) {
      return 0;
    }

    return countSub(employee) - 1; // remove self count
  }

  private int countSub(Employee employee) {
    if (employee.getReports().size() == 0) {
      return 1;
    } else {
      int count = 0;
      for (Employee report : employee.getReports()) {
        count += countSub(report);
      }
      return count + 1;
    }
  }

  public int countAllSubRole(long employeeNumber, Role role) {
    Employee employee = ceo.findEmployee(employeeNumber);

    if (employee == null) {
      throw new InvalidOperationException();
    }

    int count;
    count = countSubRole(employee, role);
    if (employee.getRole() == role && employee.getReports().size() > 0) { //remove count of self
      count -= 1;
    }
    return count;
  }

  private int countSubRole(Employee employee, Role role) {
    if (employee.getReports().size() == 0) {
      if (employee.getRole() == role && role != Role.MANAGER) { // only count manager with subs
        return 1;
      } else {
        return 0;
      }
    } else {
      int count = 0;
      for (Employee report : employee.getReports()) {
        count += countSubRole(report, role);
      }
      if (employee.getRole() == role) {
        count += 1;
      }
      boolean isManagingRole = employee.getRole() != Role.CEO && employee.getRole() != role
          && employee.getRole() != Role.MANAGER
          && employee.getReports().size() > 0;
      if (isManagingRole) {
        count += 1;
      }
      return count;
    }
  }

  public void printTree() {
    ceo.print();
  }
}
