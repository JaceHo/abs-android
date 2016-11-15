package info.futureme.abs.example.entity;

import java.util.ArrayList;

/**
 * Created by Jeffrey on 2016/4/29.
 */
public class TicketFilter {
    private ArrayList<PositionLatLng> rectangle = new ArrayList<>();
    private String timerange = "20000101T000000-";
    private String status;
    private String project;
    private String keyword;
    //from-to
    private String score;

    public String getTimerange() {
        return timerange;
    }

    public void setTimerange(String timerange) {
        this.timerange = timerange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public ArrayList<PositionLatLng> getRectangle() {
        return rectangle;
    }

    public void setRectangle(ArrayList<PositionLatLng> rectangle) {
        this.rectangle = rectangle;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void clear() {
        timerange = "20000101T000000-";
        status = null;
        project = null;
        keyword = null;
        score = null;
    }
}
