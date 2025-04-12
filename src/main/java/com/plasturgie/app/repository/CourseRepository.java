package com.plasturgie.app.repository;

import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.enums.Mode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategory(String category);
    
    List<Course> findByMode(Mode mode);
    
    List<Course> findByTitleContainingIgnoreCase(String title);
    
    List<Course> findByCertificationEligible(Boolean certificationEligible);
    
    List<Course> findByPriceLessThanEqual(BigDecimal maxPrice);
}
