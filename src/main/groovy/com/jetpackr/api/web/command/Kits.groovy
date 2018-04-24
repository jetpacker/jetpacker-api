package com.jetpackr.api.web.command

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.validation.Valid

/**
 * Created by wolf on 30/4/17.
 */
class Kits {
    @Valid
    Kit jdk

    @Valid
    Kit node

    @JsonIgnore
    Kit ruby = new Kit()
}
