package info.futureme.abs.example.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hippo on 11/29/15.
 */
public class OrderMenu implements Parcelable {


    private int actionId;
    private String actionName;
    private String icon;

    protected OrderMenu(Parcel in) {
        actionId = in.readInt();
        actionName = in.readString();
        icon = in.readString();
    }

    public static final Creator<OrderMenu> CREATOR = new Creator<OrderMenu>() {
        @Override
        public OrderMenu createFromParcel(Parcel in) {
            return new OrderMenu(in);
        }

        @Override
        public OrderMenu[] newArray(int size) {
            return new OrderMenu[size];
        }
    };

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(actionId);
        dest.writeString(actionName);
        dest.writeString(icon);
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getActionId() {
        return actionId;
    }
}
