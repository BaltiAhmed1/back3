package com.plasturgie.app.service;

import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.Review;
import com.plasturgie.app.model.User;

import java.util.List;

/**
 * Service interface for managing reviews
 */
public interface ReviewService {
    /**
     * Create a course review
     * 
     * @param review The review
     * @param userId The user ID
     * @param courseId The course ID
     * @return The created review
     */
    Review createCourseReview(Review review, Long userId, Long courseId);
    
    /**
     * Create an instructor review
     * 
     * @param review The review
     * @param userId The user ID
     * @param instructorId The instructor ID
     * @return The created review
     */
    Review createInstructorReview(Review review, Long userId, Long instructorId);
    
    /**
     * Get a review by ID
     * 
     * @param id The review ID
     * @return The review with the given ID
     */
    Review getReviewById(Long id);
    
    /**
     * Get reviews by course
     * 
     * @param course The course
     * @return List of reviews for the given course
     */
    List<Review> getReviewsByCourse(Course course);
    
    /**
     * Get reviews by instructor
     * 
     * @param instructor The instructor
     * @return List of reviews for the given instructor
     */
    List<Review> getReviewsByInstructor(Instructor instructor);
    
    /**
     * Get reviews by user
     * 
     * @param user The user
     * @return List of reviews by the given user
     */
    List<Review> getReviewsByUser(User user);
    
    /**
     * Get a review by user and course
     * 
     * @param user The user
     * @param course The course
     * @return The review by the given user for the given course
     */
    Review getReviewByUserAndCourse(User user, Course course);
    
    /**
     * Get a review by user and instructor
     * 
     * @param user The user
     * @param instructor The instructor
     * @return The review by the given user for the given instructor
     */
    Review getReviewByUserAndInstructor(User user, Instructor instructor);
    
    /**
     * Get reviews by rating
     * 
     * @param rating The rating
     * @return List of reviews with the given rating
     */
    List<Review> getReviewsByRating(Integer rating);
    
    /**
     * Update a review
     * 
     * @param id The review ID
     * @param reviewDetails The updated review details
     * @return The updated review
     */
    Review updateReview(Long id, Review reviewDetails);
    
    /**
     * Delete a review
     * 
     * @param id The ID of the review to delete
     */
    void deleteReview(Long id);
    
    /**
     * Calculate average rating for a course
     * 
     * @param courseId The course ID
     * @return The average rating
     */
    double calculateAverageRatingForCourse(Long courseId);
    
    /**
     * Calculate average rating for an instructor
     * 
     * @param instructorId The instructor ID
     * @return The average rating
     */
    double calculateAverageRatingForInstructor(Long instructorId);
}
