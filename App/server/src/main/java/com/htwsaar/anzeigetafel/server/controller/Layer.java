package com.htwsaar.anzeigetafel.server.controller;

public interface Layer {
    void onStart(String[] args);
    void onRun();
    void onShutdown();
}
