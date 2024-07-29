package org.example.userservice.mapper;

import lombok.experimental.UtilityClass;
import org.example.userservice.Model.User;
import org.example.userservice.dto.AddUserRequest;
import org.example.userservice.enums.UserStatus;

@UtilityClass
public class UserMapper {
    public User MapToUser(AddUserRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNo(request.getPhoneNo())
                .userIdentificationType(request.getUserIdentificationType())
                .userIdentificationTypeValue(request.getUserIdentificationTypeValue())
                .userStatus(UserStatus.ACTIVE)
                .build();
    }
}
