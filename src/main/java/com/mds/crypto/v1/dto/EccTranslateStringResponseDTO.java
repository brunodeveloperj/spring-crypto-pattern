package com.mds.crypto.v1.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for ECC Translate String Response. This class encapsulates the
 * response data from an ECC translation request.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EccTranslateStringResponseDTO {

  /** List of cipher string data returned in the response. */
  private List<CipherStringDataDTO> cipherStringData;

  /** Message providing additional information about the response. */
  private String message;

  /** Return code indicating the status of the translation request. */
  private int retCode;
}
