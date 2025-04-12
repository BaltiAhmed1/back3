package com.plasturgie.app.service.impl;

import com.plasturgie.app.exception.ResourceNotFoundException;
import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.Review;
import com.plasturgie.app.model.User;
import com.plasturgie.app.repository.ReviewRepository;
import com.plasturgie.app.service.CourseService;
import com.plasturgie.app.service.InstructorService;
import com.plasturgie.app.service.ReviewService;
import com.plasturgie.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private InstructorService instructorService;

    @Override
    @Transactional
    public Review createCourseReview(Review review, Long userId, Long courseId) {
        User user = userService.findById(userId);
        Course course = courseService.getCourseById(courseId);
        
        // Check if user has already reviewed this course
        if (reviewRepository.findByUserAndCourse(user, course).isPresent()) {
            throw new IllegalStateException("User has already reviewed this course");
        }
        
        review.setUser(user);
        review.setCourse(course);
        
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review createInstructorReview(Review review, Long userId, Long instructorId) {
        User user = userService.findById(userId);
        Instructor instructor = instructorService.getInstructorById(instructorId);
        
        // Check if user has already reviewed this instructor
        if (reviewRepository.findByUserAndInstructor(user, instructor).isPresent()) {
            throw new IllegalStateException("User has already reviewed this instructor");
        }
        
        review.setUser(user);
        review.setInstructor(instructor);
        
        Review savedReview = reviewRepository.save(review);
        
        // Update instructor rating
        instructorService.updateInstructorRating(instructorId);
        
        return savedReview;
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
    }

    @Override
    public List<Review> getReviewsByCourse(Course course) {
        return reviewRepository.findByCourse(course);
    }

    @Override
    public List<Review> getReviewsByInstructor(Instructor instructor) {
        return reviewRepository.findByInstructor(instructor);
    }

    @Override
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    @Override
    public Review getReviewByUserAndCourse(User user, Course course) {
        return reviewRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "user and course", 
                        user.getUserId() + " and " + course.getCourseId()));
    }

    @Override
    public Review getReviewByUserAndInstructor(User user, Instructor instructor) {
        return reviewRepository.findByUserAndInstructor(user, instructor)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "user and instructor", 
                        user.getUserId() + " and " + instructor.getInstructorId()));
    }

    @Override
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Review reviewDetails) {
        Review review = getReviewById(id);
        
        review.setRating(reviewDetails.getRating());
        review.setComment(reviewDetails.getComment());
        
        Review savedReview = reviewRepository.save(review);
        
        // If this is an instructor review, update the instructor's rating
        if (review.getInstructor() != null) {
            instructorService.updateInstructorRating(review.getInstructor().getInstructorId());
        }
        
        return savedReview;
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = getReviewById(id);
        
        // Store instructor ID if this is an instructor review
        Long instructorId = null;
        if (review.getInstructor() != null) {
            instructorId = review.getInstructor().getInstructorId();
        }
        
        reviewRepository.delete(review);
        
        // If this was an instructor review, update the instructor's rating
        if (instructorId != null) {
            instructorService.updateInstructorRating(instructorId);
        }
    }

    @Override
    public double calculateAverageRatingForCourse(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        List<Review> reviews = reviewRepository.findByCourse(course);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);
    }

    @Override
    public double calculateAverageRatingForInstructor(Long instructorId) {
        Instructor instructor = instructorService.getInstructorById(instructorId);
        List<Review> reviews = reviewRepository.findByInstructor(instructor);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);
    }
}
