package com.example.passengerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthenticationResponse {
   private String token;
}
