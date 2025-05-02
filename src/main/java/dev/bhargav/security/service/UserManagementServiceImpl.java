package dev.bhargav.security.service;

import dev.bhargav.security.constant.AccountConstants;
import dev.bhargav.security.deserializer.UserDetailsDeserializer;
import dev.bhargav.security.entity.User;
import dev.bhargav.security.exception.ResourceNotFoundException;
import dev.bhargav.security.exception.UserAlreadyExistAuthenticationException;
import dev.bhargav.security.model.SignUpRequest;
import dev.bhargav.security.model.UserDto;
import dev.bhargav.security.repository.UserRepository;
import dev.bhargav.security.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsDeserializer userDetailsDeserializer;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        return this.userDetailsDeserializer.deserializeAccount(this.userRepository.findAll());
    }

    @Override
    @Transactional(value = "transactionManager")
    public UserDto registerNewUser(final SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException {
        if (this.userRepository.existsByUsernameIgnoreCase(signUpRequest.getUsername())) {
            throw new UserAlreadyExistAuthenticationException("Username '" + signUpRequest.getUsername() + "' already exists");
        }
        User user = buildUser(signUpRequest);
        user = this.userRepository.save(user);
        this.userRepository.flush();
        return UserDto.builder().username(user.getUsername()).password(user.getPassword()).build();
    }

    @Override
    @Transactional(value = "transactionManager")
    public void deleteUser(String username) throws ResourceNotFoundException {
        if (!this.userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceNotFoundException(AccountConstants.USER_NOT_FOUND.getMessage());
        }
        this.userRepository.deleteByUsername(username);
        this.userRepository.flush();
    }

    private User buildUser(final SignUpRequest signUpRequest) {
        return User.builder()
                .username(signUpRequest.getUsername())
                .password(this.passwordEncoder.encode(signUpRequest.getPassword()))
                .build();
    }
}
