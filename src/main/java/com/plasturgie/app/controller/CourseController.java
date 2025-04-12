package com.plasturgie.app.controller;

import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.enums.Mode;
import com.plasturgie.app.security.UserPrincipal;
import com.plasturgie.app.service.CourseService;
import com.plasturgie.app.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private InstructorService instructorService;

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody Course course,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Course newCourse = courseService.createCourse(course);
        
        // If the current user is an instructor, add them as an instructor for the course
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"))) {
            try {
                Instructor instructor = instructorService.getInstructorByUser(
                        instructorService.getInstructorById(currentUser.getId()).getUser());
                courseService.addInstructorToCourse(newCourse.getCourseId(), instructor.getInstructorId());
            } catch (Exception e) {
                // User is not an instructor, just continue
            }
        }
        
        return ResponseEntity.ok(newCourse);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable String category) {
        List<Course> courses = courseService.getCoursesByCategory(category);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/by-mode/{mode}")
    public ResponseEntity<List<Course>> getCoursesByMode(@PathVariable Mode mode) {
        List<Course> courses = courseService.getCoursesByMode(mode);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/by-instructor/{instructorId}")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable Long instructorId) {
        Instructor instructor = instructorService.getInstructorById(instructorId);
        List<Course> courses = courseService.getCoursesByInstructor(instructor);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String title) {
        List<Course> courses = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/certification-eligible/{eligible}")
    public ResponseEntity<List<Course>> getCoursesByCertificationEligible(@PathVariable Boolean eligible) {
        List<Course> courses = courseService.getCoursesByCertificationEligible(eligible);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/max-price/{maxPrice}")
    public ResponseEntity<List<Course>> getCoursesByMaxPrice(@PathVariable BigDecimal maxPrice) {
        List<Course> courses = courseService.getCoursesByMaxPrice(maxPrice);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody Course courseDetails,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        // Check if the current user is an instructor for this course or an admin
        Course existingCourse = courseService.getCourseById(id);
        boolean isInstructor = false;
        
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"))) {
            try {
                Instructor instructor = instructorService.getInstructorByUser(
                        instructorService.getInstructorById(currentUser.getId()).getUser());
                
                isInstructor = existingCourse.getInstructors().contains(instructor);
            } catch (Exception e) {
                // User is not an instructor
            }
        }
        
        if (!isInstructor && 
                currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        
        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return ResponseEntity.ok(updatedCourse);
    }

    @PostMapping("/{courseId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<Course> addInstructorToCourse(
            @PathVariable Long courseId,
            @PathVariable Long instructorId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        // Check permissions
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // If not admin, check if the current user is the instructor being added
            Instructor instructor = instructorService.getInstructorById(instructorId);
            if (!instructor.getUser().getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
        }
        
        Course course = courseService.addInstructorToCourse(courseId, instructorId);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<Course> removeInstructorFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long instructorId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        // Check permissions
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // If not admin, check if the current user is the instructor being removed
            Instructor instructor = instructorService.getInstructorById(instructorId);
            if (!instructor.getUser().getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).build();
            }
        }
        
        Course course = courseService.removeInstructorFromCourse(courseId, instructorId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{courseId}/instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> setInstructorsForCourse(
            @PathVariable Long courseId,
            @RequestBody Set<Long> instructorIds) {
        
        Course course = courseService.setInstructorsForCourse(courseId, instructorIds);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }
}
