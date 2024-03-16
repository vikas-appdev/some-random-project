package com.gradlic.fts.erp.service;

import com.gradlic.fts.erp.domain.Firm;

import java.util.Collection;

public interface FirmService {
    Firm createFirm(Firm firm);
    Firm updateFirm(Firm firm);
    Firm getFirmById(Long id);
    Collection<Firm> getFirms();
}
