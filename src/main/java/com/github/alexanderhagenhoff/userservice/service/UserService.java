package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(UUID uuid) throws NotFoundException {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException(uuid));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User createUser(User user) throws EmailAlreadyInUseException {
        final String userEmail = user.getEmail();

        if (userRepository.existsByEmail(userEmail)) {
            throw new EmailAlreadyInUseException(userEmail);
        }
        return userRepository.save(user);
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }
}
