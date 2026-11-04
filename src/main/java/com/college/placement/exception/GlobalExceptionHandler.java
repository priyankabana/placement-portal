package com.college.placement.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * One place for the errors that can escape a controller, so the user always
 * lands on a proper page instead of the default white error screen.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("errorTitle", "Record not found");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBrokenRule(BusinessRuleException exception, Model model) {
        model.addAttribute("errorTitle", "Request could not be completed");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    /** A wrong URL like /studnts is a 404, not a server failure. */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUnknownUrl(NoResourceFoundException exception, Model model) {
        model.addAttribute("errorTitle", "Page not found");
        model.addAttribute("errorMessage", "There is no page at " + exception.getResourcePath() + ".");
        return "error";
    }

    /** Last safety net - log the real cause, show a short message to the user. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAnythingElse(Exception exception, HttpServletRequest request, Model model) {
        log.error("Unhandled error at {}", request.getRequestURI(), exception);
        model.addAttribute("errorTitle", "Something went wrong");
        model.addAttribute("errorMessage", "Please go back and try once more.");
        return "error";
    }
}
