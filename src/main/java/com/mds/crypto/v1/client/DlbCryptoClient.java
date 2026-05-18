package com.mds.crypto.v1.client;

import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0001;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0002;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0003;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0004;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0005;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0006;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0007;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_MESSAGE;
import static com.mds.error.handler.enumerator.Action.BACK_HOME;
import static com.mds.error.handler.enumerator.Type.PRODUCT;
import static com.mds.error.handler.exception.keys.ExceptionMessageKeys.DEFAULT_ERROR_TITLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.mds.crypto.v1.stub.DLCrypto;
import com.mds.crypto.v1.client.config.DLBConfig;
import com.mds.crypto.v1.client.config.DLBRestTemplateConfig;
import com.mds.crypto.v1.dto.CipherStringDataDTO;
import com.mds.crypto.v1.dto.EccTranslateStringRequestDTO;
import com.mds.crypto.v1.dto.EccTranslateStringResponseDTO;
import com.mds.crypto.v1.dto.KeyAgreementRequestDTO;
import com.mds.crypto.v1.dto.KeyAgreementResponseDTO;
import com.mds.crypto.v1.dto.TranslateRequestDTO;
import com.mds.error.handler.exception.GeneralException;
import com.mds.error.handler.utils.ErrorUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * Class responsible for interacting with the DLB service for cryptographic operations and data translation.
 *
 * <p>This class uses configurations defined in `DLBConfig` and `DLBRestTemplateConfig` to perform
 * secure REST calls and manage cryptographic operations with the `DLCrypto` library.
 *
 * <p>Annotations:
 *
 * <ul>
 *   <li>@Slf4j: Enables logging within the class.
 *   <li>@Component: Marks this class as a Spring-managed component.
 *   <li>@RequiredArgsConstructor: Generates a constructor with required arguments (final fields).
 * </ul>
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DlbCryptoClient {

  private final DLBConfig dlbConfig;
  private final ErrorUtils errorUtil;
  private final DLBRestTemplateConfig dlbRestTemplateConfig;

  /**
   * Initializes the key agreement process using `DLCrypto`.
   *
   * @param dlCrypto Instance of `DLCrypto` to perform the operation.
   * @return `KeyAgreementResponseDTO` object containing key agreement data.
   * @throws GeneralException If an error occurs during the operation.
   */
  public KeyAgreementResponseDTO keyAgreementInitialize(DLCrypto dlCrypto) throws GeneralException {
    // Call the method that performs the REST call to initialize the key agreement
    KeyAgreementResponseDTO keyAgreementResponseDTO = callingKeyAgreementInitialize(dlCrypto);

    // If the response is null, log a warning and throw a product exception
    if (keyAgreementResponseDTO == null) {
      log.warn("[DLBCryptoClient] - KeyAgreementResponseDTO is null, throwing error.");
      handleProductException(null, DLB_0002);
    }

    // Return the key agreement response DTO
    return keyAgreementResponseDTO;
  }

  /**
   * Translates encrypted data using `DLCrypto`.
   *
   * @param translateRequestDTO Object containing data for translation.
   * @param dlCrypto            Instance of `DLCrypto` to perform the operation.
   * @return String containing the translated and serialized data.
   * @throws GeneralException If an error occurs during the operation.
   */
  public String eccCipherTranslateStringData(TranslateRequestDTO translateRequestDTO, DLCrypto dlCrypto) throws GeneralException {
    // Call the method that performs the REST call to translate the encrypted data
    EccTranslateStringResponseDTO response = callingEccTranslateStringData(translateRequestDTO);

    // Extract the cipher content from the response or throw an exception if it is empty
    String cipherContent = extractCipherContentOrThrow(response);

    // Serialize the DLCrypto object with the imported key and return the Base64 string
    return serializeCryptoWithKey(dlCrypto, cipherContent);
  }

  /**
   * Creates a request object for the key agreement process.
   *
   * @param dlCrypto Instance of `DLCrypto` to generate the client's public key.
   * @return Configured `KeyAgreementRequestDTO` object.
   * @throws NoSuchAlgorithmException If the key generation algorithm is not found.
   */
  private KeyAgreementRequestDTO createKeyAgreementRequest(DLCrypto dlCrypto) throws NoSuchAlgorithmException {
    // Create a new KeyAgreementRequestDTO object
    KeyAgreementRequestDTO keyAgreementRequestDTO = new KeyAgreementRequestDTO();

    // Set the system identifier from configuration
    keyAgreementRequestDTO.setSistema(dlbConfig.getSystem());

    // Indicate that the key should be exported
    keyAgreementRequestDTO.setExportKey(true);

    // Generate and set the client's public key using DLCrypto
    keyAgreementRequestDTO.setClientPublicKey(dlCrypto.keyAgreementInitializePhase1());

    // Return the configured request DTO
    return keyAgreementRequestDTO;
  }

  /**
   * Makes a REST call to initialize the key agreement process.
   *
   * @param dlCrypto Instance of `DLCrypto` to generate request data.
   * @return `KeyAgreementResponseDTO` object containing the service response.
   * @throws GeneralException If an error occurs during the call.
   */
  private KeyAgreementResponseDTO callingKeyAgreementInitialize(DLCrypto dlCrypto) throws GeneralException {
    // Initialize the response object as null
    KeyAgreementResponseDTO keyAgreementResponseDTO = null;
    try {
      // Perform the REST call to initialize the key agreement and assign the response
      keyAgreementResponseDTO = dlbRestTemplateConfig.restTemplateSecurity().postForObject(dlbConfig.getInitializeURL(), new HttpEntity<>(createKeyAgreementRequest(dlCrypto), createHeader()), KeyAgreementResponseDTO.class);
      // Return the response if successful
      return keyAgreementResponseDTO;
    } catch (RestClientException e) {
      // Log the RestClientException and handle the product exception
      log.error("[DLBCryptoClient] - RestClientException during key agreement initialization.", e);
      handleProductException(e, DLB_0002);
    } catch (IOException | NoSuchAlgorithmException e) {
      // Log other exceptions and handle the product exception
      log.error("[DLBCryptoClient] - Exception during key agreement initialization.", e);
      handleProductException(e, DLB_0001);
    }
    // Return the response object (will be null if an exception occurred)
    return keyAgreementResponseDTO;
  }

  /**
   * Makes a REST call to translate encrypted data.
   *
   * @param translateRequestDTO Object containing data for translation.
   * @return `EccTranslateStringResponseDTO` object containing the service response.
   * @throws GeneralException If an error occurs during the call.
   */
  private EccTranslateStringResponseDTO callingEccTranslateStringData(TranslateRequestDTO translateRequestDTO) throws GeneralException {
    // Initialize the response object
    EccTranslateStringResponseDTO eccTranslateStringResponse = new EccTranslateStringResponseDTO();
    try {
      // Create the request object and add cipher string data
      var request = createEccTranslateStringRequestDTO(translateRequestDTO).addCipherStringData(createCipherStringDataDTO(translateRequestDTO));

      // Perform the REST call to translate the encrypted data and assign the response
      var response = dlbRestTemplateConfig.restTemplateSecurity().postForObject(dlbConfig.getTranslateURL(), new HttpEntity<>(request, createHeader()), EccTranslateStringResponseDTO.class);

      eccTranslateStringResponse = (response != null) ? response : new EccTranslateStringResponseDTO();
      // Return the response if successful
      return eccTranslateStringResponse;
    } catch (RestClientException e) {
      // Log the RestClientException and set the return code in the response
      log.error("[DLBCryptoClient] - RestClientException during cipher string translation.", e);
      eccTranslateStringResponse.setRetCode(getRetCode(e.getMessage()));

      // If the return code is not zero, log and handle the product exception
      if (eccTranslateStringResponse.getRetCode() != 0) {
        log.error("[DLBCryptoClient] - Non-zero retCode during cipher string translation.");
        handleProductException(e, DLB_0003);
      }
    } catch (IOException e) {
      // Log IOException and handle the product exception
      log.error("[DLBCryptoClient] - IOException during cipher string translation.", e);
      handleProductException(e, DLB_0004);
    }
    // Return the response object (may contain error information)
    return eccTranslateStringResponse;
  }

  /**
   * Creates a request object for encrypted data translation.
   *
   * @param translateRequestDTO Object containing data for translation.
   * @return Configured `EccTranslateStringRequestDTO` object.
   */
  private EccTranslateStringRequestDTO createEccTranslateStringRequestDTO(TranslateRequestDTO translateRequestDTO) {
    // Create a new EccTranslateStringRequestDTO object
    EccTranslateStringRequestDTO translateRequest = new EccTranslateStringRequestDTO();

    // Set the first context ID from the request DTO
    translateRequest.setContextId(translateRequestDTO.getContextId1());

    // Set the second context ID from the request DTO
    translateRequest.setContextId2(translateRequestDTO.getContextId2());

    // Indicate that the request is from the backend
    translateRequest.setFromBackend(true);

    // Set the system identifier from configuration
    translateRequest.setSistema(dlbConfig.getSystem());

    // Return the configured request DTO
    return translateRequest;
  }

  /**
   * Creates a `CipherStringDataDTO` object with encrypted data.
   *
   * @param translateRequestDTO Object containing data for translation.
   * @return Configured `CipherStringDataDTO` object.
   */
  private CipherStringDataDTO createCipherStringDataDTO(TranslateRequestDTO translateRequestDTO) {
    // Create a new CipherStringDataDTO object
    CipherStringDataDTO cipherStringDataDTO = new CipherStringDataDTO();

    // Set a fixed ID for the cipher string data
    cipherStringDataDTO.setId("id");

    // Set the encrypted key content from the request DTO
    cipherStringDataDTO.setContent(translateRequestDTO.getEncryptedKey());

    // Return the configured CipherStringDataDTO object
    return cipherStringDataDTO;
  }

  /**
   * Creates HTTP headers for REST requests.
   *
   * @return Configured `HttpHeaders` object.
   */
  private HttpHeaders createHeader() {
    // Create a new HttpHeaders object
    HttpHeaders headers = new HttpHeaders();

    // Set the content type to application/json
    headers.setContentType(APPLICATION_JSON);

    // Return the configured HttpHeaders object
    return headers;
  }

  /**
   * Extracts the encrypted content from the response or throws an exception if empty.
   *
   * @param response `EccTranslateStringResponseDTO` object containing the service response.
   * @return String containing the encrypted content.
   * @throws GeneralException If the content is empty or null.
   */
  private String extractCipherContentOrThrow(EccTranslateStringResponseDTO response) throws GeneralException {
    // Check if the cipher string data is null or empty
    if (response == null || response.getCipherStringData() == null || response.getCipherStringData().isEmpty()) {
      log.error("[DLBCryptoClient] - CipherStringData is null or empty");
      // Handle the product exception and return null
      return handleProductException(null, DLB_0005);
    }
    // Return the content of the first cipher string data entry
    return response.getCipherStringData().get(0).getContent();
  }

  /**
   * Imports a cryptographic key into `DLCrypto`.
   *
   * @param dlCrypto Instance of `DLCrypto` to import the key.
   * @param key      Cryptographic key to be imported.
   * @throws GeneralException If an error occurs during the import.
   */
  private void importKey(DLCrypto dlCrypto, String key) throws GeneralException {
    // Try to import the cryptographic key into the DLCrypto instance
    try {
      dlCrypto.importKey(key);
    } catch (Exception e) {
      // Log the exception and handle the product exception
      log.error("[DLBCryptoClient] - Exception importing key into DLCrypto.", e);
      handleProductException(e, DLB_0006);
    }
  }

  /**
   * Serializes the `DLCrypto` object with the imported key.
   *
   * @param dlCrypto      Instance of `DLCrypto` to serialize.
   * @param cipherContent Encrypted content to be used.
   * @return String containing the serialized object in Base64.
   * @throws GeneralException If an error occurs during serialization.
   */
  private String serializeCryptoWithKey(DLCrypto dlCrypto, String cipherContent) throws GeneralException {
    // Import the cryptographic key into the DLCrypto instance
    importKey(dlCrypto, cipherContent);
    try {
      // Serialize the DLCrypto object and encode it as a Base64 string
      try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ObjectOutputStream objOut = new ObjectOutputStream(out)) {
        objOut.writeObject(dlCrypto);
        return Base64.encodeBase64String(out.toByteArray());
      }
    } catch (IOException e) {
      // Log the IOException and handle the product exception
      log.error("[DLBCryptoClient] - IOException during serialization of DLCrypto.", e);
      return handleProductException(e, DLB_0007);
    }
  }

  /**
   * Retrieves the return code from an error message.
   *
   * @param message Error message containing the return code.
   * @return Extracted return code or -1 if not found.
   */
  private Integer getRetCode(String message) {
    // Check if the message contains the return code
    if (message != null && message.contains("retCode")) {
      try {
        // Extract and parse the return code from the message
        String code = message.substring(message.indexOf("retCode\":") + 9).replace("}]", "");
        return Integer.valueOf(code);
      } catch (NumberFormatException exception) {
        // Log if the code format is unexpected
        log.error("[DLBCryptoClient] - Unexpected error code format: {}", message);
      }
    }
    // Return -1 if the code is not found or parsing fails
    return -1;
  }

  /**
   * Handles product exceptions and throws a `GeneralException`.
   *
   * @param e          Original exception (can be null).
   * @param messageKey Error message key.
   * @return Always returns null.
   * @throws GeneralException Always throws this exception.
   */
  private String handleProductException(Exception e, String messageKey) throws GeneralException {
    // Log the exception if it is not null
    if (e != null) {
      log.error("[DLBCryptoClient] - Exception handled", e);
    }
    // Throw a GeneralException using the error utility
    errorUtil.throwError(messageKey, DLB_MESSAGE, DEFAULT_ERROR_TITLE, PRODUCT, BACK_HOME, UNAUTHORIZED.value());
    return null;
  }
}
