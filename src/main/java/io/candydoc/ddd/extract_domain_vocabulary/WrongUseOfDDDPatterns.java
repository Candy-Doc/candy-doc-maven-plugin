package io.candydoc.ddd.extract_domain_vocabulary;

import io.candydoc.ddd.model.FoundDDDPattern;
import io.candydoc.ddd.model.FoundDomainObject;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class WrongUseOfDDDPatterns extends ExtractionException {

    public WrongUseOfDDDPatterns(Collection<FoundDomainObject> domainObjects) {
        super(toMessage(domainObjects));
    }

    private static String toMessage(Collection<FoundDomainObject> domainObjects) {
        return domainObjects.stream()
                .sorted(Comparator.comparing(FoundDomainObject::canonicalName))
                .map(domainObject -> String.format("Domain object '%s' matches more than one DDD pattern: %s",
                        domainObject.canonicalName().value(),
                        domainObject.matchingPatterns().stream()
                                .map(FoundDDDPattern::dddPattern)
                                .sorted()
                                .collect(Collectors.toUnmodifiableList())))
                .collect(Collectors.joining(",\n"));
    }

}
