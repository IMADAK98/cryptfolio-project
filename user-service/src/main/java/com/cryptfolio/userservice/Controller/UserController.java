package com.cryptfolio.userservice.Controller;

import com.cryptfolio.userservice.Entity.*;
import com.cryptfolio.userservice.Service.EmailService;
import com.cryptfolio.userservice.Service.UserService;
import com.cryptfolio.userservice.Repo.PasswordTokenRepo;
import com.cryptfolio.userservice.Repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final EmailService emailService;

    private final PasswordTokenRepo passwordTokenRepo;

    private final UserRepo userRepo;


    @GetMapping("/test")
    public String testUserApi(){
        return "Api is working";
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return users.isEmpty() ?
                //TODO modify the response in instances where validation is needed
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found"):
                ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById (@PathVariable Long id){
        User user = userService.getUserById(id);
        return user == null ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found") :
                ResponseEntity.ok(user);
    }


    @PostMapping("/user")
    public ResponseEntity<User> createUser (@RequestBody User user){
        return ResponseEntity.ok(userService.createUser(user));
    }







    @GetMapping("/tokens")
    public  ResponseEntity<List<PasswordResetToken>> getAll(){
        return   ResponseEntity.ok().body(passwordTokenRepo.findAll());
    }






    @PostMapping("/reset-password-email")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetRequest request) throws MessagingException {
        userService.sendResetPasswordEmail(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password Reset Email has been sent");
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/token/{id}")
    public ResponseEntity<?> deleteUserToken(@PathVariable Long id){
        passwordTokenRepo.deleteById(id);
        return  ResponseEntity.ok("Token is deleted successfully");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetNewPassword (@RequestBody @Valid ResetPasswordRequest request ){
        userService.resetNewPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password Has been reset");
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/reset-password-token-check")
    public ResponseEntity<?> resetPasswordTokenCheck(@RequestBody  @Valid TokenValidationRequest request){
        if (request.getToken() == null || request.getToken().trim().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token is empty");
        }
        return  ResponseEntity.ok().body(userService.isTokenValid(request.getToken()));
    }


}
