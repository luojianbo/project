package com.runfast.gateway.vo;

/**
 * @Description
 * @Author luojianbo
 * @Date2019/10/31 17:27
 **/
public class GrayscaleInfo {
    private boolean isOpen;
    private String service;
    private String clientVersion;
    private String remoteIp;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
