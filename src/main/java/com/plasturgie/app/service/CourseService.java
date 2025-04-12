package com.plasturgie.app.service;

import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.enums.Mode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing courses
 */
public interface CourseService {
    /**
     * Create a new course
     * 
     * @param course The course to create
     * @return The created course
     */
    Course createCourse(Course course);
    
    /**
     * Get a course by its ID
     * 
     * @param id The course ID
     * @return The course with the given ID
     */
    Course getCourseById(Long id);
    
    /**
     * Get all courses
     * 
     * @return List of all courses
     */
    List<Course> getAllCourses();
    
    /**
     * Get courses by category
     * 
     * @param category The course category
     * @return List of courses in the given category
     */
    List<Course> getCoursesByCategory(String category);
    
    /**
     * Get courses by mode
     * 
     * @param mode The course delivery mode
     * @return List of courses with the given mode
     */
    List<Course> getCoursesByMode(Mode mode);
    
    /**
     * Get courses by instructor
     * 
     * @param instructor The instructor
     * @return List of courses taught by the given instructor
     */
    List<Course> getCoursesByInstructor(Instructor instructor);
    
    /**
     * Search courses by title
     * 
     * @param title The course title to search for
     * @return List of courses matching the search criteria
     */
    List<Course> searchCoursesByTitle(String title);
    
    /**
     * Get courses by certification eligibility
     * 
     * @param certificationEligible Whether the course is eligible for certification
     * @return List of courses with the given certification eligibility
     */
    List<Course> getCoursesByCertificationEligible(Boolean certificationEligible);
    
    /**
     * Get courses by maximum price
     * 
     * @param maxPrice The maximum price
     * @return List of courses with price less than or equal to the given price
     */
    List<Course> getCoursesByMaxPrice(BigDecimal maxPrice);
    
    /**
     * Update a course
     * 
     * @param id The ID of the course to update
     * @param courseDetails The updated course details
     * @return The updated course
     */
    Course updateCourse(Long id, Course courseDetails);
    
    /**
     * Add an instructor to a course
     * 
     * @param courseId The course ID
     * @param instructorId The instructor ID
     * @return The updated course
     */
    Course addInstructorToCourse(Long courseId, Long instructorId);
    
    /**
     * Remove an instructor from a course
     * 
     * @param courseId The course ID
     * @param instructorId The instructor ID
     * @return The updated course
     */
    Course removeInstructorFromCourse(Long courseId, Long instructorId);
    
    /**
     * Set instructors for a course
     * 
     * @param courseId The course ID
     * @param instructorIds Set of instructor IDs
     * @return The updated course
     */
    Course setInstructorsForCourse(Long courseId, Set<Long> instructorIds);
    
    /**
     * Delete a course
     * 
     * @param id The ID of the course to delete
     */
    void deleteCourse(Long id);
}
