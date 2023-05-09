package io.candydoc.plugin.save_ubiquitous_language.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Manifest {
  public static final Path DESTINATION_FOLDER = Path.of("target/candy-doc");
  private final List<Path> jsonPaths;
  private final ObjectMapper serializer;

  public Manifest(ObjectMapper serializer) {
    jsonPaths = new ArrayList<>();
    this.serializer = serializer;
  }

  public void addFile(Path resolve) {
    jsonPaths.add(resolve);
  }

  public void save(String extension) throws IOException {
    Files.createDirectories(DESTINATION_FOLDER);
    serializer.writeValue(DESTINATION_FOLDER.resolve("MANIFEST." + extension).toFile(), jsonPaths);
  }
}
