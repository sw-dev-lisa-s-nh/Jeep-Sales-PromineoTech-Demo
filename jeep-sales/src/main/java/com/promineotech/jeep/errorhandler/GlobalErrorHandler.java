package com.promineotech.jeep.errorhandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
  
  private enum LogStatus {
    STACK_TRACE,
    MESSAGE_ONLY
  }
  
  
  /**
   * 
   * @param e
   * @param webRequest
   * @return
   */
  
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e, WebRequest webRequest) {
    return createExceptionMessage (e, HttpStatus.BAD_REQUEST, 
        webRequest, LogStatus.MESSAGE_ONLY);
  }
  
  /**
   * 
   * @param e
   * @param webRequest
   * @return
   */
  
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolationException(
      ConstraintViolationException e, WebRequest webRequest) {
    return createExceptionMessage (e, HttpStatus.BAD_REQUEST, 
        webRequest, LogStatus.MESSAGE_ONLY);
  }
  
  /**
   *  Unexpected Error
   * @param e
   * @param webRequest
   * @return
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleException(Exception e, WebRequest webRequest) {
    return createExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, 
        webRequest, LogStatus.STACK_TRACE);
  }
 
  /**
   * 
   * @param e
   * @param webRequest
   * @return
   */
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNoSuchElementException(
      NoSuchElementException e, WebRequest webRequest) {
    return createExceptionMessage(e, HttpStatus.NOT_FOUND, 
        webRequest, LogStatus.STACK_TRACE);
  }

  private Map<String, Object> createExceptionMessage(
      Exception e, HttpStatus status, WebRequest webRequest, LogStatus logStatus) {
    Map<String, Object> error = new HashMap<String,Object>();
    String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    
    // SHOULD THIS BE URI, not URL???
    // Trying to fix the assertion in test FetchJeepTest to actually compare the "uri" with "/jeep"
    if (webRequest instanceof ServletWebRequest) {
      // String request = ((ServletWebRequest)webRequest).getRequest().getRequestURL().toString();
      String request = ((ServletWebRequest)webRequest).getRequest().getRequestURI();
      //error.put("uri",  request.substring(request.length()-6));
      error.put("uri",  request);
    } else {
      error.put("uri", webRequest.getContextPath());
    }
    error.put("message", e.toString());
    error.put("status code", status.value());
    error.put("timestamp", timestamp);
    error.put("reason", status.getReasonPhrase());
    
    if (logStatus == LogStatus.MESSAGE_ONLY) {
      log.error("Exception: {}", e.toString());
    } else {
      log.error("Exception: ", e);
    }
    return error;
  }
}
