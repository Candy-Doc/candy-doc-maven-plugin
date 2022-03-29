package io.candydoc.domain.processors;

import com.google.auto.service.AutoService;
import io.candydoc.domain.GenerateDocumentationUseCase;
import io.candydoc.domain.SaveDocumentationAdapterFactory;
import io.candydoc.domain.SaveDocumentationPort;
import io.candydoc.domain.annotations.*;
import io.candydoc.domain.command.ExtractDDDConcepts;
import io.candydoc.domain.repository.ClassesFinder;
import io.candydoc.infra.SaveDocumentationAdapterFactoryImpl;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class MainProcessor extends AbstractProcessor {
    private Messager messager;
    private Elements elementsUtils;
    private Filer filer;

    private List<String> packagesToScan;
    private String outputFormat;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementsUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Aggregate.class.getCanonicalName());
        annotations.add(BoundedContext.class.getCanonicalName());
        annotations.add(CoreConcept.class.getCanonicalName());
        annotations.add(DomainCommand.class.getCanonicalName());
        annotations.add(DomainEvent.class.getCanonicalName());
        annotations.add(ValueObject.class.getCanonicalName());
        return annotations;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> options = new LinkedHashSet<>();
        options.add("packagesToScan");
        options.add("outputFormat");
        return options;
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
        try {
            if (processingEnv.getOptions().get("packagesToScan") != null && processingEnv.getOptions().get("packagesToScan") instanceof String) {
                packagesToScan = List.of(processingEnv.getOptions().get("packagesToScan").split(","));
                messager.printMessage(Diagnostic.Kind.NOTE, "packagesToScans : " + packagesToScan);
            } else {
                throw new ProcessingException(null, "You should specify the packages to scan with the option 'packagesToScan' (in pom.xml in compiler args like this: <arg>-ApackagesToScan='packageName'</arg>)");
            }
            if ((outputFormat = processingEnv.getOptions().get("outputFormat")) != null && outputFormat instanceof String) {
                messager.printMessage(Diagnostic.Kind.NOTE, "outputFormat : " + outputFormat);
            } else {
                outputFormat = "json";
                messager.printMessage(Diagnostic.Kind.WARNING, "You didn't specify the output format. The default format is 'json'");
            }

            ClassesFinder.getInstance().addElements((Set<TypeElement>) typeElements);

            messager.printMessage(Diagnostic.Kind.NOTE, "CandyDoc is processing...");

            FileObject output = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "candydoc/boundedContexts.json");
            File file = new File(output.toUri());
            String outputDirectory = file.getAbsolutePath();
            SaveDocumentationAdapterFactory adapterFactory = new SaveDocumentationAdapterFactoryImpl();
            SaveDocumentationPort saveDocumentationPort =
                    adapterFactory.getAdapter(outputFormat, outputDirectory);
            GenerateDocumentationUseCase generateDocumentationUseCase =
                    new GenerateDocumentationUseCase(saveDocumentationPort);
            generateDocumentationUseCase.execute(
                    ExtractDDDConcepts.builder().packagesToScan(packagesToScan).build());

            return true;
        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
