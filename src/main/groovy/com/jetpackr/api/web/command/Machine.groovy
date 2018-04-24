package com.jetpackr.api.web.command

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * Created by donny on 30/11/16.
 */
class Machine {
    @NotBlank
    String box

    @NotNull
    @Min(1024L)
    Long memory

    @NotBlank
    String synchronization

    @NotBlank
    String timezone
}