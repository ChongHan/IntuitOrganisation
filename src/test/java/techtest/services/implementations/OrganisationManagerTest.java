package techtest.services.implementations;

import static org.junit.Assert.assertEquals;

import com.chong.han.techtest.models.implementations.Employee;
import com.chong.han.techtest.models.implementations.Role;
import com.chong.han.techtest.services.implementations.OrganisationManager;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by CHONG HAN on 15/04/2017.
 */
public class OrganisationManagerTest {

  Employee ceo;
  Employee manager, manager2, manager3;
  Employee employee, employee2;
  Employee director, director2;
  Employee vicePresident, vicePresident2;
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
  public void organisationCanAddNewEmployee() {
    assertEquals("initially ceo has no subordinate", 0, ceo.getReports().size());
    orgManager.addEmployee(manager, 0);
    assertEquals("after adding employee ceo has 1 subordinate", 1, ceo.getReports().size());
    orgManager.addEmployee(employee, manager.getEmployeeNumber());
    orgManager.addEmployee(employee2, employee.getEmployeeNumber());
    assertEquals("employee's role is promoted to MANAGER", Role.MANAGER, employee.getRole());
    assertEquals("employee2 reports to employee",
        employee.getReports().iterator().next().getEmployeeNumber(), employee2.getEmployeeNumber());
  }

  /**
   * before:
   * |  ceo - vicePresident2
   * |     - vicePresident  - director2
   * |                      - director  - manager
   * |                                  - manager2
   * |                                  - manager3
   * director now reports to vicePresident2
   */
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

    assertEquals("manager3 reports to vicePresident", 8,
        manager3.getSuperior().getEmployeeNumber());
    assertEquals("director has 0 subordinate", 0, director.getReports().size());
    assertEquals("vicePresident2 has 1 subordinate", 1, vicePresident2.getReports().size());
    assertEquals("director reports to vicePresident2", 9,
        director.getSuperior().getEmployeeNumber());
    assertEquals("vicepresident has 2 subordinate", 2, vicePresident.getReports().size());
  }

  @Test
  /**
   * changeTeam(e5, e2) e5 change team and will report to e2
   * before: e1 - e2
   *            - e3 - e4
   *                 - e5 - e6
   *                      - e7 - e8
   *                           - e9 - e10
   *                                - e11
   * after:  e1 - e2 - e5
   *            - e3 - e4
   *                 - e7 - e6
   *                      - e9 - e8
   *                           - e10
   */
  public void seniorSubsShouldBePromotedAccordinglyWhenManagerChangeTeam() {
    Employee e1 = new Employee(1L, "first", "last", Role.CEO, LocalDate.now());
    Employee m2 = new Employee(2L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(1));
    Employee m3 = new Employee(3L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(2));
    Employee m4 = new Employee(4L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(3));
    Employee m5 = new Employee(5L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(4));
    Employee m6 = new Employee(6L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(5));
    Employee m7 = new Employee(7L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(6));
    Employee m8 = new Employee(8L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(7));
    Employee m9 = new Employee(9L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(8));
    Employee m10 = new Employee(10L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(9));

    orgManager = new OrganisationManager(e1);
    orgManager.addEmployee(m2, 1);
    orgManager.addEmployee(m3, 1);
    orgManager.addEmployee(m4, 3);
    orgManager.addEmployee(m5, 3);
    orgManager.addEmployee(m6, 5);
    orgManager.addEmployee(m7, 5);
    orgManager.addEmployee(m8, 7);
    orgManager.addEmployee(m9, 7);
    orgManager.addEmployee(m10, 9);

    orgManager.changeTeam(5, 2);

    assertEquals(1, m2.getReports().size());
    assertEquals(2, m5.getSuperior().getEmployeeNumber());
    assertEquals(2, m3.getReports().size());
    assertEquals(3, m7.getSuperior().getEmployeeNumber());
    assertEquals(2, m7.getReports().size());
    assertEquals(7, m9.getSuperior().getEmployeeNumber());
    assertEquals(2, m9.getReports().size());
  }

  @Test
  public void employeeGoOnHolidaySubReportToManagerOfEmployee() {
    orgManager.addEmployee(vicePresident, 0);
    orgManager.addEmployee(director, vicePresident.getEmployeeNumber());
    orgManager.addEmployee(manager, director.getEmployeeNumber());

    orgManager.goOnHoliday(vicePresident.getEmployeeNumber());
    assertEquals(true, vicePresident.isHoliday());

    Employee report = vicePresident.getSeniorReport();
    assertEquals("vicePresident is on holiday, report to ceo", vicePresident.getSuperior(),
        orgManager.getCurrentSuperior(report.getEmployeeNumber()));

    // director goes on holiday
    orgManager.goOnHoliday(director.getEmployeeNumber());
    assertEquals("director and vicePresident both on holiday, manager reports directly to ceo",
        vicePresident.getSuperior(), orgManager.getCurrentSuperior(manager.getEmployeeNumber()));

    assertEquals("ceo has manager as direct report", manager,
        orgManager.getCurrentReport(ceo.getEmployeeNumber()).iterator().next());
  }

  @Test
  public void employeeComeBackFromHolidayResumeRole() {
    orgManager.addEmployee(vicePresident, 0);
    orgManager.addEmployee(director, vicePresident.getEmployeeNumber());
    orgManager.addEmployee(manager, director.getEmployeeNumber());
    orgManager.goOnHoliday(vicePresident.getEmployeeNumber());
    orgManager.comeBackFromHoliday(vicePresident.getEmployeeNumber());

    assertEquals(vicePresident, orgManager.getCurrentSuperior(director.getEmployeeNumber()));
  }

  @Test
  /**
   * promoteEmployee(e5) e5 change team and will report to e1
   * before: e1 - e2
   *            - e3 - e4
   *                 - e5 - e6
   *                      - e7 - e8
   *                           - e9 - e10
   * after:  e1 - e5
   *            - e2
   *            - e3 - e4
   *                 - e7 - e6
   *                      - e9 - e8
   *                           - e10
   */
  public void subOfEmployeeTakeOverIfEmployeeIsPromoted() {
    Employee e1 = new Employee(1L, "first", "last", Role.CEO, LocalDate.now());
    Employee m2 = new Employee(2L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(1));
    Employee m3 = new Employee(3L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(2));
    Employee m4 = new Employee(4L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(3));
    Employee m5 = new Employee(5L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(4));
    Employee m6 = new Employee(6L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(5));
    Employee m7 = new Employee(7L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(6));
    Employee m8 = new Employee(8L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(7));
    Employee m9 = new Employee(9L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(8));
    Employee m10 = new Employee(10L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(9));

    orgManager = new OrganisationManager(e1);
    orgManager.addEmployee(m2, 1);
    orgManager.addEmployee(m3, 1);
    orgManager.addEmployee(m4, 3);
    orgManager.addEmployee(m5, 3);
    orgManager.addEmployee(m6, 5);
    orgManager.addEmployee(m7, 5);
    orgManager.addEmployee(m8, 7);
    orgManager.addEmployee(m9, 7);
    orgManager.addEmployee(m10, 9);

    orgManager.promoteEmployee(m5.getEmployeeNumber());

    assertEquals(3, e1.getReports().size());
    assertEquals(2, m3.getReports().size());
    assertEquals("e1 has reports 2, 3, 5", e1.getReports(),
        new CopyOnWriteArrayList<>(Arrays.asList(m2, m3, m5)));
    assertEquals("m5 and m2 report to the same manager", m5.getSuperior(), m2.getSuperior());
    assertEquals("m3 has reports 4, 7", m3.getReports(),
        new CopyOnWriteArrayList<>(Arrays.asList(m4, m7)));
  }

  @Test
  /**
   * structure: e1 - e2
   *               - e3 - e4
   *                     - e5 - e6
   *                          - e7 - e8
   *                               - e9 - e10
   */
  public void canCountNumberOfDirectAndIndirectSubs() {
    Employee e1 = new Employee(1L, "first", "last", Role.CEO, LocalDate.now());
    Employee m2 = new Employee(2L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(1));
    Employee m3 = new Employee(3L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(2));
    Employee m4 = new Employee(4L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(3));
    Employee m5 = new Employee(5L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(4));
    Employee m6 = new Employee(6L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(5));
    Employee m7 = new Employee(7L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(6));
    Employee m8 = new Employee(8L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(7));
    Employee m9 = new Employee(9L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(8));
    Employee m10 = new Employee(10L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(9));

    orgManager = new OrganisationManager(e1);
    orgManager.addEmployee(m2, 1);
    orgManager.addEmployee(m3, 1);
    orgManager.addEmployee(m4, 3);
    orgManager.addEmployee(m5, 3);
    orgManager.addEmployee(m6, 5);
    orgManager.addEmployee(m7, 5);
    orgManager.addEmployee(m8, 7);
    orgManager.addEmployee(m9, 7);
    orgManager.addEmployee(m10, 9);

    assertEquals("e1 has 9 subs", 9, orgManager.countAllSub(e1.getEmployeeNumber()));
    assertEquals("m9 has 1 sub", 1, orgManager.countAllSub(m9.getEmployeeNumber()));
    assertEquals("m7 has 3 subs", 3, orgManager.countAllSub(m7.getEmployeeNumber()));
  }

  @Test
  /**
   * structure: e1 - e2
   *               - e3 - e4
   *                     - e5 - e6
   *                          - e7 - e8
   *                               - e9 - e10
   */
  public void canGetNumberOfSubordinatesWhoHaveRole() {
    Employee e1 = new Employee(1L, "first", "last", Role.CEO, LocalDate.now());
    Employee m2 = new Employee(2L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(1));
    Employee m3 = new Employee(3L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(2));
    Employee m4 = new Employee(4L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(3));
    Employee m5 = new Employee(5L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(4));
    Employee m6 = new Employee(6L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(5));
    Employee m7 = new Employee(7L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(6));
    Employee m8 = new Employee(8L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(7));
    Employee m9 = new Employee(9L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(8));
    Employee m10 = new Employee(10L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(9));

    orgManager = new OrganisationManager(e1);
    orgManager.addEmployee(m2, 1)
        .addEmployee(m3, 1)
        .addEmployee(m4, 3)
        .addEmployee(m5, 3)
        .addEmployee(m6, 5)
        .addEmployee(m7, 5)
        .addEmployee(m8, 7)
        .addEmployee(m9, 7)
        .addEmployee(m10, 9);

    assertEquals(
        "10 is a Manager and a sub of 9, but 10 doesnt have any direct reports, therefore 9 has no manager sub",
        0, orgManager.countAllSubRole(9, Role.MANAGER));
    assertEquals(4, orgManager.countAllSubRole(1, Role.MANAGER));
    assertEquals(0, orgManager.countAllSubRole(6, Role.MANAGER));
    assertEquals(3, orgManager.countAllSubRole(3, Role.MANAGER));
  }

  @Test
  /**
   * structure: e1 - e2
   *               - e3 - e4
   *                     - e5 - e6
   *                          - e7 - e8
   *                               - e9 - e10
   *                                    - e11 - e12
   *                                          - e13 - e14
   *                                                - e15 - e16
   *                                                      - e17 - e18
   *                                                            - e19 - e20
   *                                                                  - e21 - e22
   *                                                                        - e23
   */
  public void canGetPromotionWaitingList() {
    Employee e1 = new Employee(1L, "first", "last", Role.CEO, LocalDate.now());
    Employee m2 = new Employee(2L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(1));
    Employee m3 = new Employee(3L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(2));
    Employee m4 = new Employee(4L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(3));
    Employee m5 = new Employee(5L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(4));
    Employee m6 = new Employee(6L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(5));
    Employee m7 = new Employee(7L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(6));
    Employee m8 = new Employee(8L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(7));
    Employee m9 = new Employee(9L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(8));
    Employee m10 = new Employee(10L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(9));
    Employee m11 = new Employee(11L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(10));
    Employee m12 = new Employee(12L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(11));
    Employee m13 = new Employee(13L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(12));
    Employee m14 = new Employee(14L, "first", "last", Role.DIRECTOR, LocalDate.now().minusDays(13));
    Employee m15 = new Employee(15L, "first", "last", Role.DIRECTOR, LocalDate.now().minusDays(14));
    Employee m16 = new Employee(16L, "first", "last", Role.DIRECTOR, LocalDate.now().minusDays(15));
    Employee m17 = new Employee(17L, "first", "last", Role.DIRECTOR, LocalDate.now().minusDays(16));
    Employee m18 = new Employee(18L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(17));
    Employee m19 = new Employee(19L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(18));
    Employee m20 = new Employee(20L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(19));
    Employee m21 = new Employee(21L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(20));
    Employee m22 = new Employee(22L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(21));
    Employee m23 = new Employee(23L, "first", "last", Role.MANAGER, LocalDate.now().minusDays(22));

    orgManager = new OrganisationManager(e1);
    orgManager.addEmployee(m2, 1)
        .addEmployee(m3, 1)
        .addEmployee(m4, 3)
        .addEmployee(m5, 3)
        .addEmployee(m6, 5)
        .addEmployee(m7, 5)
        .addEmployee(m8, 7)
        .addEmployee(m9, 7)
        .addEmployee(m10, 9)
        .addEmployee(m11, 9)
        .addEmployee(m12, 11)
        .addEmployee(m13, 11)
        .addEmployee(m14, 13)
        .addEmployee(m15, 13)
        .addEmployee(m16, 15)
        .addEmployee(m17, 15)
        .addEmployee(m18, 17)
        .addEmployee(m19, 17)
        .addEmployee(m20, 19)
        .addEmployee(m21, 19)
        .addEmployee(m22, 21)
        .addEmployee(m23, 21);

    assertEquals("m3 is a candidate to be promoted to director", m3,
        orgManager.promotionWaitingList(Role.DIRECTOR).iterator().next());
    assertEquals("only 1 qualified candidate", 1,
        orgManager.promotionWaitingList(Role.DIRECTOR).size());
  }
}
