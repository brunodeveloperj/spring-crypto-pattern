package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception class representing an inaccessible object scenario.
 * Extends the {@link BaseException} to include additional context such as an error code, message, and cause.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class InaccessibleObjectException extends BaseException {

  /**
   * Constructs a new InaccessibleObjectException with the specified error code, message, and cause.
   *
   * @param code    the error code associated with the exception
   * @param message the descriptive message of the exception
   * @param cause   the original cause of the exception
   */
  public InaccessibleObjectException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
