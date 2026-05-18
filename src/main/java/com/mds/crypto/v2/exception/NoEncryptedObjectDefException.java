package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception that represents errors related to missing encrypted object definitions. This exception
 * is used to encapsulate issues where an encrypted object definition is required but not provided.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class NoEncryptedObjectDefException extends BaseException {

  /**
   * Constructs a new NoEncryptedObjectDefException with the specified detail message and cause.
   *
   * @param code the error code associated with the exception.
   * @param message the detail message explaining the reason for the exception.
   */
  public NoEncryptedObjectDefException(String code, String message) {
    super(code, message);
  }
}
