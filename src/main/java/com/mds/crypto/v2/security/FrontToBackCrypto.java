package com.mds.crypto.v2.security;

import com.mds.crypto.v2.enumeration.ClientMode;

/**
 * DLB crypto implementation for the <em>Front-to-Back</em> channel.
 *
 * <p>Used when the request originates from a front-end application and
 * the encrypted object was negotiated via a key-agreement handshake
 * between the client and the DLB service. The DLB client flag is
 * <strong>not</strong> set during operations (server-side mode).
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 * @see AbstractDlbCrypto
 */
public final class FrontToBackCrypto extends AbstractDlbCrypto {

  /**
   * Specifies the client mode as FRONT_TO_BACK.
   *
   * @return the client mode as {@link ClientMode#FRONT_TO_BACK}.
   */
  @Override
  ClientMode clientMode() {
    return ClientMode.FRONT_TO_BACK;
  }
}
