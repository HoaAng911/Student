package com.example.demo.users.service;

import com.example.demo.users.model.entity.User;
import com.example.demo.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> login(String username, String password) {
        // Trong thực tế sẽ dùng PasswordEncoder, ở đây làm đơn giản theo yêu cầu
        return userRepository.findByUsernameAndIsActiveTrue(username)
                .filter(user -> user.getPassword().equals(password));
    }
}
