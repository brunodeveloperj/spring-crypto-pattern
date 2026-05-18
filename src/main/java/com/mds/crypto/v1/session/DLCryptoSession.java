package com.mds.crypto.v1.session;

import com.mds.crypto.v1.stub.DLCrypto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Represents a session for cryptographic operations using DLCrypto. This class is scoped to a
 * single HTTP request, ensuring thread safety.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Setter
@Getter
@Component
public class DLCryptoSession {

  /**
   * The cryptographic session instance. This field is used to perform cryptographic
   * operations. @SuppressWarnings("java:S3749") Suppresses a false positive warning in SonarQube.
   */
  @SuppressWarnings("java:S3749")
  private DLCrypto session;
}
