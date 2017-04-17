package com.chong.han.techtest.exceptions;

/**
 * Created by Chong Han on 14/04/2017.
 */
public class InvalidOperationException extends RuntimeException {

  public InvalidOperationException(String message) {
    super(message);
  }

  public InvalidOperationException() {
    super();
  }

}
