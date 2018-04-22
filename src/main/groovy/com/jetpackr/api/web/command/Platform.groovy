package com.jetpackr.api.web.command

import javax.validation.constraints.NotBlank

/**
 * Created by wolf on 1/5/17.
 */
class Platform {
    String alias

    @NotBlank
    String version
}
