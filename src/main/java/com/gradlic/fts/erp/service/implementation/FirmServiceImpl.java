package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.Firm;
import com.gradlic.fts.erp.repository.FirmRepository;
import com.gradlic.fts.erp.service.FirmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FirmServiceImpl implements FirmService {

    private final FirmRepository firmRepository;

    @Override
    public Firm createFirm(Firm firm) {
        firm.setCreatedAt(LocalDateTime.now());
        return firmRepository.save(firm);
    }

    @Override
    public Firm updateFirm(Firm firm) {
        return firmRepository.save(firm);
    }

    @Override
    public Firm getFirmById(Long id) {
        return firmRepository.findById(id).get();
    }

    @Override
    public Collection<Firm> getFirms() {
        return firmRepository.findAll();
    }
}
