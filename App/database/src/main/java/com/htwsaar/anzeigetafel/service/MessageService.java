package com.htwsaar.anzeigetafel.service;

import com.htwsaar.anzeigetafel.model.Message;

import java.util.List;

public interface MessageService {
    public void createMessage(Message message);

    public void deleteMessage(int messageID);

    public void updateMessage(int messageID,Message message);

   public List<Message> getAllMessages();
    public Message getMessageById(int id);


}
