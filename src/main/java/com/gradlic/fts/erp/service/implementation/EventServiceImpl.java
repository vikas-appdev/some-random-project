package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.UserEvent;
import com.gradlic.fts.erp.enumeration.EventType;
import com.gradlic.fts.erp.repository.EventRepository;
import com.gradlic.fts.erp.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return eventRepository.getEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        eventRepository.addUserEvent(email, eventType, device, ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
