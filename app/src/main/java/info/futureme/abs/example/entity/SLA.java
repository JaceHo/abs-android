package info.futureme.abs.example.entity;

import java.io.Serializable;
import java.util.Date;

public class SLA implements Serializable{
    private Date alarmtime;
    private Date overtime;
    private Date expecttime;

    public Date getAlarmtime() {
        return alarmtime;
    }

    public void setAlarmtime(Date alarmtime) {
        this.alarmtime = alarmtime;
    }

    public Date getOvertime() {
        return overtime;
    }

    public void setOvertime(Date overtime) {
        this.overtime = overtime;
    }

    @Override
    public String toString() {
        return "SLA{" +
                "alarmtime=" + alarmtime +
                ", overtime=" + overtime +
                '}';
    }

    public Date getExpecttime() {
        return expecttime;
    }

    public void setExpecttime(Date expecttime) {
        this.expecttime = expecttime;
    }
}
