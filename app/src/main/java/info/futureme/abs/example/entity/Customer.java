package info.futureme.abs.example.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by Jeffrey on 2016/4/29.
 */
public class Customer implements Serializable{
    private String clientid;
    private String name;
    private String address;
    private String level;
    private double longitude;
    private double latitude;
    private String contact;
    private String email;
    private String phone;
    private LinkedHashMap<String, String> others;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LinkedHashMap<String, String> getOthers() {
        return others;
    }

    public void setOthers(LinkedHashMap<String, String> others) {
        this.others = others;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
