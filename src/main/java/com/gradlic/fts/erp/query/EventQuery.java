package com.gradlic.fts.erp.query;

public class EventQuery {
    public static final String SELECT_EVENTS_BY_USER_ID_QUERY = "SELECT uev.id, uev.device, uev.ip_address, uev.created_at, ev.type, ev.description FROM events ev JOIN user_events uev ON ev.id = uev.event_id JOIN users u ON u.id = uev.user_id WHERE u.id = :userId ORDER BY uev.created_at DESC LIMIT 10";
    public static final String INSERT_EVENT_BY_USER_EMAIL_QUERY = "INSERT INTO user_events(user_id, event_id, device, ip_address)" +
            "VALUES ((SELECT id FROM users WHERE email = :email), (SELECT id FROM events WHERE type = :type), :device, :ipAddress)";
}
