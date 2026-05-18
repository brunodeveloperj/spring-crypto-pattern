package com.mds.crypto.v2.security;

import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_CLAIM_CLASS_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_CLAIM_CLASS_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_STRING_CLASS_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_STRING_CLASS_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_TOKEN_DECODE_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_TOKEN_DECODE_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_TOKEN_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ILLEGAL_JWT_TOKEN_ERROR_MESSAGE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mds.crypto.v2.exception.IllegalJwtException;
import com.mds.crypto.v2.exception.JsonParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for decoding encrypted JWT (JSON Web Token) objects.
 *
 * <p>Provides overloaded static methods to extract typed claims from a
 * JWT string — with or without token validity checks. Uses
 * {@link com.auth0.jwt.JWT} for decoding and throws
 * {@link IllegalJwtException} when the token is blank, null, or
 * structurally invalid.
 *
 * <p>The {@code "Bearer "} prefix is automatically stripped before
 * decoding.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtEncryptedObjectDecoder {

  /**
   * Decodes a specific claim from a JWT and validates the token.
   *
   * @param jwt       the JWT string to decode.
   * @param claimName the name of the claim to extract.
   * @return the decoded claim value as a String.
   * @throws IllegalJwtException if the token is invalid.
   */
  public static String decodeJwtClaim(String jwt, String claimName) {
    final String decodedObject = sessionDecode(jwt, claimName, String.class);
    if (isInvalidToken(decodedObject)) {
      throw new IllegalJwtException(ILLEGAL_JWT_STRING_CLASS_ERROR_CODE, ILLEGAL_JWT_STRING_CLASS_ERROR_MESSAGE);
    }
    return decodedObject;
  }

  /**
   * Decodes a specific claim from a JWT into a typed object and validates the token.
   *
   * @param <T>        the type of the claim value.
   * @param jwt        the JWT string to decode.
   * @param claimName  the name of the claim to extract.
   * @param claimClazz the class of the claim type.
   * @return the decoded claim value as an object of type T.
   * @throws IllegalJwtException if the token is invalid.
   */
  public static <T> T decodeJwtClaim(String jwt, String claimName, Class<T> claimClazz) {
    final T decodedObject = sessionDecode(jwt, claimName, claimClazz);
    if (isInvalidToken(decodedObject)) {
      throw new IllegalJwtException(ILLEGAL_JWT_CLAIM_CLASS_ERROR_CODE, ILLEGAL_JWT_CLAIM_CLASS_ERROR_MESSAGE);
    }
    return decodedObject;
  }

  /**
   * Decodes a specific claim from a JWT into a typed object without validating the token.
   *
   * @param <T>        the type of the claim value.
   * @param jwt        the JWT string to decode.
   * @param claimName  the name of the claim to extract.
   * @param claimClazz the class of the claim type.
   * @return the decoded claim value as an object of type T.
   */
  public static <T> T decodeJwtClaimWithoutValidation(String jwt, String claimName, Class<T> claimClazz) {
    return sessionDecode(jwt, claimName, claimClazz);
  }

  /**
   * Decodes a specific claim from a JWT into a typed object.
   *
   * @param <T>       the type of the claim value.
   * @param jwt       the JWT string to decode.
   * @param claimName the name of the claim to extract.
   * @param type      the class of the claim type.
   * @return the decoded claim value as an object of type T, or null if the session is invalid.
   * @throws JsonParseException if there is an error processing the JSON.
   */
  private static <T> T sessionDecode(String jwt, String claimName, Class<T> type) {
    if (isInvalidToken(jwt)) {
      throw new IllegalJwtException(ILLEGAL_JWT_TOKEN_ERROR_CODE, ILLEGAL_JWT_TOKEN_ERROR_MESSAGE);
    }

    try {
      jwt = jwt.replace("Bearer", EMPTY).trim();
      DecodedJWT decodedJWT = JWT.decode(jwt);
      return decodedJWT.getClaim(claimName).as(type);
    } catch (Exception e) {
      log.error("Error processing JWT claim: {}", claimName, e);
      throw new IllegalJwtException(ILLEGAL_JWT_TOKEN_DECODE_ERROR_CODE, ILLEGAL_JWT_TOKEN_DECODE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Checks if a token is invalid.
   *
   * @param <T>   the type of the token.
   * @param token the token to validate.
   * @return true if the token is invalid, false otherwise.
   */
  private static <T> boolean isInvalidToken(T token) {
    boolean invalidToken = true;
    if (token != null) {
      if (token instanceof String) {
        invalidToken = ((String) token).isBlank();
      } else {
        invalidToken = false;
      }
    }
    return invalidToken;
  }
}
