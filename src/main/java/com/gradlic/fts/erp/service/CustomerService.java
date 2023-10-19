package com.gradlic.fts.erp.service;

import com.gradlic.fts.erp.domain.Customer;
import com.gradlic.fts.erp.domain.Invoice;
import org.springframework.data.domain.Page;


public interface CustomerService {
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomer(String name, int page, int size);

    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long id, Invoice invoice);

    Invoice getInvoice(Long id);
}
