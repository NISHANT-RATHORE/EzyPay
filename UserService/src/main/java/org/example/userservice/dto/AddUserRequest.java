package org.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.userservice.enums.UserIdentificationType;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {
    @NotNull
    String name;

    @NotNull
    String email;

    @NotBlank
    String phoneNo;

    @NotNull
    String password;

    @NotNull
    UserIdentificationType userIdentificationType;

    @NotNull
    String userIdentificationTypeValue;
}
