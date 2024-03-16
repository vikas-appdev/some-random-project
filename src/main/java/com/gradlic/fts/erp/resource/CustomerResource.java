package com.gradlic.fts.erp.resource;

import com.gradlic.fts.erp.domain.*;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.service.CustomerService;
import com.gradlic.fts.erp.service.FirmService;
import com.gradlic.fts.erp.service.UserService;
import jakarta.validation.Valid;
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
@RequestMapping(path = "/customer")
public class CustomerResource {

    private final CustomerService customerService;
    private final UserService userService;

    private final FirmService firmService;


    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size){
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()),
                                "page", customerService.getCustomers(page.orElse(0), size.orElse(10)),
                                "stats", customerService.getStats()))
                        .message("Customers retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer, @PathVariable Long id){
        Firm firm = firmService.getFirmById(id);
        customer.setFirm(firm);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "customer", customerService.createCustomer(customer)))
                        .message("Customers retrieved")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO user, @PathVariable Long id){
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "customer", customerService.getCustomer(id)))
                        .message("Customer retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<String> name, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size){
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "page", customerService.searchCustomer(name.orElse(""), page.orElse(0), size.orElse(10))))
                        .message("Customers retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer){
        Customer customer1 = customerService.updateCustomer(customer);
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "customer", customerService.getCustomer(customer1.getId())))
                        .message("Customer updated")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PostMapping("/invoice/create")
    public ResponseEntity<HttpResponse> createInvoice(@AuthenticationPrincipal UserDTO user, @RequestBody Invoice invoice){
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "invoice", customerService.createInvoice(invoice)))
                        .message("Invoice retrieved")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @GetMapping("/invoice/new")
    public ResponseEntity<HttpResponse> newInvoice(@AuthenticationPrincipal UserDTO user){
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "customers", customerService.getCustomers()))
                        .message("Customers retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/invoice/list")
    public ResponseEntity<HttpResponse> getInvoices(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size){
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "page", customerService.getInvoices(page.orElse(0), size.orElse(10))))
                        .message("Invoices retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/invoice/get/{id}")
    public ResponseEntity<HttpResponse> getInvoice(@AuthenticationPrincipal UserDTO user, @PathVariable Long id){
        Invoice invoice = customerService.getInvoice(id);
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()),
                                "invoice", invoice,
                                "customer", invoice.getCustomer()))
                        .message("Invoice retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PostMapping("/invoice/addtocustomer/{id}")
    public ResponseEntity<HttpResponse> addInvoicToCustomer(@AuthenticationPrincipal UserDTO user, @PathVariable Long id, @RequestBody Invoice invoice){
        customerService.addInvoiceToCustomer(id, invoice);
        return ResponseEntity.ok(
                HttpResponse.builder().timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserByUserId(user.getId()), "customers", customerService.getCustomers()))
                        .message(String.format("Invoice added to customer with id: %s", id))
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

}
