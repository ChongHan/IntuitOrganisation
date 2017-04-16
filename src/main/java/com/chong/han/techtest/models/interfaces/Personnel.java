package com.chong.han.techtest.models.interfaces;

import com.chong.han.techtest.models.implementations.Employee;

/**
 * Created by CHONG HAN on 14/04/2017.
 */
public interface Personnel {

  /**
   * Add direct report to a employee
   *
   * @return true if success false otherwise
   */
  public Employee addDirectReport(Personnel directReport);


  /**
   * Remove a direct report from a employee
   *
   * @param employeeNumber unique id to locate direct report
   * @return true if found and removed false otherwise
   */
  public Employee removeDirectReport(long employeeNumber);

  /**
   * Retrieve the numeric value that determines their level in the company hierarchy
   * in relation to the CEO (who is at level 1)
   *
   * @return int of the level
   */
  public int getLevel();
}
