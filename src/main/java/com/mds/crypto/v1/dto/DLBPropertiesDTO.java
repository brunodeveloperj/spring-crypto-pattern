package com.mds.crypto.v1.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for DLB properties. This class encapsulates the configuration
 * properties required for DLB operations.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Data
public class DLBPropertiesDTO {

  /** URL used to initialize the DLB service. */
  private String initializeURL;

  /** URL used to translate data in the DLB service. */
  private String translateURL;

  /** System identifier associated with the DLB configuration. */
  private String system;

  /** File path to the certificate used for secure communication. */
  private String pathCertificate;

  /** Password for the certificate file. */
  private String pathCertificatePassword;
}
