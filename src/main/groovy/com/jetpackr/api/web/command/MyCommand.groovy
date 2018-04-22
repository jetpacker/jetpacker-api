package com.jetpackr.api.web.command

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.validation.constraints.NotNull

/**
 * Created by donny on 30/11/16.
 */
class MyCommand {
    @NotNull
    Machine machine
    
    Kits kits
    Map<String, Container> containers

    @JsonIgnore
    List<String> namedVolumes
}
