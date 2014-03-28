package org.obiba.mica.jpa.repository;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;
import org.obiba.mica.config.audit.AuditEventConverter;
import org.obiba.mica.jpa.domain.PersistentAuditEvent;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

/**
 * Wraps an implementation of Spring Boot's AuditEventRepository.
 */
@Repository
public class CustomAuditEventRepository {

  @Inject
  private PersistenceAuditEventRepository persistenceAuditEventRepository;

  @Bean
  public AuditEventRepository auditEventRepository() {
    return new AuditEventRepository() {

      @Inject
      private AuditEventConverter auditEventConverter;

      @Override
      public List<AuditEvent> find(String principal, Date after) {
        List<PersistentAuditEvent> persistentAuditEvents;
        if(principal == null && after == null) {
          persistentAuditEvents = persistenceAuditEventRepository.findAll();
        } else if(after == null) {
          persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
        } else {
          persistentAuditEvents = persistenceAuditEventRepository
              .findByPrincipalAndAuditEventDateGreaterThan(principal, new LocalDateTime(after));
        }

        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
      }

      @Override
      public void add(AuditEvent event) {
        PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
        persistentAuditEvent.setPrincipal(event.getPrincipal());
        persistentAuditEvent.setAuditEventType(event.getType());
        persistentAuditEvent.setAuditEventDate(new LocalDateTime(event.getTimestamp()));
        persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));

        persistenceAuditEventRepository.save(persistentAuditEvent);
      }
    };
  }
}