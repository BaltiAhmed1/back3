package com.plasturgie.app.repository;

import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByUser(User user);
    
    List<Instructor> findByExpertise(String expertise);
    
    List<Instructor> findByRatingGreaterThanEqual(BigDecimal minRating);
}
