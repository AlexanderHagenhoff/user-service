package com.github.alexanderhagenhoff.userservice.service.mapper;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.service.dto.CreateUserDto;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
  //  UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class); //check this

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto dto);

    UserDto toDto(User user);
}
