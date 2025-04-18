package dto;

import lombok.Data;

@Data
public class UserReceiveDto {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
}
