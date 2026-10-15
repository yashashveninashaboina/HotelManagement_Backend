package com.hotelmanagement.dto;

import com.hotelmanagement.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role; // Optional, defaults to CUSTOMER
    private String phone;
    private String address;
}
