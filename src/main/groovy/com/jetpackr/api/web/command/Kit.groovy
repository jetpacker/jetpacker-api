package com.jetpackr.api.web.command

import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Created by donny on 30/11/16.
 */
class Kit extends Platform {
    @NotEmpty
    String dependencyVersion

    @Valid
    Map<String, Kit> extensions
}
