package info.futureme.abs.example.entity;

import java.io.Serializable;

/**
 * Created by Jeffrey on 6/19/16.
 */
public class SearchItem implements Serializable {
    private String name;
    private String projectName;
    private int id = - 1;
    private int projectId = -1;
    private int custAddressId = -1;
    private String serialNum;
    private int deviceId;
    private int faultId = -1;
    private String faultName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getCustAddressId() {
        return custAddressId;
    }

    public void setCustAddressId(int custAddressId) {
        this.custAddressId = custAddressId;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getFaultId() {
        return faultId;
    }

    public void setFaultId(int faultId) {
        this.faultId = faultId;
    }

    public String getFaultName() {
        return faultName;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    @Override
    public String toString() {
        return "SearchItem{" +
                "name='" + name + '\'' +
                ", projectName='" + projectName + '\'' +
                ", id=" + id +
                ", projectId=" + projectId +
                ", custAddressId=" + custAddressId +
                ", serialNum='" + serialNum + '\'' +
                ", deviceId=" + deviceId +
                ", faultId=" + faultId +
                ", faultName='" + faultName + '\'' +
                '}';
    }
}
