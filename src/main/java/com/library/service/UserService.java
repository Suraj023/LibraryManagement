package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setUserName(user.getUserName());
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean updateUser(User updatedUser) {
        return userRepository.findById(updatedUser.getId())
                .map(existingUser -> {
                    // Update fields
                    existingUser.setUserName(updatedUser.getUserName());
                    existingUser.setEmail(updatedUser.getEmail());

                    // Encode password only if it's being changed
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    // Set roles (assuming it's a collection type)
                    if (updatedUser.getRoles() != null) {
                        existingUser.setRoles(updatedUser.getRoles());
                    }

                    // Save updated entity
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }


    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
