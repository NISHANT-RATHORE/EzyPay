package org.example.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class InitiateTrxnRequest {
    @NotBlank
    String receiverPhoneNo;

    @Positive
    Double amount;

    String purpose;
}
