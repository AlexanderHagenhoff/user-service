package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailHashAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.UserNotFoundException;
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

    public User getUser(UUID uuid) throws UserNotFoundException {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
    }

    public User getUserByEmailHash(String emailHash) {
        User user = userRepository.findByEmailHash(emailHash);

        return user;
    }

    @Transactional
    public User createUser(User userToCreate) {
        String emailHash = userToCreate.getEmailHash();
        if (userRepository.existsByEmailHash(emailHash)) {
            throw new EmailHashAlreadyInUseException(emailHash);
        }
        User createdUser = userRepository.save(userToCreate);

        return createdUser;
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Transactional
    public User getOrCreateUser(String externalSubjectId, String emailHash) {
        User userEntity = userRepository.findByExternalSubjectId(externalSubjectId)
                .map(existingUser -> throwExceptionOnEmailHashAlreadyInUse(existingUser, emailHash))
                .orElseGet(() -> createNewUser(externalSubjectId, emailHash));

        return userEntity;
    }

    private User throwExceptionOnEmailHashAlreadyInUse(User user, String emailHash) {
        String currentEmail = user.getEmailHash();

        if (!emailHash.equals(currentEmail)) {
            throw new EmailHashAlreadyInUseException(emailHash);
        }

        return user;
    }

    private User createNewUser(String externalSubjectId, String emailHash) {
        User newUser = new User();
        newUser.setExternalSubjectId(externalSubjectId);
        newUser.setEmailHash(emailHash);

        return userRepository.save(newUser);
    }
}
