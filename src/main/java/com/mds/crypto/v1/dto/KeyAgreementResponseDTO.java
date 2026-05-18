package com.mds.crypto.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Key Agreement Response. This class encapsulates the response data
 * from a key agreement operation.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAgreementResponseDTO {

  /** Exported AES key resulting from the key agreement operation. */
  private String aesExportedKey;

  /** Context identifier associated with the key agreement response. */
  private String contextId;

  /** Encrypted key resulting from the key agreement operation. */
  private String encryptedKey;

  /** Time-to-live (TTL) for the key, in seconds. */
  private int keyTTL;

  /** Message providing additional information about the response. */
  private String message;

  /** Return code indicating the status of the key agreement operation. */
  private int retCode;

  /** Exported RSA key resulting from the key agreement operation. */
  private String rsaExportedKey;

  /** Public key of the server involved in the key agreement operation. */
  private String serverPublicKey;

  /** Translated key resulting from the key agreement operation. */
  private String translatedKey;
}
