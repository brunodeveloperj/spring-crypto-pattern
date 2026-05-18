package com.mds.crypto.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a cipher string data. This class is used to encapsulate
 * the content and identifier of a cipher string.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CipherStringDataDTO {

  /** The content of the cipher string. */
  private String content;

  /** The identifier of the cipher string. */
  private String id;
}
