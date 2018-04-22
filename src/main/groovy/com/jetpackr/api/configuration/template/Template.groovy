package com.jetpackr.api.configuration.template

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class Template {
    @NotNull
    @Size(min = 1)
    List<String> files
}