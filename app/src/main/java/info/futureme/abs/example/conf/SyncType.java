package info.futureme.abs.example.conf;

/**
 * Created by hippo on 1/8/16.
 */
public enum SyncType {
    APP_SYNC_CODE("APP_SYNC_CODE"),
    APP_STEP_IMG("APP_STEP_IMG"),
    APP_SITE_LIST("APP_SITE_LIST"),
    APP_USER("APP_USER"),
    APP_STATUS("APP_STATUS"),
    APP_PROJECT("APP_PROJECT"),
    APP_ADDRESS("APP_ADDRESS");

    private final String value;
    SyncType(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }
}
