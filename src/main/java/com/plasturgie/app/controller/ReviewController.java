package com.plasturgie.app.controller;

import com.plasturgie.app.model.Course;
import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.Review;
import com.plasturgie.app.model.User;
import com.plasturgie.app.security.UserPrincipal;
import com.plasturgie.app.service.CourseService;
import com.plasturgie.app.service.InstructorService;
import com.plasturgie.app.service.ReviewService;
import com.plasturgie.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private InstructorService instructorService;

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasRole('LEARNER') or hasRole('ADMIN')")
    public ResponseEntity<Review> createCourseReview(
            @PathVariable Long courseId,
            @Valid @RequestBody Review review,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Review newReview = reviewService.createCourseReview(review, currentUser.getId(), courseId);
        return ResponseEntity.ok(newReview);
    }

    @PostMapping("/instructor/{instructorId}")
    @PreAuthorize("hasRole('LEARNER') or hasRole('ADMIN')")
    public ResponseEntity<Review> createInstructorReview(
            @PathVariable Long instructorId,
            @Valid @RequestBody Review review,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Review newReview = reviewService.createInstructorReview(review, currentUser.getId(), instructorId);
        return ResponseEntity.ok(newReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Review>> getReviewsByCourse(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        List<Review> reviews = reviewService.getReviewsByCourse(course);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Review>> getReviewsByInstructor(@PathVariable Long instructorId) {
        Instructor instructor = instructorService.getInstructorById(instructorId);
        List<Review> reviews = reviewService.getReviewsByInstructor(instructor);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Review>> getReviewsByUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        User user = userService.findById(currentUser.getId());
        List<Review> reviews = reviewService.getReviewsByUser(user);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<Review>> getReviewsByRating(@PathVariable Integer rating) {
        List<Review> reviews = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/course/{courseId}/average")
    public ResponseEntity<Double> getAverageRatingForCourse(@PathVariable Long courseId) {
        double averageRating = reviewService.calculateAverageRatingForCourse(courseId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/instructor/{instructorId}/average")
    public ResponseEntity<Double> getAverageRatingForInstructor(@PathVariable Long instructorId) {
        double averageRating = reviewService.calculateAverageRatingForInstructor(instructorId);
        return ResponseEntity.ok(averageRating);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody Review reviewDetails,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Review existingReview = reviewService.getReviewById(id);
        
        // Check if the current user is the reviewer or an admin
        if (!existingReview.getUser().getUserId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        
        Review updatedReview = reviewService.updateReview(id, reviewDetails);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Review review = reviewService.getReviewById(id);
        
        // Check if the current user is the reviewer or an admin
        if (!review.getUser().getUserId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
