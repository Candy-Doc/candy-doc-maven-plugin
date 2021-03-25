package io.candydoc;

import io.candydoc.model.BoundedContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Mojo(name = "candy-doc", defaultPhase = LifecyclePhase.COMPILE)
public class CandyDocMojo extends AbstractMojo {


    @Parameter(property = "packageToScan")
    String packageToScan;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;


    private ClassLoader getClassLoader() throws MojoExecutionException {
        try {
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    @Override
    public void execute() throws MojoExecutionException {

        getLog().info("The Candy-Doc plugin is working");


        if (packageToScan != null && packageToScan != "") {
            getLog().info("The packageToScan parameter is : " + packageToScan);
            Thread.currentThread().setContextClassLoader(getClassLoader());

            generateDocumentation();

        } else {
            getLog().warn("Missing parameter for 'packageToScan'");
            throw new IllegalArgumentException("Wrong parameters for 'packageToScan'. Check your pom configuration.");
        }
    }

    private void generateDocumentation() {
        Domain domain = new Domain();
        getLog().info(" >>>>>> Starting retrieving of BoundedContext annotations");
        List<BoundedContext> boundedContexts = domain.getBoundedContexts(packageToScan);

        if (boundedContexts.isEmpty()) {
            getLog().warn("!!! The list of BoundedContext is empty !!!");
        } else {
            System.out.println("List of the Bounded Context(s) : ");
            for (BoundedContext bc : boundedContexts) {
                System.out.println(bc.getName() + " | " + bc.getDescription());
            }
        }
        getLog().info("<<<<<< end of the retrieving");
    }
}