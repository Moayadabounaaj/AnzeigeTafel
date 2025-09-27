package com.htwsaar.anzeigetafel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_table")
public class User implements Serializable {

    public enum Role {
        Client, ClientCoordinator
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;
    @NotEmpty(message = "UserName darf nicht leer sein")
    @Column
    private String username;

    @NotEmpty(message = "Password darf nicht leer sein")
    @Column
    private String password;

    @Column(name = "displayBoardID")
    private int displayBoardId;

    @Column
    private Role role;

    @OneToMany(mappedBy="sender")
    private List<Message> messages;
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.messages = new ArrayList<>();
    }
    public User() {

    }


    /**
     * Get the userID
     * @return UserID
     */
    public int getUserID() {
        return userID;
    }
    /**
     * Set the userID
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;

        if (userID < 0) {
            throw new DatabaseException("UserID darf nicht negativ sein.");
        }
        this.userID = userID;
    }

    /**
     * Get the username
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    /**
     * Set the username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
        if (username == null || username.trim().isEmpty()) {
            throw new DatabaseException("Der Benutzername darf nicht leer sein.");
        }
        this.username = username;
    }

    /**
     * Get the password
     * @return Password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set the password
     * @param password
     */

    public void setPassword(String password) {
        this.password = password;

        if (password == null || password.trim().isEmpty()) {
            throw new DatabaseException("Das Passwort darf nicht leer sein.");
        }
        this.password = password;
    }

    /**
     * Get the role
     * @return Role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Set the role
     * @param role
     */
    public void setRole(Role role) {
        this.role = role;
        if (role == null) {
            throw new DatabaseException("Die Rolle darf nicht leer sein.");
        }
        this.role = role;

    }

    /**
     * Get the displayBoardID
     * @return DisplayBoardID
     */
    public void setDisplayBoardId(int displayBoardId) {
        this.displayBoardId = displayBoardId;
    }

    /**
     * Set the displayBoardID
     * @return  displayBoardID
     */
    public int getDisplayBoardId() {
        return displayBoardId;
    }

    public void sendMessage(String content)
    {

    }

    public void modifyMessage(int messageID, String newContent)
    {
        // Client RMI
        //server.modifyMessage(userID, messageID, newContent);
    }

    public void deleteMessage(int messageID)
    {

    }

    public void notifyUser()
    {

    }

    public void displayMessages()
    {

    }



    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
