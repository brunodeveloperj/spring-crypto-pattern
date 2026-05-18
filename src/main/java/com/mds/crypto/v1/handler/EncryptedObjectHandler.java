package com.mds.crypto.v1.handler;

import static com.mds.crypto.v1.keys.MessagesKeys.DLB_0008;
import static com.mds.crypto.v1.keys.MessagesKeys.DLB_MESSAGE;
import static com.mds.error.handler.enumerator.Action.BACK_HOME;
import static com.mds.error.handler.enumerator.Type.TECHNICAL;
import static com.mds.error.handler.exception.keys.ExceptionMessageKeys.DEFAULT_ERROR_TITLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.mds.crypto.v1.stub.DLCrypto;
import com.mds.crypto.v1.client.DlbCryptoClient;
import com.mds.crypto.v1.dto.KeyAgreementResponseDTO;
import com.mds.crypto.v1.dto.TranslateRequestDTO;
import com.mds.error.handler.exception.GeneralException;
import com.mds.error.handler.utils.ErrorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler responsible for managing encrypted objects.
 *
 * <p>Orchestrates a two-phase ECC key agreement with the DLB service
 * via {@link DlbCryptoClient}: initializes two independent
 * {@link DLCrypto} instances, exchanges public keys, and translates
 * the resulting encrypted key into a serialised Base64 object that
 * can later be used by {@link CryptoHandler} for encrypt/decrypt
 * operations.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptedObjectHandler {

  private final ErrorUtils errorUtil;
  private final DlbCryptoClient dlbCryptoClient;

  /**
   * Creates an encrypted object and performs the translation of encrypted data.
   *
   * @return A string representing the result of the encrypted data translation.
   * @throws GeneralException If an error occurs during the creation process.
   */
  public String create() throws GeneralException {
    DLCrypto dlCrypto = new DLCrypto();

    KeyAgreementResponseDTO keyAgreementFirst = dlbCryptoClient.keyAgreementInitialize(dlCrypto);
    String encryptedKey = keyAgreementFirst.getEncryptedKey();
    String contextId = keyAgreementFirst.getContextId();

    DLCrypto dlcCryptoBack = dlCryptoClient();

    KeyAgreementResponseDTO keyAgreementSecond = dlbCryptoClient.keyAgreementInitialize(dlcCryptoBack);
    String contextId2 = keyAgreementSecond.getContextId();
    String serverPublicKey = keyAgreementSecond.getServerPublicKey();

    keyAgreementInitialize(dlcCryptoBack, serverPublicKey);

    return dlbCryptoClient.eccCipherTranslateStringData(new TranslateRequestDTO(contextId, contextId2, encryptedKey), dlcCryptoBack);
  }

  /**
   * Initializes the second phase of the key agreement using the server's public key.
   *
   * @param dlCrypto        The DLCrypto object to be initialized.
   * @param serverPublicKey The server's public key.
   * @throws GeneralException If an error occurs during initialization.
   */
  private void keyAgreementInitialize(DLCrypto dlCrypto, String serverPublicKey) throws GeneralException {
    try {
      dlCrypto.keyAgreementInitializePhase2(serverPublicKey);
    } catch (Exception e) {
      log.error("[EncryptedObjectHandler] - Error initializing key agreement.", e);
      errorUtil.throwError(DLB_0008, DLB_MESSAGE, DEFAULT_ERROR_TITLE, TECHNICAL, BACK_HOME, UNAUTHORIZED.value());
    }
  }

  /**
   * Creates and configures a new DLCrypto client instance.
   *
   * @return A DLCrypto object configured as a client.
   */
  private DLCrypto dlCryptoClient() {
    var dlCrypto = new DLCrypto();
    dlCrypto.setClient(true);
    return dlCrypto;
  }
}
