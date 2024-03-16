package com.gradlic.fts.erp.rowmapper;

import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.domain.Stats;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRowMapper implements RowMapper<Stats> {

    @Override
    public Stats mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Stats.builder()
                .totalCustomers(rs.getInt("total_customers"))
                .totalInvoices(rs.getInt("total_invoices"))
                .totalBilled(rs.getDouble("total_billed"))
                .todaysCollection(rs.getInt("todays_collection"))
                .totalRunningLoanAccount(rs.getInt("running_loan"))
                .totalDue(rs.getInt("total_due"))
                .build();
    }

}
