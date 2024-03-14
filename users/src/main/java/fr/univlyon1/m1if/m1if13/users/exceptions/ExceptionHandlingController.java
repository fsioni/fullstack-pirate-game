package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Controller
@ControllerAdvice
public class ExceptionHandlingController {
    @ExceptionHandler(BaseHttpException.class)
    public ResponseEntity<Object> handleBaseHttpException(BaseHttpException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", e.getMessage());
        body.put("status", e.getStatus());

        return new ResponseEntity<>(body, e.getStatus());
    }

}
