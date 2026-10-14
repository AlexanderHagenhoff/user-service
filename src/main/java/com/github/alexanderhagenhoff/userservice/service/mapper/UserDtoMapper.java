package com.github.alexanderhagenhoff.userservice.service.mapper;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.service.dto.CreateUserDto;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto dto);

    UserDto toDto(User user);
}
