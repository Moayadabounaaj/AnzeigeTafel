package com.htwsaar.anzeigetafel.client;

import com.htwsaar.anzeigetafel.client.controller.LoginController;
import com.htwsaar.anzeigetafel.server.controller.Layer;

public class ClientEntryPoint {
    public static void main(String[] args) {
        //Layer layer = new ConsoleDialog();
        Layer layer = new LoginController();

        layer.onStart(args);
        layer.onRun();
        layer.onShutdown();
    }
}