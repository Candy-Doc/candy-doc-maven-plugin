@SharedKernel(
    name = "shared_kernel_one",
    description = "description of shared kernel",
    relations = {
      "io.candydoc.sample.valid_bounded_contexts.bounded_context_one.package-info",
      "io.candydoc.sample.valid_bounded_contexts.bounded_context_two.package-info"
    })
package io.candydoc.sample.valid_bounded_contexts.shared_kernel;

import io.candydoc.ddd.annotations.SharedKernel;
