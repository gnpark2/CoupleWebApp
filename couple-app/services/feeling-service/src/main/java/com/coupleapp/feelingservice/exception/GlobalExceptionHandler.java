package com.coupleapp.feelingservice.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,Object>> h1(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("status",400,"message",ex.getMessage()));}
    @ExceptionHandler(Exception.class) public ResponseEntity<Map<String,Object>> h2(Exception ex){log.error("err",ex);return ResponseEntity.status(500).body(Map.of("status",500,"message","Internal server error"));}
}
