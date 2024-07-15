package com.example.passengerservice.service.Impl;

import com.example.passengerservice.dto.AuthenticationResponse;
import com.example.passengerservice.dto.LoginDto;
import com.example.passengerservice.dto.PassengerDto;
import com.example.passengerservice.entity.Passenger;
import com.example.passengerservice.repository.PassengerRepo;
import com.example.passengerservice.service.JwtService;
import com.example.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerImpl implements PassengerService {
    private final PassengerRepo passengerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    @Override
    public AuthenticationResponse registerPassenger(PassengerDto passengerDto) {
        Passenger passenger=new Passenger();
        passenger.setFirstName(passengerDto.getFirstName());
        passenger.setLastName(passengerDto.getLastName());
        passenger.setEmail(passengerDto.getEmail());
        passenger.setAddress(passengerDto.getAddress());
        passenger.setContactNum(passengerDto.getContactNum());
        passenger.setPassword(passwordEncoder.encode(passengerDto.getPassword()));
        Passenger savedPassenger = passengerRepo.save(passenger);
        String toke = jwtService.generateToke(savedPassenger);
        return new AuthenticationResponse(toke);

    }

    @Override
    public AuthenticationResponse loginPassenger(LoginDto loginDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        Passenger passenger = passengerRepo.findByEmail(loginDto.getEmail()).orElseThrow();
        String toke = jwtService.generateToke(passenger);

        return new AuthenticationResponse(toke);
    }

    @Override
    public PassengerDto getPassenger(Integer id) {
        Optional<Passenger> passenger = passengerRepo.findById(id);
        return modelMapper.map(passenger, PassengerDto.class);
    }

    @Override
    public ResponseEntity<List> getNotification() {
        ResponseEntity<List> responseEntity = restTemplate.exchange("http://localhost:9001/all-notifications",
                HttpMethod.GET, null, List.class);
        return responseEntity;
    }
}
