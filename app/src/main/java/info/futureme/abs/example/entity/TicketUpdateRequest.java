package info.futureme.abs.example.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TicketUpdateRequest {
    private LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    private String reason;
    private List<AttachInfos> attachments = new ArrayList<>();

    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    public void setFields(LinkedHashMap<String, String> fields) {
        this.fields = fields;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<AttachInfos> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachInfos> attachments) {
        this.attachments = attachments;
    }
}
