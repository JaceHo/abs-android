package info.futureme.abs.example.entity;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Ticket implements Serializable{
    public boolean isHeader;
    public int sectionFirstPosition;
    public int sectionManager;
    public int total;

    private String itsmcode;
    private String ticketid;
    private String issue;
    private String project;
    private String priority;
    private Customer customer = new Customer();
    private Date ctime;
    private double amount;
    private Date[] timers;
    private SLA sla;
    private String resplevel;
    private String notes;
    private Double score;
    private TicketStatus status = new TicketStatus();

    public Ticket(boolean isHeader, int i, int sectionManager) {
        this.isHeader = isHeader;
        this.sectionFirstPosition = i;
        this.sectionManager = sectionManager;
    }

    public Ticket() {

    }

    public String getItsmcode() {
        return itsmcode;
    }

    public void setItsmcode(String itsmcode) {
        this.itsmcode = itsmcode;
    }

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date[] getTimers() {
        return timers;
    }

    public void setTimers(Date[] timers) {
        this.timers = timers;
    }

    public SLA getSla() {
        return sla;
    }

    public void setSla(SLA sla) {
        this.sla = sla;
    }

    public String getResplevel() {
        return resplevel;
    }

    public void setResplevel(String resplevel) {
        this.resplevel = resplevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(TicketStatus status) {
        if(status == null)
            status = new TicketStatus();
        this.status = status;
    }

    public TicketStatus getStatus(){
        return this.status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "isHeader=" + isHeader +
                ", sectionFirstPosition=" + sectionFirstPosition +
                ", sectionManager=" + sectionManager +
                ", total=" + total +
                ", itsmcode='" + itsmcode + '\'' +
                ", ticketid='" + ticketid + '\'' +
                ", issue='" + issue + '\'' +
                ", project='" + project + '\'' +
                ", priority='" + priority + '\'' +
                ", customer=" + customer+
                ", ctime=" + ctime +
                ", amount=" + amount +
                ", timers=" + Arrays.toString(timers) +
                ", sla=" + sla +
                ", resplevel='" + resplevel + '\'' +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                '}';
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
