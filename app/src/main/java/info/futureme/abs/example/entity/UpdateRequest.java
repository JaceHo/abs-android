package info.futureme.abs.example.entity;

import java.io.Serializable;

/**
 * Created by Jeffrey on 9/8/16.
 */
public class UpdateRequest implements Serializable{
    private String appIdentifier;
    private int appVersionCode;

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
}
