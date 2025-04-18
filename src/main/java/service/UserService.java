package service;

import dto.UserReceiveDto;
import dto.UserReturnDto;
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

    public UserReturnDto createUserGuest(UserReceiveDto userReceiveDto) {
        User user = mapToUser(userReceiveDto);
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);
        return mapToUserReturnDto(savedUser);
    }

    public UserReturnDto createUserHost(UserReceiveDto userReceiveDto) {
        User user = mapToUser(userReceiveDto);
        user.setRole(Role.HOST);
        User savedUser = userRepository.save(user);
        return mapToUserReturnDto(savedUser);
    }

    private UserReturnDto mapToUserReturnDto(User user) {
        return UserReturnDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    private User mapToUser(UserReceiveDto userReceiveDto) {
        return User.builder()
                .email(userReceiveDto.getEmail())
                .fullName(userReceiveDto.getFullName())
                .phoneNumber(userReceiveDto.getPhoneNumber())
                .password(userReceiveDto.getPassword())
                .build();
    }
}
