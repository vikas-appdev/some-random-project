package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.Customer;
import com.gradlic.fts.erp.domain.Invoice;
import com.gradlic.fts.erp.domain.Stats;
import com.gradlic.fts.erp.repository.CustomerRepository;
import com.gradlic.fts.erp.repository.InvoiceRepository;
import com.gradlic.fts.erp.rowmapper.StatsRowMapper;
import com.gradlic.fts.erp.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.gradlic.fts.erp.query.CustomerQuery.STATS_QUERY;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final InvoiceRepository invoiceRepository;

    private final NamedParameterJdbcTemplate jdbc;


    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Page<Customer> getCustomers(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).get();
    }

    @Override
    public Page<Customer> searchCustomer(String name, int page, int size) {
        return customerRepository.findByNameContaining(name, PageRequest.of(page, size));
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getInvoices(int page, int size) {
        return invoiceRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public void addInvoiceToCustomer(Long id, Invoice i) {
        i.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        Customer customer = customerRepository.findById(id).get();
        i.setCustomer(customer);
        invoiceRepository.save(i);
    }

    @Override
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).get();
    }

    @Override
    public Stats getStats() {
        return jdbc.queryForObject(STATS_QUERY, Map.of(), new StatsRowMapper());
    }
}
