package ck.panda.util.web;


import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ck.panda.util.BeanFactory;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

import org.springframework.http.HttpStatus;


/**
 * Generic Exception handling controller.
 * TODO Replace instantiation of Errors object with scoped proxy, since we have to inject prototype bean (Errors) to singleton.
 */
public class ExceptionHandlingController {

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Beanfactory attribute. */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * Handle if entity is not found.
     *
     * @param ex exception to handle.
     * @return errors
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody Errors handleException(EntityNotFoundException ex) {
        Errors errors = new Errors(messageSource);
        errors.addGlobalError(ex.getMessage());
        return errors;
    }

    /**
     * Handle any precondition.
     *
     * @param ex exception to handle.
     * @return errors
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody Errors handleException(ApplicationException ex) {
        return ex.getErrors();
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ResponseBody Errors handleException(InsufficientAuthenticationException ex) {
//        Errors errors =  beanFactory.createError();
//        errors.setGlobalError(ex.getMessage());
//        return errors;
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.LOCKED)
//    @ResponseBody Errors handleException(AccessDeniedException e) {
//        Errors errors =  beanFactory.createError();
//        errors.setGlobalError("error.access.denied");
//        return errors;
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ResponseBody Errors handleException(AuthenticationFailureException e) {
//        Errors errors =  beanFactory.createError();
//        errors.setGlobalError(e.getMessage());
//        return errors;
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
//    @ResponseBody Errors handleException(UnsupportedActionException e) {
//        Errors errors =  beanFactory.createError();
//        errors.setGlobalError(e.getMessage());
//        return errors;
//    }

    /**
     * Handle transaction related exception.
     *
     * @param ex exception to handle.
     * @return errors
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody Errors handleException(TransactionSystemException ex) {
        Errors errors = new Errors(messageSource);
        errors.addGlobalError(ex.getRootCause().getMessage());
        return errors;
    }

    /**
     * Generic checked exception handler.
     *
     * @param ex the exception to handle
     * @return errors
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody Errors handleException(Exception ex) {
        Errors errors = new Errors(messageSource);
        ex.printStackTrace();
        errors.addGlobalError(ex.getMessage());
        return errors;
    }

    /**
     * Handler for optimistic lock exception.
     *
     * @param ex the exception to handle
     * @return errors
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_FAILURE)
    @ResponseBody Errors handleException(OptimisticLockException ex) {
        Errors errors = new Errors(messageSource);
        errors.addGlobalError("error.javax.optimistic.Exception");
        return errors;
    }

}

