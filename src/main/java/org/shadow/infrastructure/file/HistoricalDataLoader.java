package org.shadow.infrastructure.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.shadow.infrastructure.file.model.HistoricalDataBar;

/** Loads historical bars data from a JSON file. */
public class HistoricalDataLoader {

  private static final String DATA_DIR = "historical-data";
  private final ObjectMapper objectMapper;

  public HistoricalDataLoader() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Loads historical bars data from a JSON file.
   *
   * <p>If an absolute file path is provided, it loads the file directly from the filesystem.
   * Otherwise, it attempts to load the file from the classpath's 'historical-data' directory.
   *
   * @param fileName the name of the JSON file to load or the full path to the file
   * @return a list of historical bars loaded from the file
   * @throws IOException if there is an error loading or parsing the file
   */
  public List<HistoricalDataBar> load(String fileName) throws IOException {
    var filePath = Path.of(fileName);
    var path = Path.of(DATA_DIR, fileName);

    InputStream inputStream;

    if (filePath.isAbsolute()) {
      if (!Files.exists(filePath)) {
        throw new IOException("File not found: " + filePath);
      }
      try {
        inputStream = Files.newInputStream(filePath);
      } catch (IOException e) {
        throw new IOException("Error opening file: " + filePath, e);
      }
    } else {
      var resourcePath = path.toString().replace("\\", "/");
      inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

      if (inputStream == null) {
        throw new IOException("Resource not found in classpath: " + resourcePath);
      }
    }

    try (var is = inputStream) {
      return objectMapper.readValue(is, new TypeReference<>() {});
    } catch (IOException e) {
      var source = filePath.isAbsolute() ? filePath.toString() : "resource: " + path;
      throw new IOException("Error reading or parsing the file: " + source, e);
    }
  }
}
