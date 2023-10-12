package com.gradlic.fts.erp.resource;

import com.gradlic.fts.erp.domain.HttpResponse;
import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.domain.UserPrincipal;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.exception.ApiException;
import com.gradlic.fts.erp.form.LoginForm;
import com.gradlic.fts.erp.form.UpdateForm;
import com.gradlic.fts.erp.form.UpdatePasswordForm;
import com.gradlic.fts.erp.provider.TokenProvider;
import com.gradlic.fts.erp.service.RoleService;
import com.gradlic.fts.erp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gradlic.fts.erp.dtomapper.UserDTOMapper.toUser;
import static com.gradlic.fts.erp.utils.ExceptionsUtils.processError;
import static com.gradlic.fts.erp.utils.UserUtils.getAuthenticatedUser;
import static com.gradlic.fts.erp.utils.UserUtils.getLoggedInUser;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    private final RoleService roleService;

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private static final String TOKEN_PREFIX = "Bearer ";

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));

        //authenticationManager.authenticate(unauthenticated(loginForm.getEmail(), loginForm.getPassword()));

        // UserDTO user = userService.getUserByUserEmail(loginForm.getEmail());

        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO user = getLoggedInUser(authentication);
        return user.isUsingMFA() ? sendVerificationCode(user) : sendResponse(user);
    }




    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .message("User created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request){
        /*return ResponseEntity.badRequest().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason("404, Resource not found")
                        .status(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .build()
        );*/

        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .reason("There is no mapping for a "+request.getMethod()+" request for this path on this server")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build(), NOT_FOUND
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO user = userService.getUserByUserEmail(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user))
                        .message("Profile retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) throws InterruptedException {
        // Frontend loading test : Thread sleep 3 sec
        TimeUnit.SECONDS.sleep(3);
        UserDTO updatedUser = userService.updateUserDetails(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", updatedUser))
                        .message("User Updated")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email){
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .message("Email sent. Please check your email to reset your password")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form){
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .message("Password updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable String key){
        UserDTO userDTO = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .message("Please enter a new password")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@PathVariable String key, @PathVariable String password, @PathVariable String confirmPassword){
        userService.renewPassword(key, password, confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .message("Password reset successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO user = userService.verifyCode(email, code);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)), "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login successful")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key){
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .message(userService.verifyAccountKey(key).isActive() ? "Account already verified": "Account verified")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request){
        if(isHeaderAndTokenValid(request)){
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUserByUserId(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder().timeStamp(now().toString())
                            .data(Map.of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                    "refresh_token", token))
                            .message("Token refreshed")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder().timeStamp(now().toString())
                            .reason("Refresh token missing or invalid")
                            .developerMessage("Refresh token missing or invalid")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) !=null && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)), "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login successful")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByUserEmail(user.getEmail())), roleService.getRoleByUserId(user.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
        System.out.println(user.getMobileNumber());
        userService.sendVerificationCode(user);


        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user))
                        .message("Verification code sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    private Authentication authenticate(String email, String password){
        try{
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            return authentication;
        }catch (Exception exception){
            // processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }

    }

}
