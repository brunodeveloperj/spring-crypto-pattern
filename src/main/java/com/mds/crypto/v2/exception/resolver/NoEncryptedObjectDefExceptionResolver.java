package com.mds.crypto.v2.exception.resolver;

import static com.mds.error.handler.enumerator.Action.RETRY_ON_STATE;
import static com.mds.error.handler.enumerator.Type.SECURITY;
import static com.mds.error.handler.exception.helper.ErrorExceptionHandlerHelper.createError;
import static com.mds.error.handler.exception.keys.ExceptionMessageKeys.DEFAULT_ERROR_TITLE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.mds.crypto.v2.exception.NoEncryptedObjectDefException;
import com.mds.error.handler.exception.resolver.ExceptionResolver;
import com.mds.error.handler.model.response.ErrorResponse;
import org.springframework.stereotype.Component;

/**
 * Exception resolver for {@link NoEncryptedObjectDefException}.
 *
 * <p>Maps missing encrypted-object-definition failures to an
 * {@link com.mds.error.handler.model.response.ErrorResponse} with
 * HTTP 422 (Unprocessable Entity), action {@code RETRY_ON_STATE},
 * and type {@code SECURITY}.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Component
public class NoEncryptedObjectDefExceptionResolver implements ExceptionResolver<NoEncryptedObjectDefException> {

  /**
   * Resolves a NoEncryptedObjectDefException instance and returns an ErrorResponse object. This method maps the exception to an appropriate error response.
   *
   * @param error the NoEncryptedObjectDefException instance to be resolved
   * @return an ErrorResponse object representing the error response
   */
  @Override
  public ErrorResponse resolve(NoEncryptedObjectDefException error) {
    return createError(error,
                       RETRY_ON_STATE,
                       SECURITY,
                       UNPROCESSABLE_ENTITY.value(),
                       DEFAULT_ERROR_TITLE,
                       error.getCode(),
                       error.getMessage());
  }
}
