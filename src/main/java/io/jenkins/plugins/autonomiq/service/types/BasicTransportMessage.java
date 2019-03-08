package io.jenkins.plugins.autonomiq.service.types;

public class BasicTransportMessage {

    private String sessionId;
    private Integer msgType;
    private String msgJson;

    public BasicTransportMessage(String sessionId, Integer msgType, String msgJson) {
        this.sessionId = sessionId;
        this.msgType = msgType;
        this.msgJson = msgJson;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public String getMsgJson() {
        return msgJson;
    }
}
