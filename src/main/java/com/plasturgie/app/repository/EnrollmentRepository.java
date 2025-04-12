package com.plasturgie.app.repository;

import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Enrollment;
import com.plasturgie.app.model.User;
import com.plasturgie.app.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    
    List<Enrollment> findByCourse(Course course);
    
    List<Enrollment> findByStatus(Status status);
    
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    
    List<Enrollment> findByUserAndStatus(User user, Status status);
}
