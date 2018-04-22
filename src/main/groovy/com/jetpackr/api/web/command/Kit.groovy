package com.jetpackr.api.web.command

import javax.validation.Valid
import javax.validation.constraints.Size

/**
 * Created by donny on 30/11/16.
 */
class Kit extends Platform {
    String dependencyVersion

    @Valid
    @Size(min=1)
    Map<String, Kit> extensions
}
