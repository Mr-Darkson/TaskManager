package com.example.taskmanagment.application.utils.mappers;

import com.example.taskmanagment.adapters.in.security.dto.UserCredentialsDto;
import com.example.taskmanagment.application.domain.dto.UserDto;
import com.example.taskmanagment.application.domain.models.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-13T15:19:21+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCredentialsDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( dto.getEmail() );
        user.password( dto.getPassword() );

        return user.build();
    }

    @Override
    public UserCredentialsDto toUserCredentialsDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserCredentialsDto userCredentialsDto = new UserCredentialsDto();

        userCredentialsDto.setEmail( user.getEmail() );
        userCredentialsDto.setPassword( user.getPassword() );

        return userCredentialsDto;
    }

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setUsername( user.getUsername() );
        userDto.setEmail( user.getEmail() );

        return userDto;
    }

    @Override
    public User toUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDto.getId() );
        user.username( userDto.getUsername() );
        user.email( userDto.getEmail() );

        return user.build();
    }
}
