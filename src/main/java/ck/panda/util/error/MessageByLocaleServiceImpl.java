package ck.panda.util.error;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Message source service for get message by key.
 */
@Component
public class MessageByLocaleServiceImpl implements MessageByLocaleService {
    /** Message source attribute for internationalization support. */
    @Autowired
    private MessageSource messageSource;

    @Override
    public String getMessage(String id) {
         try {
             Locale locale = LocaleContextHolder.getLocale();
             String message = "";
             // split key and convert messages for current locale.
             // For resource limit check while create new vm instance.
             if (id.contains(" ")) {
                 String[] errMsg = id.split(" ");
                 for (String errKey : errMsg) {
                     message += messageSource.getMessage(errKey, null, locale) + " ";
                 }
                 return message.trim();
             }
             // convert message for current locale.
             return messageSource.getMessage(id, null, locale);
         } catch (NoSuchMessageException ex) {
            // Do nothing, the i18n key will be returned
            return id;
        } catch (NullPointerException ex) {
            // Do nothing, the i18n key will be returned
        }
        return id;
    }
}
