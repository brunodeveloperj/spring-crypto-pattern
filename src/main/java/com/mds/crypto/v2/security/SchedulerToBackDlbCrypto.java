package com.mds.crypto.v2.security;

import com.mds.crypto.v2.enumeration.ClientMode;

/**
 * DLB crypto implementation for the <em>Scheduler-to-Back</em> channel.
 *
 * <p>Used when a scheduled job or batch process communicates with a
 * back-end service and an encrypted object is required. Crypto
 * operations bypass the client flag entirely.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 * @see AbstractDlbCrypto
 */
public final class SchedulerToBackDlbCrypto extends AbstractDlbCrypto {

  /**
   * Specifies the client mode as SCHEDULER_TO_BACK.
   *
   * @return the client mode as {@link ClientMode#SCHEDULER_TO_BACK}.
   */
  @Override
  ClientMode clientMode() {
    return ClientMode.SCHEDULER_TO_BACK;
  }
}
