package com.gradlic.fts.erp.resource;

import com.gradlic.fts.erp.domain.Customer;
import com.gradlic.fts.erp.domain.Firm;
import com.gradlic.fts.erp.domain.HttpResponse;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.service.FirmService;
import com.gradlic.fts.erp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/firm")
public class FirmResource {

    private final FirmService firmService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size){
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("firms", firmService.getFirms(), "user", userService.getUserByUserId(user.getId())))
                        .message("Firms retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createFirm(@AuthenticationPrincipal UserDTO user, @RequestBody Firm firm){
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "firm", firmService.createFirm(firm)))
                        .message("Firm created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }


}
