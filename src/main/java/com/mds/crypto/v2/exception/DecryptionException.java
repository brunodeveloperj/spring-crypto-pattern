package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception class representing errors that occur during decryption operations. This exception is
 * used to encapsulate issues related to cryptographic decryption.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class DecryptionException extends BaseException {


  /**
   * Constructor with parameters.
   *
   * @param code The error code.
   * @param message The error message.
   * @param cause The root cause of the exception.
   */
  public DecryptionException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
