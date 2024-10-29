package org.shadow.infrastructure.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistoricalDataLoaderTest {

  private HistoricalDataLoader dataLoader;

  @BeforeEach
  void setUp() {
    dataLoader = new HistoricalDataLoader();
  }

  @Test
  void testLoadValidFile() throws IOException {
    var jsonContent =
        """
        [
            {
                "timestamp": 1633036800,
                "open": "43000.00",
                "high": "43500.00",
                "low": "42500.00",
                "close": "43200.00",
                "volume": "1200.5"
            },
            {
                "timestamp": 1633040400,
                "open": "43200.00",
                "high": "43800.00",
                "low": "43000.00",
                "close": "43700.00",
                "volume": "1300.7"
            }
        ]
        """;

    var tempFile = Files.createTempFile("testData", ".json");
    Files.writeString(tempFile, jsonContent);

    try {
      var historicalDataBars = dataLoader.load(tempFile.toAbsolutePath().toString());

      assertNotNull(historicalDataBars);
      assertEquals(2, historicalDataBars.size());

      var first = historicalDataBars.getFirst();
      assertEquals(1633036800L, first.timestamp());
      assertEquals("43000.00", first.open());
      assertEquals("43500.00", first.high());
      assertEquals("42500.00", first.low());
      assertEquals("43200.00", first.close());
      assertEquals("1200.5", first.volume());

      var second = historicalDataBars.get(1);
      assertEquals(1633040400L, second.timestamp());
      assertEquals("43200.00", second.open());
      assertEquals("43800.00", second.high());
      assertEquals("43000.00", second.low());
      assertEquals("43700.00", second.close());
      assertEquals("1300.7", second.volume());

    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  void testLoadFileNotFound() {
    var nonExistentFile = "non-existent-file.json";

    var exception =
        assertThrows(
            IOException.class,
            () -> {
              dataLoader.load(nonExistentFile);
            });

    assertTrue(exception.getMessage().contains("File not found"));
  }

  @Test
  void testLoadInvalidJson() throws IOException {
    var invalidJsonContent = "This is not valid JSON content";

    var tempFile = Files.createTempFile("invalidTestData", ".json");
    Files.writeString(tempFile, invalidJsonContent);

    try {
      var exception =
          assertThrows(
              IOException.class,
              () -> {
                dataLoader.load(tempFile.toAbsolutePath().toString());
              });

      assertTrue(exception.getMessage().contains("Error reading or parsing the file"));
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }
}
