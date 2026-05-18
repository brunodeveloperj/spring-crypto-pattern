package com.mds.crypto.v1.client.config;

import com.mds.crypto.v1.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class responsible for creating a RestTemplate bean with custom SSL settings.
 *
 * <p>This class uses the `DLBConfig` to retrieve certificate information and configures an
 * `HttpClient` with SSL/TLS support for secure communication.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DLBRestTemplateConfig {

  private final DLBConfig dlbConfig;

  /**
   * Creates a RestTemplate bean configured with custom SSL settings.
   *
   * <p>This method sets up a RestTemplate instance with an HttpClient that uses a custom
   * SSLContext. The SSLContext is configured with a certificate and a trust strategy that accepts all certificates.
   *
   * @return a RestTemplate instance with custom SSL configuration.
   * @throws IOException if an error occurs while reading the certificate file.
   */
  @Bean
  public RestTemplate restTemplateSecurity() throws IOException {
    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

    try {
      if (dlbConfig.hasValidCertificate()) {
        String certificatePassword = FileUtils.extractValue(dlbConfig.getPathCertificatePassword());
        File certificate = FileUtils.getFile(dlbConfig.getPathCertificate());

        try (InputStream in = Files.newInputStream(Objects.requireNonNull(certificate).toPath())) {
          SSLContext sslContext = getSSLContext(in, certificatePassword.trim(), acceptingTrustStrategy);
          requestFactory.setHttpClient(getHTTPClient(sslContext));
        }
      } else {
        log.warn("[DLBRestTemplateConfig] - No valid certificate found. Using default SSLContext.");
        SSLContext sslContext = getSSLContext(null, null, acceptingTrustStrategy);
        requestFactory.setHttpClient(getHTTPClient(sslContext));
      }
    } catch (Exception e) {
      log.error("[DLBRestTemplateConfig] - Error configuring SSLContext. Using default settings.", e);
    }

    return new RestTemplate(requestFactory);
  }

  /**
   * Configures and returns an SSLContext with custom key material and trust strategy.
   *
   * <p>This method creates an SSLContext using the provided key material and a trust strategy that
   * determines how certificates are validated. It is used to enable secure communication with custom SSL/TLS settings.
   *
   * @param fileInputStream        the input stream of the certificate file.
   * @param certificatePassword    the password for the certificate file.
   * @param acceptingTrustStrategy the trust strategy to use for certificate validation.
   * @return an SSLContext instance configured with the provided key material and trust strategy.
   * @throws NoSuchAlgorithmException if the specified SSL algorithm is not available.
   */
  private SSLContext getSSLContext(InputStream fileInputStream, String certificatePassword, TrustStrategy acceptingTrustStrategy) throws NoSuchAlgorithmException {
    try {
      SSLContextBuilder builder = SSLContextBuilder.create().loadTrustMaterial(null, acceptingTrustStrategy);
      if (fileInputStream != null && certificatePassword != null && !certificatePassword.trim().isEmpty()) {
        builder.loadKeyMaterial(loadKeyStore(fileInputStream, certificatePassword.trim()), certificatePassword.trim().toCharArray());
      }
      return builder.build();
    } catch (Exception e) {
      log.error("[DLBRestTemplateConfig] - Failed to configure SSL Context. Falling back to default context.", e);
      return SSLContext.getInstance("TLSv1.2");
    }
  }

  /**
   * Configures and returns an HttpClient with custom SSL settings.
   *
   * <p>This method creates an HttpClient instance using a custom SSLContext and connection
   * configurations. It sets up socket and connection timeouts, as well as a connection pool with specific policies.
   *
   * @param sslContext the SSLContext to be used for secure communication.
   * @return an HttpClient instance configured with the provided SSLContext and connection settings.
   */
  private HttpClient getHTTPClient(SSLContext sslContext) {
    try {
      var socketConfig = SocketConfig.custom()
                                     .setSoTimeout(Timeout.ofMinutes(1))
                                     .build();

      var connectionConfig = ConnectionConfig.custom()
                                             .setSocketTimeout(Timeout.ofMinutes(1))
                                             .setConnectTimeout(Timeout.ofMinutes(1))
                                             .setTimeToLive(TimeValue.ofMinutes(10))
                                             .build();

      var sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                                                              .setSslContext(sslContext)
                                                              .build();

      var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                                                                       .setSSLSocketFactory(sslSocketFactory)
                                                                       .setDefaultSocketConfig(socketConfig)
                                                                       .setDefaultConnectionConfig(connectionConfig)
                                                                       .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                                                                       .setConnPoolPolicy(PoolReusePolicy.LIFO)
                                                                       .build();

      return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    } catch (Exception e) {
      log.warn("[DLBRestTemplateConfig] - Filed to configure SSL Context for HttpClient. Falling back to default client. ", e);
      return HttpClients.custom().build();
    }
  }

  /**
   * Loads a KeyStore from the provided input stream using the specified password.
   *
   * <p>This method initializes a KeyStore instance of type "JKS" and loads it with the key material
   * from the given input stream. If an error occurs during the loading process, it logs the error and returns the KeyStore instance in its current state.
   *
   * @param in                  the input stream containing the key store data.
   * @param certificatePassword the password to unlock the key store.
   * @return a KeyStore instance loaded with the provided key material.
   * @throws KeyStoreException if the KeyStore instance cannot be created.
   */
  private KeyStore loadKeyStore(InputStream in, String certificatePassword) throws KeyStoreException {
    KeyStore keyStore = KeyStore.getInstance("JKS");
    try {
      keyStore.load(in, certificatePassword.trim().toCharArray());
    } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
      log.error("[DLBRestTemplateConfig] - Error loading key store.", e);
    }
    return keyStore;
  }
}
