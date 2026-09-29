package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import com.github.alexanderhagenhoff.userservice.service.dto.CreateUserDto;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
import com.github.alexanderhagenhoff.userservice.service.mapper.UserDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private static final String ERROR_MESSAGE_EMAIL_CHANGED = "Email has been changed! User [%s]. Login not possible.";

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public UserDto getUser(UUID uuid) throws NotFoundException {
        return userRepository.findById(uuid)
                .map(userDtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException(uuid));
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return userDtoMapper.toDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.email())) {
            throw new EmailAlreadyInUseException(createUserDto.email());
        }
        User user = userDtoMapper.toEntity(createUserDto);
        return userDtoMapper.toDto(userRepository.save(user));
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Transactional
    public UserDto getOrCreateUser(String externalSubjectId, String email) {
        User userEntity = userRepository.findByExternalSubjectId(externalSubjectId)
                .map(existingUser -> throwExceptionOnEmailChange(existingUser, email))
                .orElseGet(() -> createNewUser(externalSubjectId, email));

        return userDtoMapper.toDto(userEntity);
    }

    private User throwExceptionOnEmailChange(User user, String emailHash) {
        String currentEmail = user.getEmail();

        if (!emailHash.equals(currentEmail)) {
            UUID userId = user.getId();
            throw new RuntimeException(ERROR_MESSAGE_EMAIL_CHANGED.formatted(userId));
        }

        return user;
    }

    private User createNewUser(String externalSubjectId, String email) {
        User newUser = new User();
        newUser.setExternalSubjectId(externalSubjectId);
        newUser.setEmail(email);

        return userRepository.save(newUser);
    }
}
