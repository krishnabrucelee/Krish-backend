package ck.panda.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

/**
 * Application validator to handle validations.
 *
 */
@Component
public class AppValidator {

    /** Validator attribute. */
    @Autowired
    private Validator validator;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /**
     * Validates the given entity.
     *
     * @param object ,which is to be validated.
     * @return <T> error if present,else returns a new error object.
     * @throws Exception if error is present.
     */
    public Errors validateEntity(Object object) throws Exception {

        Set<ConstraintViolation<Object>> constraintViolationErrors =  validator.validate(object);

        HashMap<String, String> errorsMap = convertValidationMessages(constraintViolationErrors);

        Errors errors = new Errors(messageSource);

        errors.setFieldErrors(errorsMap);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        return errors;
    }

    /**
     * Validates the entity.
     *
     * @param object ,which is to be validated.
     * @param errors ,an error object.
     * @return error is present,else new error object is returned.
     * @throws Exception if error is present.
     */
    public Errors validateEntity(Object object, Errors errors) throws Exception {

        Set<ConstraintViolation<Object>> constraintViolationErrors =  validator.validate(object);

        HashMap<String, String> errorsMap = convertValidationMessages(constraintViolationErrors);

        errors.setFieldErrors(errorsMap);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        return errors;
    }
    
    

    /**
     * Reject if null entity.
     *
     * @param name of the entity.
     * @param entity entity
     * @return new error object is returned
     * @throws Exception if object is null
     */
    public Errors rejectIfNullEntity(String name, Object entity) throws Exception {
        Errors errors = new Errors(messageSource);

        Boolean hasValues = false;

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(entity);

            if (value != null) {
                hasValues = true;
                break;
            }
        }

        if (!hasValues) {
            errors.addGlobalError("The object '" + name + "' is null.");
            throw new ApplicationException(errors);
        }

        return errors;
    }


    /**
     * Converts the validation messages.
     *
     * @param <T> constraintViolationErrors to set
     * @param constraintViolationErrors to set
     * @return <T> error messages of specific type
     */
    private <T extends Object> HashMap<String, String>
            convertValidationMessages(Set<ConstraintViolation<T>> constraintViolationErrors) {

        HashMap<String, String> failureMessages = new HashMap<String, String>();

        for (ConstraintViolation<T> constraintViolationError : constraintViolationErrors) {

            //TODO handle the error code {javax.validation.constraints.NotNull.message}
            String errorMessage = constraintViolationError.getMessage();

            try {
                errorMessage = messageSource.getMessage(errorMessage, new Object[] {},
                        LocaleContextHolder.getLocale());
            } catch (NoSuchMessageException ex) {
            }
            failureMessages.put(constraintViolationError.getPropertyPath().toString(), errorMessage);
        }
        return failureMessages;
    }

    /**
     * Create a new error entity.
     *
     * @return Errors entity
     */
    public Errors createErrors() {
        //Errors errors = beanFactory.createError();
        return null;//errors;
    }
}
