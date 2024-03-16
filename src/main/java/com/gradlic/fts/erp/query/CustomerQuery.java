package com.gradlic.fts.erp.query;

public class CustomerQuery {
    public static final String STATS_QUERY = "SELECT c.total_customers, i.total_invoices, lep.todays_collection, loan.running_loan, les.total_due, inv.total_billed FROM (SELECT COUNT(*) total_customers FROM customer) c, (SELECT COUNT(*) total_invoices FROM invoice) i, (SELECT ROUND(SUM(total)) total_billed FROM invoice) inv, (SELECT ROUND(SUM(amount)) todays_collection FROM loan_emi_payments WHERE payment_date= current_date()) lep, (SELECT COUNT(*) running_loan FROM loan_account WHERE account_status= 'RUNNING') loan, (SELECT ROUND(SUM(amount)) total_due FROM loan_emi_schedule WHERE status= 'UNPAID') les;";
}
