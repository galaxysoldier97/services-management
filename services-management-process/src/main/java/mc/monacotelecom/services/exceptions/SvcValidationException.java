package mc.monacotelecom.services.exceptions;

import mc.monacotelecom.inventory.common.nls.LocalizedException;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;

public class SvcValidationException extends LocalizedException {
    public SvcValidationException(LocalizedMessageBuilder messageBuilder, String messageKey, Object... args) {
        super(messageBuilder, messageKey, args);
    }
}
