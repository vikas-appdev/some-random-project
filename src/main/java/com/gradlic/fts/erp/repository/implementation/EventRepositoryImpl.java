package com.gradlic.fts.erp.repository.implementation;

import com.gradlic.fts.erp.domain.UserEvent;
import com.gradlic.fts.erp.enumeration.EventType;
import com.gradlic.fts.erp.repository.EventRepository;

import java.util.Collection;

public class EventRepositoryImpl implements EventRepository {
    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return null;
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {

    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
