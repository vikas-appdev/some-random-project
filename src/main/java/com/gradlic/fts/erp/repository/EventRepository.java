package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.UserEvent;
import com.gradlic.fts.erp.enumeration.EventType;

import java.util.Collection;

public interface EventRepository {
    Collection<UserEvent> getEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}
