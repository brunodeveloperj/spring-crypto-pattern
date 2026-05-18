package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception that represents errors occurring during encryption operations. This exception is used
 * to encapsulate issues related to cryptography.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class EncryptionException extends BaseException {

  /**
   * Constructs a new EncryptionException with the specified message and cause.
   *
   * @param message A detailed message explaining the reason for the exception.
   * @param cause The underlying cause of the exception.
   */
  public EncryptionException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
