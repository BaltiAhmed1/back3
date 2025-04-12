package com.plasturgie.app.service;

import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing instructors
 */
public interface InstructorService {
    /**
     * Create a new instructor
     * 
     * @param instructor The instructor to create
     * @param userId The ID of the user associated with the instructor
     * @return The created instructor
     */
    Instructor createInstructor(Instructor instructor, Long userId);
    
    /**
     * Get an instructor by ID
     * 
     * @param id The instructor ID
     * @return The instructor with the given ID
     */
    Instructor getInstructorById(Long id);
    
    /**
     * Get instructor by user
     * 
     * @param user The user
     * @return The instructor associated with the given user
     */
    Instructor getInstructorByUser(User user);
    
    /**
     * Get all instructors
     * 
     * @return List of all instructors
     */
    List<Instructor> getAllInstructors();
    
    /**
     * Get instructors by expertise
     * 
     * @param expertise The expertise
     * @return List of instructors with the given expertise
     */
    List<Instructor> getInstructorsByExpertise(String expertise);
    
    /**
     * Get instructors by minimum rating
     * 
     * @param minRating The minimum rating
     * @return List of instructors with rating greater than or equal to the given rating
     */
    List<Instructor> getInstructorsByMinRating(BigDecimal minRating);
    
    /**
     * Update an instructor
     * 
     * @param id The ID of the instructor to update
     * @param instructorDetails The updated instructor details
     * @return The updated instructor
     */
    Instructor updateInstructor(Long id, Instructor instructorDetails);
    
    /**
     * Update instructor rating
     * 
     * @param id The instructor ID
     * @return The updated instructor
     */
    Instructor updateInstructorRating(Long id);
    
    /**
     * Delete an instructor
     * 
     * @param id The ID of the instructor to delete
     */
    void deleteInstructor(Long id);
}
