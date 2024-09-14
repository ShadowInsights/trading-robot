package org.shadow.infrastructure.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.shadow.infrastructure.file.model.Candlestick;

public class HistoricalDataLoader {

  private static final String DATA_DIR = "historical-data";
  private final ObjectMapper objectMapper;

  public HistoricalDataLoader() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Loads historical candlestick data from a JSON file located in the historical-data folder at the
   * root of the project. If a full data path is provided, it loads from that path.
   *
   * @param fileName the name of the JSON file to load or the full path to the file
   * @return a list of candlestick records
   * @throws IOException if there is an error loading or parsing the file
   */
  public List<Candlestick> load(String fileName) throws IOException {
    Path filePath = Path.of(fileName);
    if (!filePath.isAbsolute()) {
      filePath = Path.of(DATA_DIR, fileName);
    }
    if (!Files.exists(filePath)) {
      throw new IOException("File not found: " + filePath);
    }
    try (InputStream inputStream = Files.newInputStream(filePath)) {
      return objectMapper.readValue(inputStream, new TypeReference<>() {});
    } catch (IOException e) {
      throw new IOException("Error reading or parsing the file: " + filePath, e);
    }
  }
}
