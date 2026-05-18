package com.mds.crypto.v1.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for ECC Translate String Request. This class encapsulates the data
 * required to perform an ECC translation request.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EccTranslateStringRequestDTO {

  /** List of cipher string data to be translated. */
  private List<CipherStringDataDTO> cipherStringData;

  /** Primary context identifier for the translation request. */
  private String contextId;

  /** Secondary context identifier for the translation request. */
  private String contextId2;

  /** Flag indicating whether the data is in EBCDIC format. */
  private boolean ebcdic;

  /** Flag indicating whether the request originates from the backend. */
  private boolean fromBackend;

  /** Key agreement identifier used in the translation process. */
  private int keyAgreement;

  /** System identifier associated with the translation request. */
  private String sistema;

  /**
   * Adds a cipher string data entry to the list. If the list is null, it initializes a new list
   * before adding the entry.
   *
   * @param cipherStringDataDTO The cipher string data to be added.
   * @return The updated instance of `EccTranslateStringRequestDTO`.
   */
  public EccTranslateStringRequestDTO addCipherStringData(CipherStringDataDTO cipherStringDataDTO) {
    if (this.cipherStringData == null) {
      this.cipherStringData = new java.util.ArrayList<>();
    }
    this.cipherStringData.add(cipherStringDataDTO);
    return this;
  }
}
