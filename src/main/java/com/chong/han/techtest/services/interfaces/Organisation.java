package com.chong.han.techtest.services.interfaces;

import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import java.util.Collection;

/**
 * Created by CHONG HAN on 15/04/2017.
 */
public interface Organisation {

  /**
   * Add an employee to organisation
   *
   * @param newEmployee new employee to be added
   * @param managerEmployeeNumber id number of the manager of the new employee
   * @return the new organisation with an additional report staff if success
   */
  Organisation addEmployee(Employee newEmployee, long managerEmployeeNumber);

  /**
   * change employee to a new team under management of the newManager
   *
   * @param employee employeeNumber of employee to be changed
   * @param newManager employeeNumner of employee's new manager
   * @return the new organisation if success
   */
  Organisation changeTeam(long employee, long newManager);

  /**
   * release an employee for holiday, all his subordinates start reporting to the employee's manager
   * temporarily.
   *
   * @param employee employeeNumber of employee to be released
   * @return the updated organisation
   */
  Organisation goOnHoliday(long employee);

  /**
   * resume function of the employee which was on holiday and it's subordinates
   *
   * @param employee employeeNumber of employee to be welcome back
   * @return the updated organisation
   */
  Organisation comeBackFromHoliday(long employee);

  /**
   * promote employee to become a peer of his former manager and have his most senior subordinate
   * take over his previous position and role
   *
   * @param employee employeeNumber of employee to be promoted
   * @return updated organisation
   */
  Organisation promoteEmployee(long employee);

  /**
   * director candidates:
   * employees who have 20 - 40 subordinates and 2+ managers
   * vice president candidates:
   * employees who have 40+     subordinates and 4 directors
   *
   * @return the string representation of the found employees
   */
  Collection<Employee> promotionWaitingList(Role role);


}
