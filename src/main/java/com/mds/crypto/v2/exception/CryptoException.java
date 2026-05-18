package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception class representing cryptographic errors. This exception is thrown when an error occurs
 * during cryptographic operations.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class CryptoException extends BaseException {

  /**
   * Constructs a new CryptoException with the specified cause.
   *
   * @param cause The underlying cause of the exception.
   */
  public CryptoException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
