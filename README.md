# spring-crypto-pattern

Biblioteca Spring Boot reutilizável que centraliza operações criptográficas baseadas no serviço DLB (Data Leakage Barrier), incluindo key agreement ECC, criptografia/descriptografia de dados, decodificação de JWT e tratamento estruturado de exceções.

## Dependência

```xml
<dependency>
  <groupId>com.mds</groupId>
  <artifactId>spring-crypto-pattern</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

## Funcionalidades

### v1 — DLB Crypto Client

- **Key Agreement ECC** — handshake de duas fases com o serviço DLB para troca segura de chaves
- **Encrypt / Decrypt** — criptografia e descriptografia de strings e arrays via `DLCrypto`
- **ECC Translate** — tradução de dados cifrados através do endpoint DLB translate
- **RestTemplate com SSL customizado** — comunicação segura com certificado mútuo (mTLS)
- **Sessão criptográfica** — `DLCryptoSession` com escopo por requisição HTTP
- **Serialização de objetos criptográficos** — `DLBCryptoLoader` para serializar/deserializar `DLCrypto`
- **Modos de operação** — `ClientMode` (FRONT_TO_BACK, BACK_TO_BACK, SCHEDULER_TO_BACK)
- **File Utils** — leitura de secrets de arquivo ou texto plano

### v2 — JWT & Exception Handling

- **JWT Decoder** — extração tipada de claims com/sem validação de token
- **Exceções estruturadas** — hierarquia completa: `CryptoException`, `DecryptionException`, `EncryptionException`, `IllegalJwtException`, `JsonParseException`, `InaccessibleObjectException`, `NoEncryptedObjectDefException`
- **Exception Resolvers** — resolvers automáticos com mapeamento para `ErrorResponse` (HTTP status + action + type)
- **Catálogo de erros** — `DLBExceptionKeys` com códigos `DLB_0013` a `DLB_0024`

---

## Configuração

```yaml
auth:
  dlb-manager:
    initializeURL: https://dlb-service/api/v1/initialize
    translateURL: https://dlb-service/api/v1/translate
    system: MY_SYSTEM
    pathCertificate: /run/secrets/dlb-cert.p12
    pathCertificatePassword: /run/secrets/dlb-cert-pass
```

---

## Exemplo de uso

### Criptografar / Descriptografar (v1)

```java
@Autowired
private CryptoHandler cryptoHandler;

@Autowired
private EncryptedObjectHandler encryptedObjectHandler;

// Inicializar objeto criptográfico
String encryptedObject = encryptedObjectHandler.createEncryptedObject(clientMode);

// Criptografar
String encrypted = cryptoHandler.encrypt(value, encryptedObject, isClient);

// Descriptografar
String decrypted = cryptoHandler.decrypt(encrypted, encryptedObject, isClient);
```

### Decodificar JWT (v2)

```java
// Extrair claim como String
String userId = JwtEncryptedObjectDecoder.decodeJwtClaim(jwtToken, "userId");

// Extrair claim tipado
MyDTO dto = JwtEncryptedObjectDecoder.decodeJwtClaim(jwtToken, "payload", MyDTO.class);

// Sem validação
MyDTO dto = JwtEncryptedObjectDecoder.decodeJwtClaimWithoutValidation(jwtToken, "payload", MyDTO.class);
```

---

## Arquitetura

```
Application
    ↓
CryptoHandler / EncryptedObjectHandler (v1)
    ↓
DlbCryptoClient (REST + SSL)
    ↓
DLB Service (Key Agreement + Translate)

Application
    ↓
JwtEncryptedObjectDecoder (v2)
    ↓
auth0/jwt-decode
```

---

## Estrutura do projeto

```
src/main/java/com/mds/crypto/
├── CryptoAutoConfiguration.java         # Auto-config + component scan
├── v1/
│   ├── client/
│   │   ├── DlbCryptoClient.java         # REST client para DLB service
│   │   └── config/
│   │       ├── DLBConfig.java           # Properties (auth.dlb-manager.*)
│   │       └── DLBRestTemplateConfig.java # RestTemplate com SSL/mTLS
│   ├── dto/
│   │   ├── CipherStringDataDTO.java     # Dados cifrados (content + id)
│   │   ├── DLBPropertiesDTO.java        # Propriedades DLB
│   │   ├── EccTranslateStringRequestDTO.java
│   │   ├── EccTranslateStringResponseDTO.java
│   │   ├── KeyAgreementRequestDTO.java
│   │   ├── KeyAgreementResponseDTO.java
│   │   └── TranslateRequestDTO.java
│   ├── handler/
│   │   ├── CryptoHandler.java           # Encrypt/Decrypt de strings e arrays
│   │   └── EncryptedObjectHandler.java  # Criação de objetos criptográficos
│   ├── keys/
│   │   └── MessagesKeys.java           # Códigos DLB_0001 a DLB_0012
│   ├── session/
│   │   └── DLCryptoSession.java        # Sessão crypto por request
│   ├── stub/
│   │   ├── DLBCryptoLoader.java        # Serialização (stub)
│   │   └── DLCrypto.java              # Core crypto (stub)
│   └── utils/
│       └── FileUtils.java             # Leitura de secrets de arquivo
├── v2/
│   ├── enumeration/
│   │   └── ClientMode.java            # FRONT_TO_BACK, BACK_TO_BACK, SCHEDULER_TO_BACK
│   ├── exception/
│   │   ├── CryptoException.java
│   │   ├── DecryptionException.java
│   │   ├── EncryptionException.java
│   │   ├── IllegalJwtException.java
│   │   ├── InaccessibleObjectException.java
│   │   ├── JsonParseException.java
│   │   ├── NoEncryptedObjectDefException.java
│   │   ├── keys/
│   │   │   └── DLBExceptionKeys.java  # Códigos DLB_0013 a DLB_0024
│   │   └── resolver/
│   │       ├── CryptoExceptionResolver.java          # 500
│   │       ├── DecryptionExceptionResolver.java      # 422
│   │       ├── EncryptionExceptionResolver.java      # 500
│   │       ├── IllegalJwtExceptionResolver.java      # 403
│   │       ├── JsonParseExceptionResolver.java       # 400
│   │       └── NoEncryptedObjectDefExceptionResolver.java # 422
│   └── security/
│       └── JwtEncryptedObjectDecoder.java  # Decodificação de JWT
```

---

## Requisitos

- **Java** 21+
- **Spring Boot** 4.0.6+
- **spring-error-pattern** (dependência para BaseException e resolvers)
- **com.auth0:java-jwt** (decodificação de JWT no v2)

---

## Melhorias futuras

- Substituir stubs DLCrypto/DLBCryptoLoader por implementação open-source
- Cache de encrypted objects para reutilização entre requests
- Suporte a rotação automática de chaves
- Métricas Micrometer para latência de key agreement
