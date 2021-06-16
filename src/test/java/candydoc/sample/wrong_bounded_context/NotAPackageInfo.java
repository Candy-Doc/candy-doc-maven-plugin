package candydoc.sample.wrong_bounded_context;

import io.candydoc.domain.annotations.BoundedContext;

@BoundedContext(name = "not a bounded context", description = "not supposed to be extracted")
public class NotAPackageInfo {
}
