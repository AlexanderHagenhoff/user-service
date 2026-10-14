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
        User createdUser = userRepository.save(user);

        return userDtoMapper.toDto(createdUser);
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Transactional
    public UserDto getOrCreateUser(String externalSubjectId, String emailHash) {
        User userEntity = userRepository.findByExternalSubjectId(externalSubjectId)
                .map(existingUser -> throwExceptionOnEmailAlreadyInUse(existingUser, emailHash))
                .orElseGet(() -> createNewUser(externalSubjectId, emailHash));

        return userDtoMapper.toDto(userEntity);
    }

    private User throwExceptionOnEmailAlreadyInUse(User user, String emailHash) {
        String currentEmail = user.getEmail();

        if (!emailHash.equals(currentEmail)) {
            throw new EmailAlreadyInUseException(emailHash);
        }

        return user;
    }

    private User createNewUser(String externalSubjectId, String emailHash) {
        User newUser = new User();
        newUser.setExternalSubjectId(externalSubjectId);
        newUser.setEmail(emailHash);

        return userRepository.save(newUser);
    }
}
