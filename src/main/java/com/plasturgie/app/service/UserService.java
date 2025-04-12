package com.plasturgie.app.service;

import com.plasturgie.app.dto.UserDTO;
import com.plasturgie.app.model.User;

import java.util.List;

public interface UserService {
    User registerUser(UserDTO userDTO);
    
    User findById(Long id);
    
    User findByUsername(String username);
    
    List<User> findAllUsers();
    
    User updateUser(Long id, UserDTO userDTO);
    
    void deleteUser(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
