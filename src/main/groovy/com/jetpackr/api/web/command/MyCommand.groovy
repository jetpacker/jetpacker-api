package com.jetpackr.api.web.command

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by donny on 30/11/16.
 */
class MyCommand {
    @Valid
    @NotNull
    Machine machine

    @Valid
    Kits kits = new Kits()

    @Valid
    @Size(min = 1)
    Map<String, Container> containers

    @JsonIgnore
    List<String> namedVolumes
}