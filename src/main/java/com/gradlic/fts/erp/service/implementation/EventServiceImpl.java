package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.UserEvent;
import com.gradlic.fts.erp.enumeration.EventType;
import com.gradlic.fts.erp.service.EventService;

import java.util.Collection;

public class EventServiceImpl implements EventService {

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
