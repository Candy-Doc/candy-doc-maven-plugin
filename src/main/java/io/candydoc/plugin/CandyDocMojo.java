package io.candydoc.plugin;

import io.candydoc.ddd.extract_ddd_concepts.*;
import io.candydoc.plugin.save_documentation.SaveDocumentationAdapterFactory;
import io.candydoc.plugin.save_documentation.SaveDocumentationAdapterFactoryImpl;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(
    name = "candy-doc",
    defaultPhase = LifecyclePhase.PROCESS_SOURCES,
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
    SaveDocumentationAdapterFactory adapterFactory = new SaveDocumentationAdapterFactoryImpl();
    SaveDocumentationPort saveDocumentationPort =
        adapterFactory.getAdapter(outputFormat, outputDirectory);
    DDDConceptFinder DDDConceptFinder = new ReflectionsConceptFinder();
    DDDConceptsExtractionService dddConceptsExtractionService =
        new DDDConceptsExtractionService(DDDConceptFinder);

    ExtractDDDConceptsUseCase extractDDDConceptsUseCase =
        new ExtractDDDConceptsUseCase(dddConceptsExtractionService, saveDocumentationPort);

    extractDDDConceptsUseCase.execute(
        ExtractDDDConcepts.builder().packagesToScan(packagesToScan).build());
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
}
