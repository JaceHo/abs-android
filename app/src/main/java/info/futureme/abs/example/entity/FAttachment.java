package info.futureme.abs.example.entity;

import android.os.Parcel;
import android.os.Parcelable;

import info.futureme.abs.example.entity.g.Attachment;

/**
 * Created by hippo on 12/10/15.
 */
public class FAttachment extends Attachment implements Parcelable {
    private boolean isSyncNow = false;

    public static final Creator<FAttachment> CREATOR = new Creator<FAttachment>() {
        public FAttachment createFromParcel(Parcel source) {
            return new FAttachment(source);
        }

        public FAttachment[] newArray(int size) {
            return new FAttachment[size];
        }
    };

    public FAttachment() {
        setStatus(0);
        setFailedTime(0);
    }

    public FAttachment(Parcel in) {
        this.setType(in.readString());
        this.setTicketid(in.readString());
        this.setStatus((Integer) in.readValue(Integer.class.getClassLoader()));
        this.setPath(in.readString());
        this.setLazyId(in.readString());
        this.setClientName(in.readString());
        this.setAccount(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getType());
        dest.writeString(this.getTicketid());
        dest.writeValue(this.getStatus());
        dest.writeString(this.getPath());
        dest.writeString(this.getLazyId());
        dest.writeString(this.getClientName());
        dest.writeString(this.getAccount());
    }

    @Override
    public String toString() {
        return "FAttachment{} " + super.toString();
    }

    public static FAttachment from(Attachment attachment) {
        FAttachment fAttachment = new FAttachment();
        fAttachment.setStatus(attachment.getStatus());
        fAttachment.setPath(attachment.getPath());
        fAttachment.setClientName(attachment.getClientName());
        fAttachment.setFailedTime(attachment.getFailedTime());
        fAttachment.setType(attachment.getType());
        fAttachment.setAccount(attachment.getAccount());
        fAttachment.setLazyId(attachment.getLazyId());
        fAttachment.setTicketid(attachment.getTicketid());
        fAttachment.setTime(attachment.getTime());
        return fAttachment;
    }

    public boolean isSyncNow() {
        return isSyncNow;
    }

    public void setSyncNow(boolean syncNow) {
        isSyncNow = syncNow;
    }
}
