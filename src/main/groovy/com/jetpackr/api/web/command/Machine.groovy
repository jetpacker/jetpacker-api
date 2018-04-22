package com.jetpackr.api.web.command

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * Created by donny on 30/11/16.
 */
class Machine {
    @NotBlank
    String box

    @NotNull
    Integer memory

    @NotBlank
    String synchronization

    @NotBlank
    String timezone

    @JsonIgnore
    Kit guard = new Kit()
}