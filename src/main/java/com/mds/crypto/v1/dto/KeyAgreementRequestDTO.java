package com.mds.crypto.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Key Agreement Request. This class encapsulates the data required
 * for a key agreement operation.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAgreementRequestDTO {

  /** Identifier for the AES exporter key. */
  private String aesExporterKeyIdentifier;

  /** Public key of the client. */
  private String clientPublicKey;

  /** Context identifier for the key agreement operation. */
  private String contextId;

  /** Flag indicating whether the key should be exported. */
  private boolean exportKey;

  /** Key translation data. */
  private String keyTranslate;

  /** Context identifier for the key translation. */
  private String keyTranslateContextId;

  /** RSA public key for the exporter. */
  private String rsaExporterPublicKey;

  /** System identifier associated with the key agreement request. */
  private String sistema;
}
