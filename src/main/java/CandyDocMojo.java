import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "candy-doc", defaultPhase = LifecyclePhase.COMPILE)
public class CandyDocMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("The Candy-Doc plugin is working");

    }
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;
}