package io.candydoc.plugin;

import com.google.auto.service.AutoService;
import io.candydoc.ddd.extract_ddd_concepts.*;
import io.candydoc.ddd.repository.ClassesFinder;
import io.candydoc.ddd.repository.ProcessorUtils;
import io.candydoc.plugin.save_documentation.SaveDocumentationAdapterFactory;
import io.candydoc.plugin.save_documentation.SaveDocumentationAdapterFactoryImpl;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class MainProcessor extends AbstractProcessor {
  private Messager messager;
  private Elements elementsUtils;
  private Types typesUtils;
  private Filer filer;

  private List<String> packagesToScan;
  private String outputFormat;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    messager = processingEnv.getMessager();
    elementsUtils = processingEnv.getElementUtils();
    typesUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();

    ProcessorUtils.getInstance().setMessager(messager);
    ProcessorUtils.getInstance().setElementsUtils(elementsUtils);
    ProcessorUtils.getInstance().setTypesUtils(typesUtils);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return AnnotationProcessorConceptFinder.ANNOTATION_PROCESSORS.keySet().stream()
        .map(Class::getCanonicalName)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<String> getSupportedOptions() {
    return Set.of("packagesToScan", "outputFormat");
  }

  @Override
  public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
    try {
      if (processingEnv.getOptions().get("packagesToScan") != null
          && processingEnv.getOptions().get("packagesToScan") instanceof String) {
        packagesToScan = List.of(processingEnv.getOptions().get("packagesToScan").split(","));
        messager.printMessage(Diagnostic.Kind.NOTE, "packagesToScan: " + packagesToScan);
      } else {
        throw new ProcessingException(
            null,
            "You should specify the packages to scan with the option 'packagesToScan' (in pom.xml"
                + " in compiler args like this: <arg>-ApackagesToScan='packageName'</arg>)");
      }
      if ((outputFormat = processingEnv.getOptions().get("outputFormat")) != null
          && outputFormat instanceof String) {
        messager.printMessage(Diagnostic.Kind.NOTE, "outputFormat: " + outputFormat);
      } else {
        outputFormat = "json";
        messager.printMessage(
            Diagnostic.Kind.WARNING,
            "You didn't specify the output format. The default format is 'json'");
      }

      messager.printMessage(Diagnostic.Kind.NOTE, "CandyDoc is processing...");

      ClassesFinder.getInstance()
          .addElements(
              (Set<Element>)
                  roundEnv.getElementsAnnotatedWithAny(
                      AnnotationProcessorConceptFinder.ANNOTATION_PROCESSORS.keySet()));

      messager.printMessage(
          Diagnostic.Kind.NOTE,
          "Classes found : " + ClassesFinder.getInstance().getElements().toString());

      FileObject output =
          filer.createResource(
              StandardLocation.SOURCE_OUTPUT, "", "candydoc/boundedContexts." + outputFormat);
      File file = new File(output.toUri());
      String outputDirectory = file.getParentFile().getAbsolutePath();
      messager.printMessage(
          Diagnostic.Kind.NOTE, "CandyDoc output directory is: " + outputDirectory);
      SaveDocumentationAdapterFactory adapterFactory = new SaveDocumentationAdapterFactoryImpl();
      SaveDocumentationPort saveDocumentationPort =
          adapterFactory.getAdapter(outputFormat, outputDirectory);
      DDDConceptFinder DDDConceptFinder = new AnnotationProcessorConceptFinder();
      DDDConceptsExtractionService dddConceptsExtractionService =
          new DDDConceptsExtractionService(DDDConceptFinder);

      ExtractDDDConceptsUseCase extractDDDConceptsUseCase =
          new ExtractDDDConceptsUseCase(dddConceptsExtractionService, saveDocumentationPort);

      extractDDDConceptsUseCase.execute(
          ExtractDDDConcepts.builder().packagesToScan(packagesToScan).build());
      messager.printMessage(Diagnostic.Kind.NOTE, "CandyDoc documentation done.");

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
