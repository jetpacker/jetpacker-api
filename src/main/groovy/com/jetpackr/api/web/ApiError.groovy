package com.jetpackr.api.web

import groovy.transform.CompileStatic

@CompileStatic
class ApiError {
    String message
    List<String> errors
}