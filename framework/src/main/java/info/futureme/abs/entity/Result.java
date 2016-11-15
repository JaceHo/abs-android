package info.futureme.abs.entity;

/**
 * default rest api result entity
 */
public class Result<T> {
    //used in rest api
    private String reason;
    private int ecode;
    private T result;

    //used in js&native api
    private T data;
    private int status;
    private String message;

    public int getEcode() {
        return ecode;
    }

    public void setEcode(int ecode) {
        this.ecode = ecode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Result{" +
                "reason='" + reason + '\'' +
                ", ecode=" + ecode +
                ", result=" + result +
                ", data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
