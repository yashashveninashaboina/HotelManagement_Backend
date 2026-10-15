package com.hotelmanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotelmanagement.model.PaymentMethod;
import com.hotelmanagement.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private String customerName;
    private String roomNumber;
    private Double amount;
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
}
