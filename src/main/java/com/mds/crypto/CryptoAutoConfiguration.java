package com.mds.crypto;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring auto-configuration entry point for the MDS Crypto Pattern library.
 *
 * <p>Enables component scanning across the {@code com.mds.crypto} package
 * so that all cryptographic handlers, clients, sessions, exception resolvers,
 * and configuration beans from both v1 (DLB-based) and v2 (JWT / generic)
 * modules are automatically registered.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@ComponentScan
public class CryptoAutoConfiguration {}
