package com.htwsaar.anzeigetafel.model;


import com.htwsaar.anzeigetafel.model.DatabaseException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@Table(name = "displayboard")
public class DisplayBoard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardID")

    private int boardID;
    @NotNull
    @Column(name = "boardName")
    private String boardName;

    public DisplayBoard(int boardID) {
        this.boardID = boardID;

        if (boardID < 0) {
            throw new DatabaseException("BoardID darf nicht negativ sein.");
        }
    }
    /**
     * Constructor
     * @param boardName
     */

    public DisplayBoard(String boardName){
        this.boardName = boardName;
    }
    /**
     * Constructor
     */

    public DisplayBoard() {

    }


    public void setBoardID(int boardID) {
        this.boardID = boardID;
    }


    /**
     * Get the boardID
     * @return BoardID
     */
    public int getBoardID()
    {
        return boardID;
    }




    /**
     * add a message to the board
     * @param senderID
     * @param  content
     */
    public void addMessage(int senderID, String content) {
    }

    /**
     * modify a message
     * @param messageID
     * @param newContent
     */
    public void modifyMessage(int messageID, String newContent) {
    }

    /**
     * delete a message
     * @param messageID
     */
    public void deleteMessage(int messageID) {
    }

    /**
     * Get the board name
     * @return BoardName
     */
    public String getBoardName() {
        return this.boardName;
    }
}
