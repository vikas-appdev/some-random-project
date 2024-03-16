package com.gradlic.fts.erp.resource;

import com.gradlic.fts.erp.domain.*;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.service.CustomerService;
import com.gradlic.fts.erp.service.UserService;
import com.gradlic.fts.erp.service.implementation.LoanAccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import static java.time.LocalDateTime.now;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/loan")
public class LoanAccountResource {

    private final CustomerService customerService;
    private final UserService userService;

    private final LoanAccountServiceImpl loanAccountService;


    @PostMapping("/create/{customerId}")
    public ResponseEntity<HttpResponse> createLoanAccount(@AuthenticationPrincipal UserDTO user, @RequestBody LoanAccount loanAccount, @PathVariable Long customerId){
        Customer customer = customerService.getCustomer(customerId);
        loanAccount.setCustomer(customer);
        LoanAccount loan = loanAccountService.createNewLoan(loanAccount);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user, "customer", loan.getCustomer(), "loanAccount", loan))
                        .message("Loan retrieved")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getLoanAccountById(@AuthenticationPrincipal UserDTO user, @PathVariable Long id){
        LoanAccount loanAccount = loanAccountService.getLoanAccountById(id);
        Customer customer = loanAccount.getCustomer();
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user, "customer", customer, "loanAccount", loanAccount))
                        .message("Customer retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PostMapping("/create/payment/{loanEmiId}")
    public ResponseEntity<HttpResponse> createEmiPayment(@AuthenticationPrincipal UserDTO user, @RequestBody LoanEmiPayments loanEmiPayments, @PathVariable Long loanEmiId){
        // loanAccountService.createNewPayments(user, loanEmiPayments, loanEmiId);
        LoanEmiPayments newPayments = loanAccountService.createNewPayments(user, loanEmiPayments, loanEmiId);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "loanAccount", newPayments.getLoanAccount(), "customer", newPayments.getLoanAccount().getCustomer()))
                        .message("Payment done")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @PostMapping("/close/{loanAccountId}/{date}")
    public ResponseEntity<HttpResponse> closeLoanAccount(@AuthenticationPrincipal UserDTO user, @PathVariable Long loanAccountId, @PathVariable LocalDate date){
        // loanAccountService.createNewPayments(user, loanEmiPayments, loanEmiId);
        LoanAccount loanAccount = loanAccountService.closeLoanAccount(user, loanAccountId, date);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", user, "customer", loanAccount.getCustomer(), "loanAccount", loanAccount))
                        .message("Payment done")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }
}
