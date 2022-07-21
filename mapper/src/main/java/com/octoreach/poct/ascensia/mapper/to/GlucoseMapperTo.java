package com.octoreach.poct.ascensia.mapper.to;

import java.io.Serializable;

public class GlucoseMapperTo implements Serializable {

    private String id;

    private String staffId;

    private String staffIdTime;

    private String patientId;

    private String patientIdTime;

    private String bedId;

    private String bedIdTime;

    private String deviceId;

    private String deviceIdTime;

    private String medicineId;

    private String medicineIdTime;

    private String specimenId;

    private String specimenIdTime;

    private String recordTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffIdTime() {
        return staffIdTime;
    }

    public void setStaffIdTime(String staffIdTime) {
        this.staffIdTime = staffIdTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientIdTime() {
        return patientIdTime;
    }

    public void setPatientIdTime(String patientIdTime) {
        this.patientIdTime = patientIdTime;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getBedIdTime() {
        return bedIdTime;
    }

    public void setBedIdTime(String bedIdTime) {
        this.bedIdTime = bedIdTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceIdTime() {
        return deviceIdTime;
    }

    public void setDeviceIdTime(String deviceIdTime) {
        this.deviceIdTime = deviceIdTime;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineIdTime() {
        return medicineIdTime;
    }

    public void setMedicineIdTime(String medicineIdTime) {
        this.medicineIdTime = medicineIdTime;
    }

    public String getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(String specimenId) {
        this.specimenId = specimenId;
    }

    public String getSpecimenIdTime() {
        return specimenIdTime;
    }

    public void setSpecimenIdTime(String specimenIdTime) {
        this.specimenIdTime = specimenIdTime;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
}
