package com.callcenter.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends DefaultHandlerExceptionResolver {

    @ExceptionHandler(value = CallCenterServiceException.class)
    public void handleException(CallCenterServiceException ex, HttpServletResponse response) throws IOException {
        log.error(ex.getMessage(), ex);
        response.sendError(ex.getStatus().value());
    }

}
