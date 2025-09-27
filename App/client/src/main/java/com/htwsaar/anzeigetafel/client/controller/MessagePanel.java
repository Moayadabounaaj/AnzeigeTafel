package com.htwsaar.anzeigetafel.client.controller;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MessagePanel {
    public AnchorPane anchorPane;
    public Label username;
    public Label messageContent;

    public Label timestamp;

    private int messageId;

    // message id

    public MessagePanel()
    {

    }

    public Node getContent()
    {
        return anchorPane;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
