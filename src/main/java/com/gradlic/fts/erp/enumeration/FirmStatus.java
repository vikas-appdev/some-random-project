package com.gradlic.fts.erp.enumeration;

public enum FirmStatus {
    RUNNING("RUNNING"),
    TEMPORARILY_CLOSED("TEMPORARILY_CLOSED"),
    PERMANENTLY_CLOSE("PERMANENTLY_CLOSE");

    private final String status;

    FirmStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }
}
