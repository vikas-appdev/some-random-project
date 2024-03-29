package com.gradlic.fts.erp.resource;

import com.gradlic.fts.erp.domain.HttpResponse;
import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.domain.UserEvent;
import com.gradlic.fts.erp.domain.UserPrincipal;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.event.NewUserEvent;
import com.gradlic.fts.erp.exception.ApiException;
import com.gradlic.fts.erp.form.LoginForm;
import com.gradlic.fts.erp.form.SettingsForm;
import com.gradlic.fts.erp.form.UpdateForm;
import com.gradlic.fts.erp.form.UpdatePasswordForm;
import com.gradlic.fts.erp.provider.TokenProvider;
import com.gradlic.fts.erp.service.EventService;
import com.gradlic.fts.erp.service.RoleService;
import com.gradlic.fts.erp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gradlic.fts.erp.dtomapper.UserDTOMapper.toUser;
import static com.gradlic.fts.erp.enumeration.EventType.*;
import static com.gradlic.fts.erp.utils.ExceptionsUtils.processError;
import static com.gradlic.fts.erp.utils.UserUtils.getAuthenticatedUser;
import static com.gradlic.fts.erp.utils.UserUtils.getLoggedInUser;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    private final RoleService roleService;
    private final EventService eventService;

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ApplicationEventPublisher publisher;

    private static final String TOKEN_PREFIX = "Bearer ";

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));

        //authenticationManager.authenticate(unauthenticated(loginForm.getEmail(), loginForm.getPassword()));

        // UserDTO user = userService.getUserByUserEmail(loginForm.getEmail());

        UserDTO user = authenticate(loginForm.getEmail(), loginForm.getPassword());
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
                        .data(Map.of("user", user, "events", eventService.getEventsByUserId(user.getId()), "roles", roleService.getRoles()))
                        .message("Profile retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) throws InterruptedException {
        // Frontend loading test : Thread sleep 3 sec
        // TimeUnit.SECONDS.sleep(3);
        UserDTO updatedUser = userService.updateUserDetails(user);
        //publisher.publishEvent(new NewUserEvent(updatedUser.getEmail(), PROFILE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", updatedUser, "events", eventService.getEventsByUserId(user.getId()), "roles", roleService.getRoles()))
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
        //publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), PASSWORD_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .message("Password updated successfully")
                        .data(of("user", userDTO, "events", eventService.getEventsByUserId(userDTO.getId()), "roles", roleService.getRoles()))
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole(Authentication authentication, @PathVariable String roleName){
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateUserRole(userDTO.getId(), roleName);
        //publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), ROLE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(of("user", userService.getUserByUserId(userDTO.getId()), "events", eventService.getEventsByUserId(userDTO.getId()), "roles", roleService.getRoles()))
                        .message("Role updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(Authentication authentication, @RequestBody @Valid SettingsForm form){
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateAccountSettings(userDTO.getId(), form.getActive(), form.getNotLocked());
        //publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), ACCOUNT_SETTINGS_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(of("user", userService.getUserByUserId(userDTO.getId()), "events", eventService.getEventsByUserId(userDTO.getId()), "roles", roleService.getRoles()))
                        .message("Account settings updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/togglemfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication){
        UserDTO userDTO = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), MFA_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(of("user", userDTO, "events", eventService.getEventsByUserId(userDTO.getId()), "roles", roleService.getRoles()))
                        .message("Multi factor authentication updated")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PatchMapping("/update/image")
    public ResponseEntity<HttpResponse> updateProfileImage(Authentication authentication, @RequestParam("image") MultipartFile image){
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updateImage(user, image);
        //publisher.publishEvent(new NewUserEvent(user.getEmail(), PROFILE_PICTURE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(of("user", userService.getUserByUserId(user.getId()), "events", eventService.getEventsByUserId(user.getId()), "roles", roleService.getRoles()))
                        .message("Profile image updated")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    // produces will let browser know about content type
    @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") +"/Downloads/images/" +fileName));
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
        publisher.publishEvent(new NewUserEvent(user.getEmail(), LOGIN_ATTEMPT_SUCCESS));
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
        userService.sendVerificationCode(user);


        return ResponseEntity.ok().body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user))
                        .message("Verification code sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    private UserDTO authenticate(String email, String password){
        try{
//            if (null != userService.getUserByUserEmail(email)){
//                publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT));
//            }
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            UserDTO userDTO = getLoggedInUser(authentication);
//            if (!userDTO.isUsingMFA()){
//                publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_SUCCESS));
//            }
            return userDTO;
        }catch (Exception exception){
            //publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_FAILURE));
            processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }

    }

}
