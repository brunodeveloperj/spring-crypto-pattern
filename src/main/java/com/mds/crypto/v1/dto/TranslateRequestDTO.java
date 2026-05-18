package com.mds.crypto.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Translate Request. This class encapsulates the data required for a
 * translation request.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateRequestDTO {

  /** Primary context identifier for the translation request. */
  private String contextId1;

  /** Secondary context identifier for the translation request. */
  private String contextId2;

  /** Encrypted key associated with the translation request. */
  private String encryptedKey;
}
