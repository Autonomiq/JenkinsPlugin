package io.jenkins.plugins.autonomiq.util;

import okhttp3.*;

public class WebsocketData {
    WebSocket socket;
    WebsocketListener listener;

    public WebsocketData(WebSocket socket, WebsocketListener listener) {
        this.socket = socket;
        this.listener = listener;
    }

    public WebSocket getSocket() {
        return socket;
    }

    public WebsocketListener getListener() {
        return listener;
    }
}
