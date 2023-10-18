package com.gradlic.fts.erp.repository.implementation;

import com.gradlic.fts.erp.domain.UserEvent;
import com.gradlic.fts.erp.enumeration.EventType;
import com.gradlic.fts.erp.repository.EventRepository;
import com.gradlic.fts.erp.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.gradlic.fts.erp.query.EventQuery.INSERT_EVENT_BY_USER_EMAIL_QUERY;
import static com.gradlic.fts.erp.query.EventQuery.SELECT_EVENTS_BY_USER_ID_QUERY;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, Map.of("userId", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, Map.of("email", email, "type", eventType.name(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
