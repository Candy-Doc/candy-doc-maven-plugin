package io.candydoc;

import io.candydoc.model.BoundedContext;
import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Domain {

    public List<BoundedContext> getBoundedContexts(String packageToScan) {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageToScan))
                .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner()));
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(io.candydoc.annotations.BoundedContext.class);

        return set.stream().map(bc -> BoundedContext.builder()
                .name(bc.getPackageName())
                .description(bc.getAnnotation(io.candydoc.annotations.BoundedContext.class).desc())
                .build())
                .collect(Collectors.toList());
    }

}
