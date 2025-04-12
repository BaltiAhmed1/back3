package com.plasturgie.app.service.impl;

import com.plasturgie.app.exception.ResourceNotFoundException;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.Review;
import com.plasturgie.app.model.User;
import com.plasturgie.app.repository.InstructorRepository;
import com.plasturgie.app.repository.ReviewRepository;
import com.plasturgie.app.service.InstructorService;
import com.plasturgie.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Instructor createInstructor(Instructor instructor, Long userId) {
        User user = userService.findById(userId);
        instructor.setUser(user);
        
        // Default rating if not provided
        if (instructor.getRating() == null) {
            instructor.setRating(BigDecimal.ZERO);
        }
        
        return instructorRepository.save(instructor);
    }

    @Override
    public Instructor getInstructorById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", id));
    }

    @Override
    public Instructor getInstructorByUser(User user) {
        return instructorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "userId", user.getUserId()));
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @Override
    public List<Instructor> getInstructorsByExpertise(String expertise) {
        return instructorRepository.findByExpertise(expertise);
    }

    @Override
    public List<Instructor> getInstructorsByMinRating(BigDecimal minRating) {
        return instructorRepository.findByRatingGreaterThanEqual(minRating);
    }

    @Override
    @Transactional
    public Instructor updateInstructor(Long id, Instructor instructorDetails) {
        Instructor instructor = getInstructorById(id);
        
        instructor.setBio(instructorDetails.getBio());
        instructor.setExpertise(instructorDetails.getExpertise());
        
        return instructorRepository.save(instructor);
    }

    @Override
    @Transactional
    public Instructor updateInstructorRating(Long id) {
        Instructor instructor = getInstructorById(id);
        List<Review> reviews = reviewRepository.findByInstructor(instructor);
        
        if (reviews.isEmpty()) {
            instructor.setRating(BigDecimal.ZERO);
        } else {
            double averageRating = reviews.stream()
                    .mapToInt(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            
            BigDecimal rating = BigDecimal.valueOf(averageRating)
                    .setScale(1, RoundingMode.HALF_UP);
            
            instructor.setRating(rating);
        }
        
        return instructorRepository.save(instructor);
    }

    @Override
    @Transactional
    public void deleteInstructor(Long id) {
        Instructor instructor = getInstructorById(id);
        instructorRepository.delete(instructor);
    }
}
