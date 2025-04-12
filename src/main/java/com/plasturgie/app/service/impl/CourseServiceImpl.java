package com.plasturgie.app.service.impl;

import com.plasturgie.app.exception.ResourceNotFoundException;
import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.enums.Mode;
import com.plasturgie.app.repository.CourseRepository;
import com.plasturgie.app.service.CourseService;
import com.plasturgie.app.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private InstructorService instructorService;

    @Override
    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category);
    }

    @Override
    public List<Course> getCoursesByMode(Mode mode) {
        return courseRepository.findByMode(mode);
    }

    @Override
    public List<Course> getCoursesByInstructor(Instructor instructor) {
        return instructor.getCourses().stream().collect(Collectors.toList());
    }

    @Override
    public List<Course> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Course> getCoursesByCertificationEligible(Boolean certificationEligible) {
        return courseRepository.findByCertificationEligible(certificationEligible);
    }

    @Override
    public List<Course> getCoursesByMaxPrice(BigDecimal maxPrice) {
        return courseRepository.findByPriceLessThanEqual(maxPrice);
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);
        
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setCategory(courseDetails.getCategory());
        course.setDurationHours(courseDetails.getDurationHours());
        course.setMode(courseDetails.getMode());
        course.setPrice(courseDetails.getPrice());
        course.setCertificationEligible(courseDetails.getCertificationEligible());
        
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public Course addInstructorToCourse(Long courseId, Long instructorId) {
        Course course = getCourseById(courseId);
        Instructor instructor = instructorService.getInstructorById(instructorId);
        
        course.getInstructors().add(instructor);
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public Course removeInstructorFromCourse(Long courseId, Long instructorId) {
        Course course = getCourseById(courseId);
        Instructor instructor = instructorService.getInstructorById(instructorId);
        
        course.getInstructors().remove(instructor);
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public Course setInstructorsForCourse(Long courseId, Set<Long> instructorIds) {
        Course course = getCourseById(courseId);
        
        Set<Instructor> instructors = new HashSet<>();
        for (Long instructorId : instructorIds) {
            Instructor instructor = instructorService.getInstructorById(instructorId);
            instructors.add(instructor);
        }
        
        course.setInstructors(instructors);
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }
}
