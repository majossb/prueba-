package com.lta.auditoriumsystem.Services;


import com.lta.auditoriumsystem.dtos.LoginResponse;
import com.lta.auditoriumsystem.dtos.LoginRequest;
import com.lta.auditoriumsystem.dtos.RegisterRequest;
import com.lta.auditoriumsystem.Exceptions.ResourceNotFoundException;

public interface AuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
    void registerUser(RegisterRequest registerRequest) throws ResourceNotFoundException;
}
