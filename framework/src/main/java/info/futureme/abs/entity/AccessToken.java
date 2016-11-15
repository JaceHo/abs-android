package info.futureme.abs.entity;

/**
 * common accesstoken entity used to access remote resource
 */

public class AccessToken {

    private String token;

    private String tokenType;

    private Long expiretime;

    private String engineerid;

    private String refreshtoken;

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public String getEngineerid() {
        return engineerid;
    }

    public void setEngineerid(String engineerid) {
        this.engineerid = engineerid;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "token='" + token + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", engineerid=" + engineerid +
                ", refreshtoken='" + refreshtoken + '\'' +
                '}';
    }

    public Long getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(Long expiretime) {
        this.expiretime = expiretime;
    }
}
