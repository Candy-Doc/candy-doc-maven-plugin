@SharedKernel(
    name = "Shared kernel one",
    description = "One valid shared kernel",
    relations = {
      "io.candydoc.ddd.sample.one_valid_bounded_context",
      "io.candydoc.ddd.sample.second_valid_bounded_context"
    })
package io.candydoc.ddd.sample.one_valid_shared_kernel;

import io.candydoc.ddd.annotations.SharedKernel;
