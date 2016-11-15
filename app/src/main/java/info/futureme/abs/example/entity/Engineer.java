package info.futureme.abs.example.entity;

/**
 * Created by Jeffrey on 2016/5/6.
 */
public class Engineer {
    private String itsmcode;
    private String engineerid;
    private String name;
    /*
    工程师状态
0 - 可接单, 1-不可接单-系统示忙, 2-不可接单-忙碌 3-不可接单-休假, 4-不可接单-病假 ,5-不可接单-培训 6-离岗

     */
    private int status;
    private String phone;
    private String email;
    private String avatar;
    private String loginname;

    public String getItsmcode() {
        return itsmcode;
    }

    public void setItsmcode(String itsmcode) {
        this.itsmcode = itsmcode;
    }

    public String getEngineerid() {
        return engineerid;
    }

    public void setEngineerid(String engineerid) {
        this.engineerid = engineerid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAppconfigjson() {
        return null;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }
}
