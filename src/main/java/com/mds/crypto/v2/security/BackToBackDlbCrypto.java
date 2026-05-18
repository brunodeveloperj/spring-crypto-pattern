package com.mds.crypto.v2.security;

import com.mds.crypto.v2.enumeration.ClientMode;

/**
 * DLB crypto implementation for the <em>Back-to-Back</em> channel.
 *
 * <p>Used for inter-service communication where both sides share a
 * pre-negotiated encrypted object without front-end involvement.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 * @see AbstractDlbCrypto
 */
public final class BackToBackDlbCrypto extends AbstractDlbCrypto {

  /**
   * Specifies the client mode as BACK_TO_BACK.
   *
   * @return the client mode as {@link ClientMode#BACK_TO_BACK}.
   */
  @Override
  ClientMode clientMode() {
    return ClientMode.BACK_TO_BACK;
  }
}
