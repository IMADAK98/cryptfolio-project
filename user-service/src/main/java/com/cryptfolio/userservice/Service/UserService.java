package com.cryptfolio.userservice.Service;

import com.cryptfolio.userservice.Entity.PasswordResetToken;
import com.cryptfolio.userservice.Entity.ResetPasswordRequest;
import com.cryptfolio.userservice.Entity.User;
import com.cryptfolio.userservice.Exceptions.UserNotFoundException;
import com.cryptfolio.userservice.Repo.PasswordTokenRepo;
import com.cryptfolio.userservice.Repo.UserRepo;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordTokenRepo passwordTokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    public List<User> getAllUsers (){
        return userRepo.findAll();
    }

    public User getUserById(Long id){
        return userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found with this id"));
    }

    public User createUser(User user){
        return userRepo.save(user);
    }

    public User deleteUser(Long id) {
        return userRepo.findById(id)
                .map(user -> {
                    userRepo.deleteById(id);
                    return user;
                })
                .orElseThrow(() -> new RuntimeException("No User with this id"));
    }



    public User findUserByEmail(String email) throws  UsernameNotFoundException{
       return  userRepo.findByEmail(email)
               .orElseThrow(()->
                       new UserNotFoundException("User not found with this email :" + " " + email));
    }




    public void sendResetPasswordEmail(String userEmail) throws MessagingException {
        if (userEmail== null || userEmail.trim().isEmpty()){
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        //TODO need to implement a feature to delete the token only after a certain period,to prevent email spam
        User user = findUserByEmail(userEmail);
        String token = generateResetToken(user);

        //Todo use configuration values ,,, and for the token expiration date as well
        String baseUrl = "https://www.cryptfolio.pro";
        MimeMessage email  = emailService.constructResetTokenEmail(baseUrl,token,user);
        emailService.sendEmail(email);
    }



    private String generateResetToken(User user) {
        // Check if user is not null
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Check if the user already has a valid token
        PasswordResetToken existingToken = user.getToken();
        if (existingToken != null) {
            // If the token is valid, return it
            if (isTokenValid(existingToken.getToken())) {
                return existingToken.getToken();
            } else {
                // If the token is invalid, delete it
                passwordTokenRepo.delete(existingToken);
                user.setToken(null);

                //using this to make sure that the state of the database is synced , otherwise hibernate is not performing the delete operation,
                //and it's causing an exception to be thrown
                passwordTokenRepo.flush();
            }
        }

        // Create a new token if there's no valid token
        return buildNewToken(user).getToken();
    }


    public PasswordResetToken buildNewToken (User user ){

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiryDate(new Date(System.currentTimeMillis() + 3600000))  // 1 hour expiry
                .build();

       return passwordTokenRepo.save(resetToken);
    }





    public void resetNewPassword(ResetPasswordRequest request){
        User existingUser =   findUserByEmail(request.getEmail());

        PasswordResetToken resetToken = passwordTokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (!resetToken.getUser().equals(existingUser)) {
            throw new RuntimeException("Token does not match user");
        }


        if (resetToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }


        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(existingUser);
        passwordTokenRepo.delete(resetToken);


    }



    public boolean isTokenValid(String token) {
        return passwordTokenRepo.findByToken(token)
                .map(resetToken -> !resetToken.isExpired())
                .orElse(false);
    }

}
