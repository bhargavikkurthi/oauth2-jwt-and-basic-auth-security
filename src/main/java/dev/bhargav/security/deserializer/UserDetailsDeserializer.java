package dev.bhargav.security.deserializer;

import dev.bhargav.security.entity.User;
import dev.bhargav.security.model.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDetailsDeserializer {

    public UserDto deserializeAccount(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .build();
    }

    public List<UserDto> deserializeAccount(List<User> user) {
        List<UserDto> users = new ArrayList<>();
        user.forEach(u -> users.add(UserDto.builder()
                .username(u.getUsername())
                .build()));
        return users;
    }
}
