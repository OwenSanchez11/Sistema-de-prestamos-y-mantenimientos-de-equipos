
package gestionDeEquiposDeMantenimiento.firstVersion.Exceptions;

import java.time.LocalDateTime;
import static org.aspectj.bridge.MessageUtil.error;
import static org.slf4j.helpers.Reporter.error;

import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, "Not Found",
                ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessRuleException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 409, "Business Rule Violation",ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExcepted(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 500, "Internal Server Error",ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message  = ex.getBindingResult().getFieldError().getDefaultMessage();
        
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, "Validation error", message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 409, "Database integrity violation", ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    
    @ExceptionHandler(InvalidCredentialsException.class) 
    public ResponseEntity<ErrorResponse> invalidCredentiales(InvalidCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401,"Unauthorized", ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> tokenExpired(RefreshTokenExpiredException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 401, "Unautohrized", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
}
