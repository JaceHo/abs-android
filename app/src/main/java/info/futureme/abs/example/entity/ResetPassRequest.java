package info.futureme.abs.example.entity;

/**
 * Created by Jeffrey on 2016/5/9.
 */
public class ResetPassRequest {
    private String securitycode;
    private String password;
    private String loginname;

    public String getSecuritycode() {
        return securitycode;
    }

    public void setSecuritycode(String securitycode) {
        this.securitycode = securitycode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }
}
