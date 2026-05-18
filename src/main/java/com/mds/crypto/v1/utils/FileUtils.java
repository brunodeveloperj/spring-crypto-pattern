package com.mds.crypto.v1.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * File-system utility helpers for the crypto layer.
 *
 * <p>Provides two operations used primarily by
 * {@link com.mds.crypto.v1.client.config.DLBRestTemplateConfig} and
 * {@link com.mds.crypto.v1.client.config.DLBConfig}:
 * <ul>
 *   <li>{@link #getFile(String)} — resolves a path to a {@link java.io.File}
 *       if it exists and is not a directory.</li>
 *   <li>{@link #extractValue(String)} — reads the full content of a file
 *       as a {@link String}, or falls back to returning the path itself
 *       when it is not a valid file (treating it as a plain-text value).</li>
 * </ul>
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

  public static File getFile(String path) {
    try {
      Path filePath = Path.of(path);
      if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
        log.info("[FileUtils] - File exists and is not a directory: {}", path);
        return filePath.toFile();
      }
    } catch (Exception e) {
      log.error("[FileUtils] - Error retrieving file: {}", e.getMessage());
    }
    log.warn("[FileUtils] - Path is not a file when retrieving: {}", path);
    return null;
  }

  public static String extractValue(String path) {
    try {
      Path filePath = Path.of(path);
      if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
        log.info("[FileUtils] - Reading value from file: {}", path);
        return Files.readString(filePath);
      }
    } catch (Exception e) {
      log.error("[FileUtils] - Error reading file value: {}", e.getMessage());
    }
    log.warn("[FileUtils] - Path is not a file when reading: {}", path);
    return path;
  }
}
