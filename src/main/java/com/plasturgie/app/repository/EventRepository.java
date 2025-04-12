package com.plasturgie.app.repository;

import com.plasturgie.app.model.Company;
import com.plasturgie.app.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCompany(Company company);
    
    List<Event> findByEventDateAfter(LocalDateTime date);
    
    List<Event> findByRegistrationDeadlineAfter(LocalDateTime date);
    
    List<Event> findByTitleContainingIgnoreCase(String title);
    
    List<Event> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
