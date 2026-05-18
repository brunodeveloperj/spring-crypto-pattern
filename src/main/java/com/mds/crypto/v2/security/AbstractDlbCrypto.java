package com.mds.crypto.v2.security;

import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.CRYPTO_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.CRYPTO_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.DECRYPTION_BYTES_VALUE_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.DECRYPTION_BYTES_VALUE_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.DECRYPTION_STRING_VALUE_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.DECRYPTION_STRING_VALUE_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.DECRYPTION_TYPED_OBJECT_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ENCRYPTION_BY_ENCRYPTED_OBJECT_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ENCRYPTION_BY_ENCRYPTED_OBJECT_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ENCRYPTION_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.ENCRYPTION_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.INACCESSIBLE_OBJECT_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.INACCESSIBLE_OBJECT_ERROR_MESSAGE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.NO_ENCRYPTED_OBJECT_DEF_ERROR_CODE;
import static com.mds.crypto.v2.exception.keys.DLBExceptionKeys.NO_ENCRYPTED_OBJECT_DEF_ERROR_MESSAGE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import com.mds.crypto.v1.stub.DLBCryptoLoader;
import com.mds.crypto.v1.stub.DLCrypto;
import com.mds.crypto.v2.enumeration.ClientMode;
import com.mds.crypto.v2.exception.CryptoException;
import com.mds.crypto.v2.exception.DecryptionException;
import com.mds.crypto.v2.exception.EncryptionException;
import com.mds.crypto.v2.exception.InaccessibleObjectException;
import com.mds.crypto.v2.exception.NoEncryptedObjectDefException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Sealed abstract base providing encrypt/decrypt operations over the DLB
 * (Data Layer Bridge) cryptographic library.
 *
 * <p>Supports encryption and decryption of plain strings, byte arrays,
 * {@link Map Map&lt;String,String&gt;} dictionaries, and arbitrary
 * {@link java.io.Serializable Serializable} typed objects via reflective
 * field traversal. Each concrete subclass ({@link FrontToBackCrypto},
 * {@link BackToBackDlbCrypto}, {@link SchedulerToBackDlbCrypto}) declares
 * its {@link ClientMode} which controls the DLB client flag during
 * crypto operations.
 *
 * <p>An {@code encryptedObject} (serialised DLB session) must be set
 * before bulk or typed-object operations; otherwise
 * {@link com.mds.crypto.v2.exception.NoEncryptedObjectDefException} is thrown.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@Slf4j
public abstract sealed class AbstractDlbCrypto permits BackToBackDlbCrypto, FrontToBackCrypto, SchedulerToBackDlbCrypto {

  private String encryptedObject;

  /**
   * Abstract method to determine the client mode.
   *
   * @return the client mode as an instance of {@link ClientMode}.
   */
  abstract ClientMode clientMode();

  /**
   * Checks if the client mode is SCHEDULER_TO_BACK.
   *
   * @return true if the client mode is SCHEDULER_TO_BACK, false otherwise.
   */
  public boolean isClientSchedulerToBack() {
    return clientMode() == ClientMode.SCHEDULER_TO_BACK;
  }

  /**
   * Checks if the client mode is BACK_TO_BACK.
   *
   * @return true if the client mode is BACK_TO_BACK, false otherwise.
   */
  public boolean isClientBackToBack() {
    return clientMode() == ClientMode.BACK_TO_BACK;
  }

  /**
   * Checks if the client mode is FRONT_TO_BACK.
   *
   * @return true if the client mode is FRONT_TO_BACK, false otherwise.
   */
  public boolean isClientFrontToBack() {
    return clientMode() == ClientMode.FRONT_TO_BACK;
  }

  // ========================================
  // ========= ENCRYPT (Session) ============
  // ========================================

  /**
   * Encrypts a given text using the current encrypted object.
   *
   * @param text the text to be encrypted.
   * @return the encrypted text.
   */
  public String encrypt(String text) {
    return encrypt(getEncryptedObject(), text, Boolean.FALSE, Boolean.FALSE);
  }

  /**
   * Encrypts a given text using a specified encrypted object.
   *
   * @param text            the text to be encrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the encrypted text.
   */
  public String encrypt(String text, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return encrypt(text);
  }

  /**
   * Encrypts a given text with encoding enabled and client mode disabled.
   *
   * @param text the text to be encrypted.
   * @return the encrypted text.
   */
  public String encryptByEncodedAndNotClient(String text) {
    return encrypt(getEncryptedObject(), text, Boolean.TRUE, Boolean.FALSE);
  }

  /**
   * Encrypts a given text with encoding enabled and client mode disabled, using a specified encrypted object.
   *
   * @param text            the text to be encrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the encrypted text.
   */
  public String encryptByEncodedAndNotClient(String text, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return encryptByEncodedAndNotClient(text);
  }

  /**
   * Encrypts a given text with encoding disabled and client mode enabled.
   *
   * @param text the text to be encrypted.
   * @return the encrypted text.
   */
  public String encryptByNotEncodedAndClient(String text) {
    return encrypt(getEncryptedObject(), text, Boolean.FALSE, Boolean.TRUE);
  }

  /**
   * Encrypts a given text with encoding disabled and client mode enabled, using a specified encrypted object.
   *
   * @param text            the text to be encrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the encrypted text.
   */
  public String encryptByNotEncodedAndClient(String text, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return encryptByNotEncodedAndClient(text);
  }

  /**
   * Encrypts a given text with both encoding and client mode enabled.
   *
   * @param text the text to be encrypted.
   * @return the encrypted text.
   */
  public String encryptByEncodedAndClient(String text) {
    return encrypt(getEncryptedObject(), text, Boolean.TRUE, Boolean.TRUE);
  }

  /**
   * Encrypts a given text with both encoding and client mode enabled, using a specified encrypted object.
   *
   * @param text            the text to be encrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the encrypted text.
   */
  public String encryptByEncodedAndClient(String text, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return encryptByEncodedAndClient(text);
  }

  /**
   * Encrypts all values in a given dictionary.
   *
   * @param dictionary the dictionary containing values to be encrypted.
   * @return the dictionary with encrypted values.
   */
  public Map<String, String> encrypt(Map<String, String> dictionary) {
    validateEncryptedObject();
    dictionary.replaceAll((key, value) -> encrypt(value));
    return dictionary;
  }

  /**
   * Encrypts all values in a given dictionary using a specified encrypted object.
   *
   * @param dictionary      the dictionary containing values to be encrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the dictionary with encrypted values.
   */
  public Map<String, String> encrypt(Map<String, String> dictionary, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return encrypt(dictionary);
  }

  /**
   * Encrypts all fields of a given typed object.
   *
   * @param object the object to be encrypted.
   * @param <T>    the type of the object.
   * @return the encrypted object.
   */
  public <T extends Serializable> T encryptTypedObject(T object) {
    validateEncryptedObject();
    try {
      cryptoFields(object, this::encrypt);
      return object;
    } catch (Exception e) {
      throw new EncryptionException(ENCRYPTION_BY_ENCRYPTED_OBJECT_ERROR_CODE, ENCRYPTION_BY_ENCRYPTED_OBJECT_ERROR_MESSAGE, e);
    }
  }

  /**
   * Encrypts all fields of a given typed object using a specified encrypted object.
   *
   * @param object          the object to be encrypted.
   * @param encryptedObject the encrypted object to use.
   * @param <T>             the type of the object.
   * @return the encrypted object.
   */
  public <T extends Serializable> T encryptTypedObject(T object, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return encryptTypedObject(object);
  }

  /**
   * Encrypts a given value using the specified parameters.
   *
   * @param encryptedObject the encrypted object to use.
   * @param value           the value to be encrypted.
   * @param encoded         whether the result should be encoded.
   * @param encryptClient   whether the client mode should be enabled.
   * @return the encrypted value.
   */
  public String encrypt(String encryptedObject, String value, Boolean encoded, Boolean encryptClient) {
    try {
      DLCrypto dlCrypto = DLBCryptoLoader.deserialize(encryptedObject);

      if (encryptClient == Boolean.TRUE) {
        dlCrypto.setClient(true);
      }

      String encryptedData = dlCrypto.encrypt(value);
      if (encoded == Boolean.TRUE) {
        return String.valueOf(Base64.getUrlEncoder().encodeToString(encryptedData.getBytes(UTF_8)));
      }
      return encryptedData;
    } catch (Exception e) {
      log.error("(encrypt) - Error trying to encrypt the value: {}", value, e);
      throw new EncryptionException(ENCRYPTION_ERROR_CODE, ENCRYPTION_ERROR_MESSAGE, e);
    }
  }

  // ========================================
  // ========== DECRYPT (Session) ===========
  // ========================================

  /**
   * Decrypts a given text using the current encrypted object.
   *
   * @param text the text to be decrypted.
   * @return the decrypted text.
   */
  public String decrypt(String text) {
    return decrypt(getEncryptedObject(), text, Boolean.FALSE);
  }

  /**
   * Decrypts a given byte array using the current encrypted object.
   *
   * @param value the byte array to be decrypted.
   * @return the decrypted byte array.
   */
  public byte[] decryptToByteArray(byte[] value) {
    return decryptToByteArray(getEncryptedObject(), value, Boolean.FALSE);
  }

  /**
   * Decrypts a given text using a specified encrypted object.
   *
   * @param text            the text to be decrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the decrypted text.
   */
  public String decrypt(String text, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return decrypt(text);
  }

  /**
   * Decrypts a given byte array using a specified encrypted object.
   *
   * @param value           the byte array to be decrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the decrypted byte array.
   */
  public byte[] decryptToByteArray(byte[] value, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return decryptToByteArray(value);
  }

  /**
   * Decrypts a given text with client mode enabled.
   *
   * @param text the text to be decrypted.
   * @return the decrypted text.
   */
  public String decryptByClient(String text) {
    return decrypt(getEncryptedObject(), text, Boolean.TRUE);
  }

  /**
   * Decrypts a given byte array with client mode enabled.
   *
   * @param value the byte array to be decrypted.
   * @return the decrypted byte array.
   */
  public byte[] decryptToByteArrayByClient(byte[] value) {
    return decryptToByteArray(getEncryptedObject(), value, Boolean.TRUE);
  }

  /**
   * Decrypts a given text with client mode enabled, using a specified encrypted object.
   *
   * @param text            the text to be decrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the decrypted text.
   */
  public String decryptByClient(String text, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return decryptByClient(text);
  }

  public byte[] decryptToByteArrayByClient(byte[] value, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return decryptToByteArrayByClient(value);
  }

  /**
   * Decrypts all values in a given dictionary.
   *
   * @param dictionary the dictionary containing values to be decrypted.
   * @return the dictionary with decrypted values.
   */
  public Map<String, String> decrypt(Map<String, String> dictionary) {
    validateEncryptedObject();
    dictionary.replaceAll((key, value) -> decrypt(value));
    return dictionary;
  }

  /**
   * Decrypts all values in a given dictionary using a specified encrypted object.
   *
   * @param dictionary      the dictionary containing values to be decrypted.
   * @param encryptedObject the encrypted object to use.
   * @return the dictionary with decrypted values.
   */
  public Map<String, String> decrypt(Map<String, String> dictionary, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return decrypt(dictionary);
  }

  /**
   * Decrypts all fields of a given typed object.
   *
   * @param object the object to be decrypted.
   * @param <T>    the type of the object.
   * @return the decrypted object.
   */
  public <T extends Serializable> T decryptTypedObject(T object) {
    validateEncryptedObject();
    try {
      cryptoFields(object, this::decrypt);
      return object;
    } catch (Exception e) {
      throw new DecryptionException(DECRYPTION_TYPED_OBJECT_ERROR_CODE, DECRYPTION_TYPED_OBJECT_ERROR_MESSAGE, e);
    }
  }

  /**
   * Decrypts all fields of a given typed object using a specified encrypted object.
   *
   * @param object          the object to be decrypted.
   * @param encryptedObject the encrypted object to use.
   * @param <T>             the type of the object.
   * @return the decrypted object.
   */
  public <T extends Serializable> T decryptTypedObject(T object, String encryptedObject) {
    setEncryptedObject(encryptedObject);
    return decryptTypedObject(object);
  }

  /**
   * Decrypts a given value using the specified parameters.
   *
   * @param encryptedObject the encrypted object to use.
   * @param value           the value to be decrypted.
   * @param encryptClient   whether the client mode should be enabled.
   * @return the decrypted value.
   */
  public String decrypt(String encryptedObject, String value, Boolean encryptClient) {
    try {
      DLCrypto dlCrypto = DLBCryptoLoader.deserialize(encryptedObject);
      if (encryptClient == Boolean.TRUE) {
        dlCrypto.setClient(true);
      }
      return dlCrypto.decrypt(value);
    } catch (Exception e) {
      log.info("Error trying to decrypt the value: {}", value, e);
      throw new DecryptionException(DECRYPTION_STRING_VALUE_ERROR_CODE, DECRYPTION_STRING_VALUE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Decrypts a given byte array using the specified parameters.
   *
   * @param encryptedObject the encrypted object to use.
   * @param value           the byte array to be decrypted.
   * @param encryptClient   whether the client mode should be enabled.
   * @return the decrypted byte array.
   */
  public byte[] decryptToByteArray(String encryptedObject, byte[] value, Boolean encryptClient) {
    try {
      DLCrypto dlCrypto = DLBCryptoLoader.deserialize(encryptedObject);
      if (encryptClient == Boolean.TRUE) {
        dlCrypto.setClient(true);
      }
      return dlCrypto.decrypt(value);
    } catch (Exception e) {
      log.error("Error trying to decrypt the value", e);
      throw new DecryptionException(DECRYPTION_BYTES_VALUE_ERROR_CODE, DECRYPTION_BYTES_VALUE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Validates the presence of an encrypted object definition.
   *
   * @throws NoEncryptedObjectDefException if the encrypted object is not defined.
   */
  private void validateEncryptedObject() {
    if (getEncryptedObject() == null) {
      throw new NoEncryptedObjectDefException(NO_ENCRYPTED_OBJECT_DEF_ERROR_CODE, NO_ENCRYPTED_OBJECT_DEF_ERROR_MESSAGE);
    }
  }

  /**
   * Applies a cryptographic operation to the fields of a given object.
   *
   * @param object   the object to process.
   * @param operator the cryptographic operation to apply.
   * @param <T>      the type of the object.
   * @return the processed object.
   */
  @SuppressWarnings("unchecked")
  private <T> T cryptoFields(T object, UnaryOperator<String> operator) {
    if (object instanceof String text) {
      return (T) operator.apply(text);
    }
    if (object instanceof List<?> list) {
      return (T) list.stream().map(item -> cryptoFields(item, operator)).toList();
    }
    if (object instanceof Map<?, ?> uncheckedMap) {
      Map<String, Object> map = uncheckedMap.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
      map.values().removeAll(Collections.singleton(null));
      map.replaceAll((key, value) -> cryptoFields(value, operator));
      return (T) map;
    }
    if (!isDto(object.getClass()) && !(object instanceof Serializable)) {
      return object;
    }
    getFields(object).forEach(field -> cryptoField(object, field, operator));
    return object;
  }

  /**
   * Applies a cryptographic operation to a specific field of an object.
   *
   * @param serializable the object containing the field.
   * @param field        the field to process.
   * @param operator     the cryptographic operation to apply.
   */
  private void cryptoField(Object serializable, Field field, UnaryOperator<String> operator) {
    String object = (String) getAccessibleObject(serializable, field);
    try {
      field.set(serializable, operator.apply(object));
    } catch (IllegalAccessException e) {
      log.error("(cryptoField) - Error trying to access the field: {}", field.getName(), e);
      throw new CryptoException(CRYPTO_ERROR_CODE, CRYPTO_ERROR_MESSAGE,e);
    }
  }

  /**
   * Retrieves the value of a field from an object, making it accessible if necessary.
   *
   * @param object the object containing the field.
   * @param field  the field to retrieve.
   * @return the value of the field.
   */
  private Object getAccessibleObject(Object object, Field field) {
    makeAccessible(field);
    try {
      return field.get(object);
    } catch (IllegalAccessException e) {
      log.error("(getAccessibleObject) - Error trying to access the field: {}", field.getName(), e);
      throw new InaccessibleObjectException(INACCESSIBLE_OBJECT_ERROR_CODE, INACCESSIBLE_OBJECT_ERROR_MESSAGE, e);
    }
  }

  /**
   * Retrieves all non-static, non-final, and non-transient fields of an object.
   *
   * @param object the object to inspect.
   * @return a list of fields.
   */
  private List<Field> getFields(Object object) {
    return Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers()))).toList();
  }

  /**
   * Checks if a class represents a common type (e.g., primitive, java.\*, javax.\*).
   *
   * @param clazz the class to check.
   * @return true if the class is a common type, false otherwise.
   */
  public static boolean isCommonType(Class<?> clazz) {
    String packageName = clazz.getPackageName();
    return clazz.isPrimitive() || packageName.startsWith("java.") || packageName.startsWith("javax.");
  }

  /**
   * Checks if a class represents an enum.
   *
   * @param clazz the class to check.
   * @return true if the class is an enum, false otherwise.
   */
  public static boolean isEnum(Class<?> clazz) {
    return clazz.isEnum();
  }

  /**
   * Checks if a class represents a DTO (Data Transfer Object).
   *
   * @param clazz the class to check.
   * @return true if the class is a DTO, false otherwise.
   */
  public static boolean isDto(Class<?> clazz) {
    return !(isCommonType(clazz) || isEnum(clazz));
  }
}
