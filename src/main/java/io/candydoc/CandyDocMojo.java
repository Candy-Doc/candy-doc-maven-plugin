package io.candydoc;

import io.candydoc.domain.Domain;
import io.candydoc.domain.GenerateDocumentation;
import io.candydoc.domain.SaveDocumentationAdapterFactory;
import io.candydoc.domain.SaveDocumentationPort;
import io.candydoc.infra.SaveDocumentationAdapterFactoryImpl;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Mojo(name = "candy-doc", defaultPhase = LifecyclePhase.COMPILE)
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
        SaveDocumentationPort saveDocumentationPort = adapterFactory.getAdapter(outputFormat, outputDirectory);
        Domain domain = new Domain(saveDocumentationPort);
        domain.generateDocumentation(GenerateDocumentation.builder().packagesToScan(packagesToScan).build());
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