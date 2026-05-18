package com.mds.crypto.v2.enumeration;

import lombok.Getter;

/**
 * Enumeration representing the different modes of operation for a client. Each mode specifies
 * whether the client is active or not.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Getter
public enum ClientMode {

  /**
   * Mode where the scheduler communicates with the backend. The client is not active in this mode.
   */
  SCHEDULER_TO_BACK(null),

  /**
   * Mode where the backend communicates with another backend. The client is active in this mode.
   */
  BACK_TO_BACK(Boolean.TRUE),

  /**
   * Mode where the frontend communicates with the backend. The client is not active in this mode.
   */
  FRONT_TO_BACK(Boolean.FALSE);

  /**
   * Constructor for the enumeration.
   *
   * @param client A Boolean indicating whether the client is active.
   */
  ClientMode(Boolean client) {
    this.client = client;
  }

  /** Indicates whether the client is active. */
  private final Boolean client;
}
