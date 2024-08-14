package com.example.taskmanagment.application.utils.mappers;

import com.example.taskmanagment.adapters.in.security.dto.UserCredentialsDto;
import com.example.taskmanagment.application.domain.dto.UserDto;
import com.example.taskmanagment.application.domain.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserCredentialsDto dto);

    UserCredentialsDto toUserCredentialsDto(User user);

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);





}
