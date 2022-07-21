package com.octoreach.poct.ascensia.mapper.to;

import java.io.Serializable;

public class VersionTo implements Serializable {

    private int version;

    private String networkPath;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNetworkPath() {
        return networkPath;
    }

    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
    }
}
