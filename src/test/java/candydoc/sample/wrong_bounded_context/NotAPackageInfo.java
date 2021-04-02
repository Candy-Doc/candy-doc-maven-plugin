package candydoc.sample.wrong_bounded_context;

import io.candydoc.domain.annotations.BoundedContext;

@BoundedContext(description = "not supposed to be extracted")
public class NotAPackageInfo {
}
