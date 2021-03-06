package edu.kit.joana.api.sdg.opt;

import edu.kit.joana.api.IFCAnalysis;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Collects information over all files first and then processes each file and stores the modified version.
 *
 * The implicit constraint is that the methods are called in the following order:
 * setup → collect* → store* → teardown (the order in which the process method calls them)
 */
public interface FilePass extends Pass {

  @Override
  default void process(String libClassPath, Path sourceFolder, Path targetFolder) throws IOException {
    setup(libClassPath);
    walk(sourceFolder, targetFolder, (s, t) -> {
      s.toFile().getParentFile().mkdirs();
      collect(s);
    });
    walk(sourceFolder, targetFolder, this::store);
    teardown();
  }

  default void walk(Path sourceFolder, Path targetFolder, ThrowingBiConsumer<Path, Path> func) throws IOException {
    Pass.walk(sourceFolder, f -> {
      func.accept(f, targetFolder.resolve(sourceFolder.relativize(f)));
    });
  }

  void setup(String libClassPath);

  void collect(Path file) throws IOException;

  void store(Path source, Path target) throws IOException;

  void teardown();
}
