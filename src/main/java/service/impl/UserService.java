package service.impl;

import dto.UserRequestDto;
import dto.UserResponseDto;
import entity.Role;
import entity.User;
import org.springframework.stereotype.Service;
import repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto createUserGuest(UserRequestDto userRequestDto) {
        User user = mapToUser(userRequestDto);
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);
        return mapToUserReturnDto(savedUser);
    }

    public UserResponseDto createUserHost(UserRequestDto userRequestDto) {
        User user = mapToUser(userRequestDto);
        user.setRole(Role.HOST);
        User savedUser = userRepository.save(user);
        return mapToUserReturnDto(savedUser);
    }

    private UserResponseDto mapToUserReturnDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    private User mapToUser(UserRequestDto userRequestDto) {
        return User.builder()
                .email(userRequestDto.getEmail())
                .fullName(userRequestDto.getFullName())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .password(userRequestDto.getPassword())
                .build();
    }
}
