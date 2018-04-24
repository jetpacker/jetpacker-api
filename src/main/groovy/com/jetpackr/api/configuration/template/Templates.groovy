package com.jetpackr.api.configuration.template

import javax.validation.Valid
import javax.validation.constraints.NotNull

class Templates {
    @Valid
    @NotNull
    Template basic

    @Valid
    @NotNull
    Template docker

    @Valid
    @NotNull
    Template jdk

    @Valid
    @NotNull
    Template node

    @Valid
    @NotNull
    Template ruby

    @Valid
    @NotNull
    Template guard
}
