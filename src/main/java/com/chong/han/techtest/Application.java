package com.chong.han.techtest;

import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.EmployeeFactory;
import com.chong.han.techtest.models.implementations.Role;
import com.chong.han.techtest.services.implementations.OrganisationManager;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Created by Chong Han on 17/04/2017.
 * Simple demonstration of the program
 */
public class Application {

  public static void main(String[] args) {

    EmployeeFactory factory = new EmployeeFactory();

    Employee ceo;
    Employee vicePresident1, vicePresident2;
    Employee director1, director2, director3, director4;
    Employee manager1, manager2, manager3, manager4;
    Employee employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8, employee9, employee10, employee11, employee12, employee13, employee14;

    ceo = factory.createEmployee("FirstName", "LastName", Role.CEO, LocalDate.now().minusDays(14));
    vicePresident1 = factory
        .createEmployee("F", "L", Role.VICE_PRESIDENT, LocalDate.now().minusDays(13));
    vicePresident2 = factory
        .createEmployee("F", "L", Role.VICE_PRESIDENT, LocalDate.now().minusDays(12));
    director1 = factory.createEmployee("F", "L", Role.DIRECTOR, LocalDate.now().minusDays(11));
    director2 = factory.createEmployee("F", "L", Role.DIRECTOR, LocalDate.now().minusDays(10));
    director3 = factory.createEmployee("F", "L", Role.DIRECTOR, LocalDate.now().minusDays(9));
    director4 = factory.createEmployee("F", "L", Role.DIRECTOR, LocalDate.now().minusDays(8));
    manager1 = factory.createEmployee("F", "L", Role.MANAGER, LocalDate.now().minusDays(7));
    manager2 = factory.createEmployee("F", "L", Role.MANAGER, LocalDate.now().minusDays(6));
    manager3 = factory.createEmployee("F", "L", Role.MANAGER, LocalDate.now().minusDays(5));
    manager4 = factory.createEmployee("F", "L", Role.MANAGER, LocalDate.now().minusDays(4));
    employee1 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now().minusDays(3));
    employee2 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now().minusDays(2));
    employee3 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now().minusDays(1));
    employee4 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee5 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee6 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee7 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee8 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee9 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee10 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee11 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee12 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee13 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());
    employee14 = factory.createEmployee("F", "L", Role.EMPLOYEE, LocalDate.now());

    OrganisationManager orgnManager = new OrganisationManager(ceo);

    orgnManager.addEmployee(vicePresident1, ceo.getEmployeeNumber());
    orgnManager.addEmployee(vicePresident2, ceo.getEmployeeNumber());
    orgnManager.addEmployee(director1, vicePresident2.getEmployeeNumber());
    orgnManager.addEmployee(director2, vicePresident2.getEmployeeNumber());
    orgnManager.addEmployee(director3, vicePresident2.getEmployeeNumber());
    orgnManager.addEmployee(director4, vicePresident2.getEmployeeNumber());
    orgnManager.addEmployee(manager1, director4.getEmployeeNumber());
    orgnManager.addEmployee(manager2, director4.getEmployeeNumber());
    orgnManager.addEmployee(manager3, director4.getEmployeeNumber());
    orgnManager.addEmployee(manager4, director4.getEmployeeNumber());
    orgnManager.addEmployee(employee1, manager1.getEmployeeNumber());
    orgnManager.addEmployee(employee2, manager1.getEmployeeNumber());
    orgnManager.addEmployee(employee3, manager1.getEmployeeNumber());
    orgnManager.addEmployee(employee4, manager1.getEmployeeNumber());
    orgnManager.addEmployee(employee5, manager2.getEmployeeNumber());
    orgnManager.addEmployee(employee6, manager2.getEmployeeNumber());
    orgnManager.addEmployee(employee7, manager2.getEmployeeNumber());
    orgnManager.addEmployee(employee8, manager2.getEmployeeNumber());
    orgnManager.addEmployee(employee9, manager3.getEmployeeNumber());
    orgnManager.addEmployee(employee10, manager3.getEmployeeNumber());
    orgnManager.addEmployee(employee11, manager3.getEmployeeNumber());
    orgnManager.addEmployee(employee12, manager3.getEmployeeNumber());
    orgnManager.addEmployee(employee13, manager4.getEmployeeNumber());
    orgnManager.addEmployee(employee14, manager4.getEmployeeNumber());

    System.out.println("Original organisation tree: ");
    orgnManager.printTree();
    /* expected output:
        └── 0 C
            ├── 1 V
            └── 2 V
                ├── 3 D
                ├── 4 D
                ├── 5 D
                └── 6 D
                    ├── 7 M
                    ├── 8 M
                    ├── 9 M
                    └── 10 M
                        ├── 11 E
                        ├── 12 E
                        ├── 13 E
                        └── 14 E
     */

    System.out.println();
    System.out.println("6 D change team and report to new manager 1 V");
    orgnManager.changeTeam(director4.getEmployeeNumber(), vicePresident1.getEmployeeNumber());

    System.out.println("new organisation tree:");
    orgnManager.printTree();

    System.out.println();
    System.out.println("11 E is now gets promotion, his subordinates gets promoted accordingly");
    orgnManager.promoteEmployee(employee1.getEmployeeNumber());
    System.out.println("new organisation tree: ");
    orgnManager.printTree();

    Collection<Employee> directorCandidates = orgnManager.promotionWaitingList(Role.DIRECTOR);

    for (Employee employee : directorCandidates) {
      System.out.print(
          employee.getEmployeeNumber() + " "); // noone is eligible for promotion in this example
    }
  }
}
