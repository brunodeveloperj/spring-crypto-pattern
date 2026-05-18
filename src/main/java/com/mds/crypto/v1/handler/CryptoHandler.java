package com.mds.crypto.v1.handler;

import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0009;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0010;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0011;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0012;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_MESSAGE;
import static com.mds.error.handler.enumerator.Action.RETRY_ON_STATE;
import static com.mds.error.handler.enumerator.Type.TECHNICAL;
import static com.mds.error.handler.exception.keys.ErrorStatusKeys.METHOD_FAILURE;
import static com.mds.error.handler.exception.keys.ExceptionMessageKeys.DEFAULT_ERROR_TITLE;
import static java.util.Objects.requireNonNull;

import com.mds.crypto.v1.stub.DLBCryptoLoader;
import com.mds.crypto.v1.stub.DLCrypto;
import com.mds.crypto.v1.session.DLCryptoSession;
import com.mds.error.handler.exception.GeneralException;
import com.mds.error.handler.utils.ErrorUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for cryptographic operations such as encryption and decryption.
 *
 * <p>Provides multiple overloads for encrypting and decrypting single values
 * or arrays, with optional Base64 URL-safe encoding and client/server mode
 * selection. Internally delegates to {@link DLBCryptoLoader} for
 * deserialization of the encrypted object and to
 * {@link EncryptedObjectHandler} when no pre-existing encrypted object is
 * supplied.
 *
 * <p>Errors during crypto operations are mapped to structured
 * {@link com.mds.error.handler.exception.GeneralException} responses via
 * {@link com.mds.error.handler.utils.ErrorUtils}.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoHandler {

  private static final String ERROR_ENCRYPTION_LOG = "[CryptoHandler] - Error during encryption.";
  private static final String ERROR_DECRYPTION_LOG = "[CryptoHandler] - Error during decryption.";

  private final ErrorUtils errorUtil;
  private final DLCryptoSession dlCryptoSession;
  private final EncryptedObjectHandler encryptedObjectHandler;

  /**
   * Encrypts a value using the provided encrypted object.
   *
   * @param encryptedObject The encrypted object used for encryption.
   * @param value           The value to be encrypted.
   * @return The encrypted value.
   * @throws GeneralException If an error occurs during encryption.
   */
  public String encrypt(String encryptedObject, String value) throws GeneralException {
    return encrypt(encryptedObject, value, Boolean.FALSE, Boolean.TRUE);
  }

  /**
   * Encrypts a value using the provided encrypted object with an encoding option.
   *
   * @param encryptedObject The encrypted object used for encryption.
   * @param value           The value to be encrypted.
   * @param isEncoded       Whether the result should be Base64 encoded.
   * @return The encrypted value.
   * @throws GeneralException If an error occurs during encryption.
   */
  public String encrypt(String encryptedObject, String value, Boolean isEncoded) throws GeneralException {
    return encrypt(encryptedObject, value, isEncoded, Boolean.TRUE);
  }

  /**
   * Encrypts an array of values using the provided encrypted object.
   *
   * @param encryptedObject The encrypted object used for encryption.
   * @param isEncoded       Whether the results should be Base64 encoded.
   * @param values          The array of values to be encrypted.
   * @return An array of encrypted values.
   * @throws GeneralException If an error occurs during encryption.
   */
  public String[] encryptArray(String encryptedObject, Boolean isEncoded, String... values) throws GeneralException {
    requireNonNull(values, "Provide valid values.");
    List<String> encrypted = new ArrayList<>();
    AtomicReference<GeneralException> exceptionRef = new AtomicReference<>();

    forEach(Arrays.stream(values).parallel(), (value, breaker) -> {
      try {
        encrypted.add(encrypt(encryptedObject, value, isEncoded, Boolean.TRUE));
      } catch (GeneralException ex) {
        exceptionRef.set(ex);
        breaker.stop();
      }
    });

    if (exceptionRef.get() != null) {
      throw exceptionRef.get();
    }
    return encrypted.toArray(String[]::new);
  }

  /**
   * Encrypts a value using a newly created encrypted object.
   *
   * @param value The value to be encrypted.
   * @return The encrypted value.
   * @throws GeneralException If an error occurs during encryption.
   */
  public String encrypt(String value) throws GeneralException {
    return encrypt(encryptedObjectHandler.create(), value, Boolean.FALSE, Boolean.TRUE);
  }

  /**
   * Encrypts a value using the provided encrypted object with additional options.
   *
   * @param encryptedObject The encrypted object used for encryption.
   * @param value           The value to be encrypted.
   * @param isEncoded       Whether the result should be Base64 encoded.
   * @param isEncryptClient Whether the encryption is client-side.
   * @return The encrypted value.
   * @throws GeneralException If an error occurs during encryption.
   */
  public String encrypt(String encryptedObject, String value, Boolean isEncoded, Boolean isEncryptClient) throws GeneralException {
    try {
      DLCrypto dlCrypto = DLBCryptoLoader.deserialize(encryptedObject);
      dlCryptoSession.setSession(dlCrypto);
      dlCrypto.setClient(isEncryptClient == Boolean.TRUE);

      return (isEncoded == Boolean.TRUE) ? Base64.getUrlEncoder().encodeToString(dlCrypto.encrypt(value).getBytes(StandardCharsets.UTF_8)) : dlCrypto.encrypt(value);
    } catch (IOException | ClassNotFoundException e) {
      log.error(ERROR_ENCRYPTION_LOG, e);
      errorUtil.throwError(DLB_0009, DLB_MESSAGE, DEFAULT_ERROR_TITLE, TECHNICAL, RETRY_ON_STATE, METHOD_FAILURE);
    } catch (Exception e) {
      log.error(ERROR_ENCRYPTION_LOG, e);
      errorUtil.throwError(DLB_0010, DLB_MESSAGE, DEFAULT_ERROR_TITLE, TECHNICAL, RETRY_ON_STATE, METHOD_FAILURE);
    }
    return value;
  }

  /**
   * Decrypts a value using the provided encrypted object.
   *
   * @param encryptedObject The encrypted object used for decryption.
   * @param value           The value to be decrypted.
   * @return The decrypted value.
   * @throws GeneralException If an error occurs during decryption.
   */
  public String decrypt(String encryptedObject, String value) throws GeneralException {
    return decrypt(encryptedObject, value, Boolean.TRUE);
  }

  /**
   * Decrypts a value using a newly created encrypted object.
   *
   * @param value The value to be decrypted.
   * @return The decrypted value.
   * @throws GeneralException If an error occurs during decryption.
   */
  public String decrypt(String value) throws GeneralException {
    return decrypt(encryptedObjectHandler.create(), value, Boolean.TRUE);
  }

  /**
   * Decrypts a value using the provided encrypted object with an encryption client option.
   *
   * @param encryptedObject The encrypted object used for decryption.
   * @param value           The value to be decrypted.
   * @param isEncryptClient Whether the decryption is client-side.
   * @return The decrypted value.
   * @throws GeneralException If an error occurs during decryption.
   */
  public String decrypt(String encryptedObject, String value, Boolean isEncryptClient) throws GeneralException {
    log.info("[CryptoHandler] - Decrypting value: {}", value);
    try {
      DLCrypto dlCrypto = DLBCryptoLoader.deserialize(encryptedObject);
      dlCrypto.setClient(isEncryptClient == Boolean.TRUE);
      return dlCrypto.decrypt(value);
    } catch (IOException | ClassNotFoundException e) {
      log.error(ERROR_DECRYPTION_LOG, e);
      errorUtil.throwError(DLB_0011, DLB_MESSAGE, DEFAULT_ERROR_TITLE, TECHNICAL, RETRY_ON_STATE, METHOD_FAILURE);
    } catch (Exception e) {
      log.error(ERROR_DECRYPTION_LOG, e);
      errorUtil.throwError(DLB_0012, DLB_MESSAGE, DEFAULT_ERROR_TITLE, TECHNICAL, RETRY_ON_STATE, METHOD_FAILURE);
    }
    return value;
  }

  /**
   * Iterates over a stream and applies a consumer with a breaker mechanism.
   *
   * @param stream   The stream to iterate over.
   * @param consumer The consumer to apply to each element.
   * @param <T>      The type of elements in the stream.
   */
  private static <T> void forEach(Stream<T> stream, BiConsumer<T, Breaker> consumer) {
    Spliterator<T> spliterator = stream.spliterator();
    Breaker breaker = new Breaker();
    while (spliterator.tryAdvance(elem -> consumer.accept(elem, breaker)) && !breaker.get()) {
    }
  }

  /**
   * Breaker class to control the flow of iteration.
   */
  private static class Breaker {

    private boolean shouldBreak = false;

    /**
     * Stops the iteration.
     */
    public void stop() {
      shouldBreak = true;
    }

    /**
     * Checks if the iteration should stop.
     *
     * @return True if the iteration should stop, false otherwise.
     */
    public boolean get() {
      return shouldBreak;
    }
  }
}
