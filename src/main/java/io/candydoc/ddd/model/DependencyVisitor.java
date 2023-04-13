package io.candydoc.ddd.model;

import io.candydoc.ddd.strategic_pattern.BoundedContext;
import io.candydoc.ddd.strategic_pattern.SharedKernel;
import io.candydoc.ddd.tactical_pattern.*;
import io.candydoc.plugin.model.DependencyDto;
import java.util.List;

public interface DependencyVisitor {
  List<DependencyDto> visit(BoundedContext boundedContext);

  List<DependencyDto> visit(SharedKernel sharedKernel);

  List<DependencyDto> visit(Aggregate aggregate);

  List<DependencyDto> visit(DomainEvent domainEvent);

  List<DependencyDto> visit(DomainCommand domainCommand);

  List<DependencyDto> visit(Entity entity);

  List<DependencyDto> visit(ValueObject valueObject);
}
