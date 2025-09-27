package com.htwsaar.anzeigetafel.server.controller;

public interface ControllerApi {

    // Get called only once when new controller get loaded through
    void onStart();

    // Get called when database or serializer updated (add, update, remove)
    void onUpdate();
}
