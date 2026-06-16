package com.coupleapp.userservice.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegal(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("status",400,"message",ex.getMessage()));}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneral(Exception ex){log.error("Unhandled",ex);return ResponseEntity.status(500).body(Map.of("status",500,"message","Internal server error"));}
}
