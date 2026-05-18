package com.mds.crypto.v2.exception;

import com.mds.error.handler.exception.base.BaseException;

/**
 * Exception that represents errors occurring during JSON parsing operations. This exception is used
 * to encapsulate issues related to invalid or malformed JSON data.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class JsonParseException extends BaseException {

  /**
   * Constructs a new JsonParseException with the specified error code and message.
   *
   * @param code    the error code associated with the exception
   * @param message the detailed message explaining the reason for the exception
   */
  public JsonParseException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
