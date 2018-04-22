package com.jetpackr.api.web.command

import javax.validation.constraints.Size

/**
 * Created by donny on 30/11/16.
 */
class Container extends Platform {
    String command
    Map<String, String> ports

    @Size(min = 1, max = 20)
    Map<String, String> environment

    List<String> volumes
}
