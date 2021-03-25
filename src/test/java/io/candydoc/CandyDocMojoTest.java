package io.candydoc;

import io.candydoc.CandyDocMojo;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CandyDocMojoTest {

    private CandyDocMojo candyDocMojo;

    @BeforeEach
    public void setUp() {
        candyDocMojo = new CandyDocMojo();
        candyDocMojo.project = new MavenProject();
    }

    @Test
    public void path_is_required() {
        candyDocMojo.packageToScan = "";
        Assertions.assertThatThrownBy(() -> candyDocMojo.execute())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
