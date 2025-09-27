package com.htwsaar.anzeigetafel.service;

import com.htwsaar.anzeigetafel.model.User;

import java.util.List;

public interface UserService {


    public int createUser(User user);

    public void deleteuserById(int id);

    public void updateUser(User user);

    public User findUserById(int id);

    public User findUserByName(String name);

    public List<User> getAllUsers();


}
