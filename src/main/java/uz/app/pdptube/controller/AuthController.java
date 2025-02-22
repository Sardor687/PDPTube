package uz.app.pdptube.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.app.pdptube.dto.SignInDTO;
import uz.app.pdptube.dto.UserDTO;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.AuthService;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody  UserDTO userDTO) {
        try {
            ResponseMessage responseMessage = authService.signUp(userDTO);
            boolean success = responseMessage.success();
            if (success){
                return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignInDTO emailAndPassword) {
        ResponseMessage responseMessage = authService.signIn(emailAndPassword);
        try {
            boolean success = responseMessage.success();
            if (success){
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
            }
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage() + "our message --> " + responseMessage.message());
        }
    }

    Random random=new Random();

    public String key(){
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < 20; i++) {
            sb.append(((char)random.nextInt(97,122)));
        }
        return sb.toString();
    }
}