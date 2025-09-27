package com.htwsaar.anzeigetafel.service;

import com.htwsaar.anzeigetafel.model.Message;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private UserRepository userRepository;
   @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user
     * @param user
     * @return int
     */
    @Override
    public int createUser(User user) {
        userRepository.save(user);

        return user.getUserID();
    }
    /**
     * Delete a user by id
     * @param id
     */
    @Override
    public void deleteuserById(int id) {
      userRepository.deleteById(id);
    }
    /**
     * Update a user
     * @param user
     */

    @Override
    public void updateUser(User user) {
        Optional<User> userOptional = this.userRepository.findById(user.getUserID());
        if (userOptional.isPresent()) {
            userRepository.save(user);
        } else {
            throw new RuntimeException("Message not founded");
        }
    }
    /**
     * Find a user by id
     * @param id
     * @return User
     */

    @Override
    public User findUserById(int id) {
        User user = userRepository.findAll().stream().filter(a-> a.getUserID() == id).findFirst().get();

        return user;
    }
    /**
     * Find a user by name
     * @param name
     * @return User
     */
    @Override
    public User findUserByName(String name) {
        if (name == null) {
            return null;
        }

        List<User> users = userRepository.findAll();
        if (users == null) {
            return null;
        }

        Optional<User> userOptional = users.stream()
                .filter(a -> a.getUsername() != null && a.getUsername().equalsIgnoreCase(name))
                .findFirst();

        return userOptional.orElse(null); // Return null if user not found
    }
    /**
     * Get all users
     * @return List<User>
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
