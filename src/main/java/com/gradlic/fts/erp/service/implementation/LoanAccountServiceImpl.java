package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.LoanAccount;
import com.gradlic.fts.erp.domain.LoanEmiPayments;
import com.gradlic.fts.erp.domain.LoanEmiSchedule;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.repository.CustomerRepository;
import com.gradlic.fts.erp.repository.LoanAccountRepository;
import com.gradlic.fts.erp.repository.LoanEmiPaymentsRepository;
import com.gradlic.fts.erp.repository.LoanEmiScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoanAccountServiceImpl {


    private final CustomerRepository customerRepository;
    private final LoanAccountRepository loanAccountRepository;

    private final LoanEmiScheduleRepository loanEmiScheduleRepository;

    private final LoanEmiPaymentsRepository loanEmiPaymentsRepository;


    public LoanAccount createNewLoan(LoanAccount loanAccount){
        loanAccount.setLoanType("DAILY_WAGE");
        loanAccount.setAccountStatus("RUNNING");
        loanAccount.setEmiType("DAILY");
        loanAccount.setNumberOfEmi(90);
        loanAccount.setCreatedAt(LocalDateTime.now());
        //loanAccount.setAccountOpeningDate(LocalDate.now());
        loanAccount.setProcessingCharge(getProcessingCharge(loanAccount.getTotalAmount()));

        LoanAccount account = loanAccountRepository.save(loanAccount);

        int remainingLoan = account.getTotalAmount() - account.getProcessingCharge();

        int emiAmount = remainingLoan / loanAccount.getNumberOfEmi();
        LocalDate accountOpeningDate = account.getAccountOpeningDate();

        for (int i=0; i<loanAccount.getNumberOfEmi(); i++){
            LoanEmiSchedule loanEmiSchedule = new LoanEmiSchedule();
            loanEmiSchedule.setLoanAccount(account);
            loanEmiSchedule.setStatus("UNPAID");
            loanEmiSchedule.setAmount(emiAmount);
            loanEmiSchedule.setPaymentDate(accountOpeningDate.plusDays(i+1));
            loanEmiScheduleRepository.save(loanEmiSchedule);
        }
        return account;
    }

    public LoanAccount getLoanAccountById(Long id){
        return loanAccountRepository.findById(id).get();
    }

    public LoanEmiSchedule getLoanEmiScheduleById(Long id){
        return loanEmiScheduleRepository.findById(id).get();
    }

    public LoanEmiPayments createNewPayments(UserDTO user, LoanEmiPayments loanEmiPayments, Long loanEmiId){
        LoanEmiSchedule loanEmiSchedule = loanEmiScheduleRepository.findById(loanEmiId).get();
        loanEmiPayments.setLoanEmiSchedule(loanEmiSchedule);
        loanEmiPayments.setLoanAccount(loanEmiSchedule.getLoanAccount());
        loanEmiPayments.setCreatedAt(LocalDateTime.now());
        loanEmiPayments.setAmount(loanEmiSchedule.getAmount());
        loanEmiPayments.setReceivedBy(user.getFirstName()+ " "+user.getLastName());
        loanEmiPayments.setReceivedByUserId(user.getId());

        if (loanEmiSchedule.getPaymentDate().isBefore(loanEmiPayments.getPaymentDate())){
            loanEmiPayments.setRemarks("LATE_PAYMENT");
        } else if (loanEmiSchedule.getPaymentDate().isAfter(loanEmiPayments.getPaymentDate())) {
            loanEmiPayments.setRemarks("PRE_PAID");
        }else {
            loanEmiPayments.setRemarks("ON_TIME_PAYMENT");
        }

        LoanEmiPayments savedPayments = loanEmiPaymentsRepository.save(loanEmiPayments);

        loanEmiSchedule.setStatus("PAID");
        loanEmiScheduleRepository.save(loanEmiSchedule);

        return savedPayments;
    }

    public LoanAccount closeLoanAccount(UserDTO user, Long lonaId, LocalDate date){
        AtomicInteger count = new AtomicInteger();
        Collection<LoanEmiSchedule> byLoanAccountId = loanEmiScheduleRepository.findByLoanAccountIdAndStatus(lonaId, "UNPAID");
        byLoanAccountId.forEach(loanEmiSchedule -> {
            LoanEmiPayments loanEmiPayments = new LoanEmiPayments();
            loanEmiPayments.setPaymentDate(date);
            createNewPayments(user, loanEmiPayments, loanEmiSchedule.getId());
            count.getAndIncrement();
        });

        LoanAccount loanAccount = loanAccountRepository.findById(lonaId).get();

        if (count.get() >= 1){
            loanAccount.setAccountStatus("PRE_CLOSED");
        }else {
            loanAccount.setAccountStatus("CLOSED");
        }

        return loanAccountRepository.save(loanAccount);

    }

    private int getProcessingCharge(int totalAmount) {
        return (totalAmount * 10) / 100;
    }
}
