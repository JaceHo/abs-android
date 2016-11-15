package info.futureme.abs.example.entity;

import java.io.Serializable;

/**
 * Created by Jeffrey on 2016/5/9.
 */
public class TicketStatus implements Serializable{
    private int state;
    private String currstate;
    private String prevstate;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPrevstate() {
        return prevstate;
    }

    public void setPrevstate(String prevstate) {
        this.prevstate = prevstate;
    }

    public String getCurrstate() {
        return currstate;
    }

    public void setCurrstate(String currstate) {
        this.currstate = currstate;
    }

    @Override
    public String toString() {
        return "TicketStatus{" +
                "state=" + state +
                ", currstate='" + currstate + '\'' +
                ", prevstate='" + prevstate + '\'' +
                '}';
    }
}
