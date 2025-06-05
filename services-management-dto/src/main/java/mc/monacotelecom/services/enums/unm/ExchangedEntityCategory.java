package mc.monacotelecom.services.enums.unm;

import java.util.HashMap;
import java.util.Map;

/**
 * Used by UNM synchronization system
  */
public enum ExchangedEntityCategory {

    SERVICE("ServiceUnmDTO");

    private static final Map<String, ExchangedEntityCategory> lookup = new HashMap<>();

    static {
        for (ExchangedEntityCategory d : ExchangedEntityCategory.values()) {
            lookup.put(d.toString(), d);
        }
    }

    public static ExchangedEntityCategory fromString(String value) {
        return lookup.get(value);
    }

    private final String value;

    ExchangedEntityCategory(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
