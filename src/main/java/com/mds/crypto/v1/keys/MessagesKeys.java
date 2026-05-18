package com.mds.crypto.v1.keys;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class that contains constant keys used for messages in the application. This class is not
 * meant to be instantiated.
 *
 * <p>Error codes {@code DLB_0001} through {@code DLB_0012} are used by
 * {@link com.mds.crypto.v1.client.DlbCryptoClient} and
 * {@link com.mds.crypto.v1.handler.CryptoHandler} to identify specific
 * failure scenarios during key agreement, translation, encryption and
 * decryption operations.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessagesKeys {

  /** Key for message DLB_0001. */
  public static final String DLB_0001 = "DLB_0001";

  /** Key for message DLB_0002. */
  public static final String DLB_0002 = "DLB_0002";

  /** Key for message DLB_0003. */
  public static final String DLB_0003 = "DLB_0003";

  /** Key for message DLB_0004. */
  public static final String DLB_0004 = "DLB_0004";

  /** Key for message DLB_0005. */
  public static final String DLB_0005 = "DLB_0005";

  /** Key for message DLB_0006. */
  public static final String DLB_0006 = "DLB_0006";

  /** Key for message DLB_0007. */
  public static final String DLB_0007 = "DLB_0007";

  /** Key for message DLB_0008. */
  public static final String DLB_0008 = "DLB_0008";

  /** Key for message DLB_0009. */
  public static final String DLB_0009 = "DLB_0009";

  /** Key for message DLB_0010. */
  public static final String DLB_0010 = "DLB_0010";

  /** Key for message DLB_0011. */
  public static final String DLB_0011 = "DLB_0011";

  /** Key for message DLB_0012. */
  public static final String DLB_0012 = "DLB_0012";

  /** Default error message displayed when communication fails. */
  public static final String DLB_MESSAGE = "Não foi possível completar a comunicação. Tente novamente mais tarde.";
}
