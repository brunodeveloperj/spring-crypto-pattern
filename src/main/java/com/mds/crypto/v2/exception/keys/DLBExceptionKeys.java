package com.mds.crypto.v2.exception.keys;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

/**
 * Centralised catalogue of error codes and user-facing messages for the
 * v2 cryptographic layer.
 *
 * <p>Each constant pair ({@code *_ERROR_CODE} / {@code *_ERROR_MESSAGE})
 * maps to a specific failure scenario and is consumed by the corresponding
 * {@link com.mds.error.handler.exception.resolver.ExceptionResolver}
 * implementations in {@code com.mds.crypto.v2.exception.resolver}.
 *
 * <p>Code ranges:
 * <ul>
 *   <li>{@code DLB_0013 – DLB_0019} — encryption / decryption / object access errors</li>
 *   <li>{@code DLB_0020 – DLB_0024} — JWT and encrypted-object-definition errors</li>
 * </ul>
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@NoArgsConstructor(access = PRIVATE)
public class DLBExceptionKeys {

  /** Error code for illegal encrypted object exceptions. */
  public static final String CRYPTO_ERROR_CODE = "DLB_0013";

  /** Error message for illegal encrypted object exceptions. */
  public static final String CRYPTO_ERROR_MESSAGE = "Ocorreu um erro ao processar a solicitação de segurança. Por favor, tente novamente. Se o problema persistir, entre em contato com o suporte comercial.";

  /** Error code for inaccessible object exceptions. */
  public static final String INACCESSIBLE_OBJECT_ERROR_CODE = "DLB_0014";

  /** Error message for inaccessible object exceptions. */
  public static final String INACCESSIBLE_OBJECT_ERROR_MESSAGE = CRYPTO_ERROR_MESSAGE;

  /** Error code for decryption typed object errors. */
  public static final String DECRYPTION_TYPED_OBJECT_ERROR_CODE = "DLB_0015";

  /** Error message for decryption typed object errors. */
  public static final String DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE = "Não foi possível processar a solicitação de segurança devido a um valor inválido. Por favor, verifique os dados fornecidos e tente novamente. Se o problema persistir, entre em contato com o suporte comercial.";

  /** Error code for decryption string value errors. */
  public static final String DECRYPTION_STRING_VALUE_ERROR_CODE = "DLB_0016";

  /** Error message for decryption string value errors. */
  public static final String DECRYPTION_STRING_VALUE_ERROR_MESSAGE = DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE;

  /** Error code for decryption bytes value errors. */
  public static final String DECRYPTION_BYTES_VALUE_ERROR_CODE = "DLB_0017";

  /** Error message for decryption bytes value errors. */
  public static final String DECRYPTION_BYTES_VALUE_ERROR_MESSAGE = DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE;

  /** Error code for encryption errors. */
  public static final String ENCRYPTION_ERROR_CODE = "DLB_0018";

  /** Error message for encryption errors. */
  public static final String ENCRYPTION_ERROR_MESSAGE = DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE;

  /** Error code for encryption by encrypted object errors. */
  public static final String ENCRYPTION_BY_ENCRYPTED_OBJECT_ERROR_CODE = "DLB_0019";

  /** Error message for encryption by encrypted object errors. */
  public static final String ENCRYPTION_BY_ENCRYPTED_OBJECT_ERROR_MESSAGE = DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE;

  /** Error code for illegal jwt string class exceptions. */
  public static final String ILLEGAL_JWT_STRING_CLASS_ERROR_CODE = "DLB_0020";

  /** Error message for illegal jwt string class exceptions. */
  public static final String ILLEGAL_JWT_STRING_CLASS_ERROR_MESSAGE = "Ocorreu um erro ao processar a solicitação de transação. Por favor, tente novamente. Se o problema persistir, entre em contato com o suporte comercial.";

  /** Error code for illegal jwt claim class exceptions. */
  public static final String ILLEGAL_JWT_CLAIM_CLASS_ERROR_CODE = "DLB_0021";

  /** Error message for illegal jwt claim class exceptions. */
  public static final String ILLEGAL_JWT_CLAIM_CLASS_ERROR_MESSAGE = ILLEGAL_JWT_STRING_CLASS_ERROR_MESSAGE;

  /** Error code for illegal jwt token exceptions. */
  public static final String ILLEGAL_JWT_TOKEN_ERROR_CODE = "DLB_0022";

  /** Error message for illegal jwt token exceptions. */
  public static final String ILLEGAL_JWT_TOKEN_ERROR_MESSAGE = ILLEGAL_JWT_STRING_CLASS_ERROR_MESSAGE;

  /** Error code for illegal jwt token decode exceptions. */
  public static final String ILLEGAL_JWT_TOKEN_DECODE_ERROR_CODE = "DLB_0023";

  /** Error message for illegal jwt token decode exceptions. */
  public static final String ILLEGAL_JWT_TOKEN_DECODE_ERROR_MESSAGE = ILLEGAL_JWT_STRING_CLASS_ERROR_MESSAGE;

  /** Error code for no encrypted object def exceptions. */
  public static final String NO_ENCRYPTED_OBJECT_DEF_ERROR_CODE = "DLB_0024";

  /** Error message for no encrypted object def exceptions. */
  public static final String NO_ENCRYPTED_OBJECT_DEF_ERROR_MESSAGE = DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE;

}
