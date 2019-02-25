package lt.js.ratechanges.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler({ IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse processIllegalArgumentException(IllegalArgumentException ex) {
        return new ExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse processConversionFailedException(MethodArgumentTypeMismatchException ex) {
        return new ExceptionResponse("invalid '" + ex.getName() + "' parameter");
    }

    @ExceptionHandler({ RuntimeException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionResponse processRuntimeException(RuntimeException ex) {
        return new ExceptionResponse("internal server error");
    }
    
    public static class ExceptionResponse {
        private String message;
        public ExceptionResponse(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

}
