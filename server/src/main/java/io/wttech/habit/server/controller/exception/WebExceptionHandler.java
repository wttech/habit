package io.wttech.habit.server.controller.exception;

import io.wttech.habit.server.request.RequestNotProcessableException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);

  @ExceptionHandler(RequestNotProcessableException.class)
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
  @ApiResponse(responseCode = "422", description = "", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResult.class)))
  public ErrorResult handleRequestNotProcessable(RequestNotProcessableException e) {
    LOGGER.error("Request not processable", e);
    return ErrorResult.of(e.getMessage());
  }

}
