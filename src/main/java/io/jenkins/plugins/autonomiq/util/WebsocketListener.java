package io.jenkins.plugins.autonomiq.util;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.LinkedList;

public class WebsocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private Boolean closed = false;
    private Boolean failed = false;
    private LinkedList<String> messages = new LinkedList<String>();
    private final Object msgLock = new Object();
    private Integer binMsgCount = 0;

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        synchronized (msgLock) {
            messages.add(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        // binary messages not expected
        binMsgCount++;
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        closed = true;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        failed = true;
    }

    public Integer getBinMsgCount() {
        return binMsgCount;
    }

    public Boolean isClosed() {
        return closed;
    }

    public Boolean isFailed() {
        return failed;
    }

    public String getMsg() {

        String ret;
        synchronized (msgLock) {
            ret = messages.poll();
        }

        return ret;
    }
}


