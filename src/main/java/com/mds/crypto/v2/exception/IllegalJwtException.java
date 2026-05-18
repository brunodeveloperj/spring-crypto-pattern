package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception that represents errors related to illegal JWTs. This exception is used to encapsulate
 * issues associated with invalid or malformed JWT tokens.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class IllegalJwtException extends BaseException {

  /**
   * Constructs a new IllegalJwtException with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the exception.
   */
  public IllegalJwtException(String code, String message) {
    super(code, message);
  }

  /**
   * Constructs a new IllegalJwtException with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the exception.
   * @param cause   the cause of the exception.
   */
  public IllegalJwtException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
