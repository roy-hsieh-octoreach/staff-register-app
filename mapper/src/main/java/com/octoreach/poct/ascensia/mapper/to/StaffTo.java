package com.octoreach.poct.ascensia.mapper.to;

import android.os.Parcel;
import android.os.Parcelable;

public class StaffTo implements Parcelable {

	private String staffIdNo;

	private String staffId;

	private String staffName;

	private String staffZone;

    public String getStaffIdNo() {
        return staffIdNo;
    }

    public void setStaffIdNo(String staffIdNo) {
        this.staffIdNo = staffIdNo;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffZone() {
        return staffZone;
    }

    public void setStaffZone(String staffZone) {
        this.staffZone = staffZone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.staffIdNo);
        dest.writeString(this.staffId);
        dest.writeString(this.staffName);
        dest.writeString(this.staffZone);
    }

    public StaffTo() {}

    protected StaffTo(Parcel in) {
        this.staffIdNo = in.readString();
        this.staffId = in.readString();
        this.staffName = in.readString();
        this.staffZone = in.readString();
    }

    public static final Creator<StaffTo> CREATOR = new Creator<StaffTo>() {
        @Override
        public StaffTo createFromParcel(Parcel source) {
            return new StaffTo(source);
        }

        @Override
        public StaffTo[] newArray(int size) {
            return new StaffTo[size];
        }
    };
}
