package dev.bhargav.security.service;

import dev.bhargav.security.exception.ResourceNotFoundException;
import dev.bhargav.security.exception.UserAlreadyExistAuthenticationException;
import dev.bhargav.security.model.SignUpRequest;
import dev.bhargav.security.model.UserDto;

import java.util.List;

public interface UserManagementService {

    List<UserDto> getAllUsers();

    UserDto registerNewUser(SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException;

    void deleteUser(String username) throws ResourceNotFoundException;
}
