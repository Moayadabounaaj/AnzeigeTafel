package com.htwsaar.anzeigetafel.server;

import com.htwsaar.anzeigetafel.service.DisplayBoardService;
import com.htwsaar.anzeigetafel.service.MessageService;
import com.htwsaar.anzeigetafel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbService {

    @Autowired
    private DisplayBoardService displayBoardService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    public DisplayBoardService getDisplayBoardService() {
        return displayBoardService;
    }

    public UserService getUserService() {
        return userService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
