package com.fsd.exception;

import org.springframework.security.core.AuthenticationException;

public class FsdAuthenticationException extends AuthenticationException {

  private static final long serialVersionUID = 3961033612757948067L;

  public FsdAuthenticationException(String msg) {
    super(msg);
  }

}
