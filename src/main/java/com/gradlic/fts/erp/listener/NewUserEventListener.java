package com.gradlic.fts.erp.listener;

import com.gradlic.fts.erp.event.NewUserEvent;
import com.gradlic.fts.erp.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.gradlic.fts.erp.utils.RequestUtils.getDevice;
import static com.gradlic.fts.erp.utils.RequestUtils.getIpAddress;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent even){
        eventService.addUserEvent(even.getEmail(), even.getType(), getDevice(request), getIpAddress(request));
    }

}
