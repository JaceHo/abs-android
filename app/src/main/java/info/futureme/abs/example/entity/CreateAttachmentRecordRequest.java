package info.futureme.abs.example.entity;

/**
 * Created by Jeffrey on 2016/6/1.
 */
public class CreateAttachmentRecordRequest {
    private String ticketid;
    private int stepconfigid;
    private int engineerid;
    private String[] pagefile_0_stepInfo;

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public int getStepconfigid() {
        return stepconfigid;
    }

    public void setStepconfigid(int stepconfigid) {
        this.stepconfigid = stepconfigid;
    }

    public String[] getPagefile_0_stepInfo() {
        return pagefile_0_stepInfo;
    }

    public void setPagefile_0_stepInfo(String[] pagefile_0_stepInfo) {
        this.pagefile_0_stepInfo = pagefile_0_stepInfo;
    }

    public int getEngineerid() {
        return engineerid;
    }

    public void setEngineerid(int engineerid) {
        this.engineerid = engineerid;
    }
}
