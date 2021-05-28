package com.kagaconnect.streamrd.webrtc;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class WebSocket extends WebSocketClient {
    public interface Listener{
        void call(Object... args);
    }

    public WebSocket(URI serverUri) {
        super(serverUri);
    }

    public void disconnect(){
        this.close();
    }

    public void on(String name, Listener listener){

    }

    public void emit(String name, JSONObject message){

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

}