package ck.panda.util.error;

/**
 * i18n key will be handled here to return message.
 */
public interface MessageByLocaleService {
    /**
     * Get the message by key.
     *
     * @param id message key.
     * @return message value.
     */
    public String getMessage(String id);
}
