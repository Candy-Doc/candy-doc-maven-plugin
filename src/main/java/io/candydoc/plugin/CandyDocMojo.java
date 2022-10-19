package io.candydoc.plugin;

import io.candydoc.ddd.extract_domain_models.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.candydoc.plugin.save_documentation.file.json.UbiquitousLanguageAsJson;
import io.candydoc.plugin.save_documentation.file.yml.UbiquitousLanguageAsYml;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

@Mojo(
    name = "candy-doc",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM,
    requiresDependencyCollection = ResolutionScope.RUNTIME_PLUS_SYSTEM,
    instantiationStrategy = InstantiationStrategy.SINGLETON,
    threadSafe = true)
public class CandyDocMojo extends AbstractMojo {

  @Parameter(property = "packagesToScan")
  List<String> packagesToScan;

  @Parameter(property = "outputFormat")
  String outputFormat;

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @SneakyThrows
  @Override
  public void execute() {
    String outputDirectory = project.getBuild().getDirectory();
    Thread.currentThread().setContextClassLoader(getProjectClassLoader());
    UbiquitousLanguageRepository ubiquitousLanguageRepository = getRepository(outputFormat, outputDirectory);
    DomainModelFinder domainModelFinder = new ReflectionsDomainModelFinder(packagesToScan);

    ExtractDomainModelsUseCase extractDomainModelsUseCase =
        new ExtractDomainModelsUseCase(domainModelFinder, ubiquitousLanguageRepository);

    extractDomainModelsUseCase.execute(
        ExtractDomainModels.builder()
            .packagesToScan(packagesToScan)
            .build());
  }

  private ClassLoader getProjectClassLoader() throws MojoExecutionException {
    try {
      List<String> classpathElements = project.getCompileClasspathElements();
      URL[] urls = new URL[classpathElements.size()];
      for (int i = 0; i < classpathElements.size(); ++i) {
        urls[i] = new File(classpathElements.get(i)).toURI().toURL();
      }
      return new URLClassLoader(urls, getClass().getClassLoader());
    } catch (Exception e) {
      throw new MojoExecutionException("Classloader could not be created.", e);
    }
  }

  public UbiquitousLanguageRepository getRepository(String outputFormat, String outputDirectory) {
    if (Files.notExists((Path.of(outputDirectory)))) {
      throw new IllegalArgumentException("Output directory does not exist.");
    }
    Path outputFile = Paths.get(outputDirectory, "candy-doc", "ubiquitous_language", "result." + outputFormat);
    switch (outputFormat) {
      case "json":
        return new UbiquitousLanguageAsJson(outputFile);
      case "yml":
        return new UbiquitousLanguageAsYml(outputFile);
    }
    throw new IllegalArgumentException("File format must be 'json', 'yml'.");
  }

}
