package com.htwsaar.anzeigetafel.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "message")

public class Message implements Serializable {
    private static int messageCounter = 0;
    @Id
    @Column(name = "messageID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageID;

    @ManyToOne()
    @JoinColumn(name = "userID")
    @JsonIgnore
    private User sender;

    @Column(name = "displayBoardID")
    private int displayBoardID; // @Note(basel): maybe delete this ?
    @Column(name = "content")
    private String content;
    // @Column(name = "senderID")
    private int senderID;

    @Column(name = "IsPublic")
    private boolean markedPublic;

    @Column(name = "TimeStamp")
    private String timeStamp;


    /**
     * Constructor
     * @param messageID

     * @param content
     */
    public Message(int messageID, String content) {
        this.messageID = messageID;
        this.content = content;
        if (this.sender != null)
        {
            this.sender = sender;
            this.senderID = sender.getUserID();
            if (sender.getUserID() < 0) {
                throw new DatabaseException("SenderID darf nicht negativ sein.");
            }
        }

        if (messageID < 0) {
            throw new DatabaseException("MessageID darf nicht negativ sein.");
        }
        this.messageID = messageID;

        if (content == null || content.trim().isEmpty()) {
            throw new DatabaseException("Der Inhalt der Nachricht darf nicht leer sein.");
        }
        this.content = content;
    }

    public Message() {

    }


    /**
     * Get the message Counter
     *
     */
    public int getMessageCounter() {
        return messageCounter;
    }

    /**
     * Set the message Counter
     * @param messageCounter
     */
    public void setMessageCounter(int messageCounter) {
        Message.messageCounter = messageCounter;
    }
    /**
     * set the messageID
     * @param messageID
     */
    public void setMessageID(int messageID) {
        this.messageID = messageID;

    }
    /**
     * set the senderID
     * @param senderID
     */

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    /**
     * set the content
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the messageID
     * @return  messageID
     */
    public int getMessageID() {
        return messageID;
    }

    /**
     * Get the senderID
     * @return senderID
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * Get the content
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * check if the message is marked as public
     * @return markedPublic
     */
    public boolean isMarkedPublic() {
        return markedPublic;
    }
    /**
     * set the message as public
     * @param markedPublic
     */
    public void setPublic(boolean markedPublic) {
        this.markedPublic = markedPublic;
    }
    /**
     * generate a messageID
     * @return messageCounter and increment it
     */
    private int generateMessageID() {
        return messageCounter++;
    }
    /**
     * print the message
     * @return message
     */
    @Override
    public String toString() {
        return "Message{" +
                "messageID=" + messageID +
                ", senderID=" + senderID +
                ", senderName=" + sender.getUsername() +
                ", content='" + content + '\'' +
                (markedPublic ? "Forwarded" : "Private") +
                '}';
    }

    /**
     * set the displayBoardID
     * @param displayBoardID
     */
    public void setDisplayBoardID(int displayBoardID) {
        this.displayBoardID = displayBoardID;
    }

    /**
     * get the displayBoardID
     * @return displayBoardID
     */
    public int getDisplayBoardID() {
        return displayBoardID;
    }

    /**
     * get the timeStamp
     * @return  timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * set the timeStamp
     * @param timeStamp
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * set the sender
     * @param  user
     */
    public void setUser(User user)
    {
        this.sender = user;
    }
}
