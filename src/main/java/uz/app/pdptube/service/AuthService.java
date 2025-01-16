package uz.app.pdptube.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.app.pdptube.dto.SignInDTO;
import uz.app.pdptube.dto.UserDTO;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public ResponseMessage signUp(UserDTO userDTO) {
        boolean existsByEmail = userRepository.existsByEmail(userDTO.getEmail());
        if (existsByEmail) {
            return new ResponseMessage(false, "email already exists", userDTO.getEmail());
        }

        User user = User.builder()
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .age(userDTO.getAge())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .build();
        userRepository.save(user);
        // JWT bilan userDetails filter qoshilganda , contextga user qoshish esdan chiqmasin
         addAuthenticatedUserToContext(user);
        return new ResponseMessage(true, "User registered", user);
    }

    private void addAuthenticatedUserToContext(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList() // Or user.getAuthorities() if roles/permissions exist
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    public ResponseMessage signIn(SignInDTO emailAndPassword) {
        User user = userRepository.findByEmail(emailAndPassword.getEmail()).orElseThrow();
        if (user == null) {
            return new ResponseMessage(false, "email not found", emailAndPassword.getEmail());
        }
        if (!user.getPassword().equals(emailAndPassword.getPassword())) {
            return new ResponseMessage(false, "password incorrect", emailAndPassword);
        }
        // JWT bilan userDetails filter qoshilganda , contextga user qoshish esdan chiqmasin
        addAuthenticatedUserToContext(user);
        return new ResponseMessage(true, "User logged in", user);

    }

}
