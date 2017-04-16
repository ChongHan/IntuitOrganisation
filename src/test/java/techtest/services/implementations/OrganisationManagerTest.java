package techtest.services.implementations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import com.chong.han.techtest.services.implementations.OrganisationManager;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by CHONG HAN on 15/04/2017.
 */
public class OrganisationManagerTest {

  Employee ceo,
      manager, manager2, manager3,
      employee, employee2,
      director, director2,
      vicePresident, vicePresident2;
  OrganisationManager orgManager;

  @Before
  public void initialize() {

    ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());

    orgManager = new OrganisationManager(ceo);

    manager = new Employee(1L, "first", "last", Role.MANAGER,
        LocalDate.now().minusDays(1));
    manager2 = new Employee(2L, "first", "last", Role.MANAGER,
        LocalDate.now().minusDays(2));
    manager3 = new Employee(3L, "first", "last", Role.MANAGER,
        LocalDate.now().minusDays(3));
    employee = new Employee(4L, "first", "last", Role.EMPLOYEE,
        LocalDate.now().minusDays(4));
    employee2 = new Employee(5L, "first", "last", Role.EMPLOYEE,
        LocalDate.now().minusDays(5));
    director = new Employee(6L, "first", "last", Role.DIRECTOR,
        LocalDate.now().minusDays(6));
    director2 = new Employee(7L, "first", "last", Role.DIRECTOR,
        LocalDate.now().minusDays(7));
    vicePresident = new Employee(8L, "first", "last", Role.VICE_PRESIDENT,
        LocalDate.now().minusDays(8));
    vicePresident2 = new Employee(9L, "first", "last", Role.VICE_PRESIDENT,
        LocalDate.now().minusDays(9));
  }

  @Test
  public void organisationCanAddnewEmployee() {
    assertEquals("initially ceo has no subordinate", 0, ceo.getReports().size());
    orgManager.addEmployee(manager, 0);
    assertEquals("after adding employee ceo has 1 subordinate", 1, ceo.getReports().size());
    orgManager.addEmployee(employee, manager.getEmployeeNumber());
    orgManager.addEmployee(employee2, employee.getEmployeeNumber());
    assertEquals("employee's role is promoted to MANAGER", Role.MANAGER, employee.getRole());
    assertEquals("employee2 reports to employee", employee.getReports().iterator().next().getEmployeeNumber(), employee2.getEmployeeNumber());
  }

  @Test
  public void employeeCanChangeTeam() {
    orgManager.addEmployee(vicePresident, ceo.getEmployeeNumber());
    orgManager.addEmployee(vicePresident2, ceo.getEmployeeNumber());
    orgManager.addEmployee(director, vicePresident.getEmployeeNumber());
    orgManager.addEmployee(director2, vicePresident.getEmployeeNumber());
    orgManager.addEmployee(manager, director.getEmployeeNumber());
    orgManager.addEmployee(manager2, director.getEmployeeNumber());
    orgManager.addEmployee(manager3, director.getEmployeeNumber());

    orgManager.changeTeam(director.getEmployeeNumber(), vicePresident2.getEmployeeNumber());

    assertEquals("manager3 reports to vicePresident", 8, manager3.getSuperior().getEmployeeNumber());
    assertEquals("director has 0 subordinate", 0, director.getReports().size());
    assertEquals("vicePresident2 has 1 subordinate", 1, vicePresident2.getReports().size());
    assertEquals("director reports to vicePresident2", 9, director.getSuperior().getEmployeeNumber());
    assertEquals("vicepresident has 1 subordinate", 1, vicePresident.getReports().size());
  }




}
