package techtest.models.implementations;

import static org.junit.Assert.assertEquals;

import com.chong.han.techtest.exceptions.InvalidOperationException;
import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import java.time.LocalDate;
import org.junit.Test;

/**
 * Created by CHONG HAN on 14/04/2017.
 */
public class EmployeeTest {

  @Test
  public void canAddDirectReportsToManagingRoles() {
    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());
    Employee director = new Employee(1L, "first", "last", Role.DIRECTOR, LocalDate.now());

    ceo.addDirectReport(director);
    assertEquals("ceo has 1 reports", 1, ceo.getReports().size());
    assertEquals("ceo has director as a direct report", director,
        ceo.getReports().iterator().next());
  }

  @Test
  public void canGetEmployeeLevelCorrespondingToCeo() {

    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());
    Employee director = new Employee(1L, "first", "last", Role.DIRECTOR, LocalDate.now());
    director.setSuperior(ceo);

    ceo.addDirectReport(director);

    assertEquals("CEO has level 1", 1, ceo.getLevel());
    assertEquals("CEO's direct report has level 2", 2,
        ceo.getReports().iterator().next().getLevel());
  }

  @Test(expected = InvalidOperationException.class)
  public void setSuperiorForCeoThrowsException() {
    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());
    Employee director = new Employee(1L, "first", "last", Role.DIRECTOR, LocalDate.now());

    ceo.setSuperior(director);
  }

  @Test(expected = InvalidOperationException.class)
  public void retrieveSuperiorForCeoThrowsException() {
    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());

    ceo.getSuperior();
  }

  @Test
  public void canFindSubordinatesOfEmployee() {
    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());
    Employee manager = new Employee(2L, "first", "last", Role.MANAGER, LocalDate.now());
    Employee manager2 = new Employee(3L, "first", "last", Role.MANAGER, LocalDate.now());
    Employee employee = new Employee(1L, "first", "last", Role.EMPLOYEE, LocalDate.now());

    ceo.addDirectReport(manager);
    ceo.addDirectReport(manager2);
    manager.addDirectReport(employee);

    assertEquals("employee with employeeNumber 1 found", 1,
        ceo.findEmployee(1).getEmployeeNumber());
    assertEquals("employee is ceo's subordinate", employee, ceo.findEmployee(1));
  }

  @Test
  public void canGetMostSeniorDirectReportOfManagingRole() {
    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());
    Employee manager = new Employee(2L, "first", "last", Role.MANAGER,
        LocalDate.now().minusDays(1));
    Employee employee = new Employee(1L, "first", "last", Role.EMPLOYEE,
        LocalDate.now().minusDays(2));
    Employee director = new Employee(3L, "first", "last", Role.DIRECTOR,
        LocalDate.now().minusDays(3));

    ceo.addDirectReport(manager);
    ceo.addDirectReport(employee);
    ceo.addDirectReport(director);

    assertEquals("employee with number 3 is the most senior", 3,
        ceo.getSeniorReport().getEmployeeNumber());
  }

  @Test
  public void canRemoveDirectReportFromReports() {
    Employee ceo = new Employee(0L, "first", "last", Role.CEO, LocalDate.now());
    Employee manager = new Employee(2L, "first", "last", Role.MANAGER,
        LocalDate.now().minusDays(1));
    Employee employee = new Employee(1L, "first", "last", Role.EMPLOYEE,
        LocalDate.now().minusDays(2));
    Employee director = new Employee(3L, "first", "last", Role.DIRECTOR,
        LocalDate.now().minusDays(3));

    ceo.addDirectReport(manager);
    ceo.addDirectReport(employee);
    ceo.addDirectReport(director);

    assertEquals("2 direct reports in ceo", 3, ceo.getReports().size());
  }
}

