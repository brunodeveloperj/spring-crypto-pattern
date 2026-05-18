package com.mds.crypto.v1.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for DLB Manager authentication properties.
 *
 * <p>This class is used to map properties defined in the application's configuration file (e.g.,
 * `application.yml` or `application.properties`) under the prefix `auth.dlb-manager`.
 *
 * <p>Example configuration:
 *
 * <pre>
 * auth.dlb-manager:
 *   initializeURL: "https://example.com/initialize"
 *   translateURL: "https://example.com/translate"
 *   system: "exampleSystem"
 *   pathCertificate: "/path/to/certificate"
 *   pathCertificatePassword: "password123"
 * </pre>
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties("auth.dlb-manager")
public class DLBConfig {

  /** URL for the initialization endpoint. */
  private String initializeURL;

  /** URL for the translation endpoint. */
  private String translateURL;

  /** Identifier for the system using the DLB Manager. */
  private String system;

  /** Path to the certificate file used for authentication. */
  private String pathCertificate;

  /** Password for the certificate file. */
  private String pathCertificatePassword;

  public boolean hasValidCertificate() {
    return pathCertificate != null
        && !pathCertificate.isBlank()
        && pathCertificatePassword != null
        && !pathCertificatePassword.isBlank();
  }
}
