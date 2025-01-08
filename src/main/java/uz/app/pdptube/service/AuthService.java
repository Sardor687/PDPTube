package uz.app.pdptube.service;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.pdptube.dto.UserDTO;
import uz.app.pdptube.entity.User;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.UserRepository;

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
        return new ResponseMessage(true, "User registered", user);
    }

    public ResponseMessage signIn(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user == null) {
            return new ResponseMessage(false, "email not found", userDTO.getEmail());
        }
        if (!user.getPassword().equals(userDTO.getPassword())) {
            return new ResponseMessage(false, "password incorrect", userDTO);
        }
        return new ResponseMessage(true, "User logged in", user);

    }

}
