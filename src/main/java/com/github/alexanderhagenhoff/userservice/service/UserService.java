package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailHashAlreadyInUseException;
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

    public UserDto getUserByEmail(String emailHash) {
        User user = userRepository.findByEmailHash(emailHash);

        return userDtoMapper.toDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmailHash(createUserDto.emailHash())) {
            throw new EmailHashAlreadyInUseException(createUserDto.emailHash());
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
                .map(existingUser -> throwExceptionOnEmailHashAlreadyInUse(existingUser, emailHash))
                .orElseGet(() -> createNewUser(externalSubjectId, emailHash));

        return userDtoMapper.toDto(userEntity);
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
