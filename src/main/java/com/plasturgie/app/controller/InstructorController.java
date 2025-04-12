package com.plasturgie.app.controller;

import com.plasturgie.app.model.Instructor;
import com.plasturgie.app.model.User;
import com.plasturgie.app.security.UserPrincipal;
import com.plasturgie.app.service.InstructorService;
import com.plasturgie.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;
    
    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Instructor> createInstructor(
            @Valid @RequestBody Instructor instructor,
            @RequestParam Long userId) {
        
        Instructor newInstructor = instructorService.createInstructor(instructor, userId);
        return ResponseEntity.ok(newInstructor);
    }

    @GetMapping
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instructor> getInstructorById(@PathVariable Long id) {
        Instructor instructor = instructorService.getInstructorById(id);
        return ResponseEntity.ok(instructor);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Instructor> getInstructorByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        Instructor instructor = instructorService.getInstructorByUser(user);
        return ResponseEntity.ok(instructor);
    }

    @GetMapping("/by-expertise/{expertise}")
    public ResponseEntity<List<Instructor>> getInstructorsByExpertise(@PathVariable String expertise) {
        List<Instructor> instructors = instructorService.getInstructorsByExpertise(expertise);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/by-min-rating/{minRating}")
    public ResponseEntity<List<Instructor>> getInstructorsByMinRating(@PathVariable BigDecimal minRating) {
        List<Instructor> instructors = instructorService.getInstructorsByMinRating(minRating);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Instructor> getCurrentInstructor(@AuthenticationPrincipal UserPrincipal currentUser) {
        User user = userService.findById(currentUser.getId());
        Instructor instructor = instructorService.getInstructorByUser(user);
        return ResponseEntity.ok(instructor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @instructorSecurity.isCurrentInstructor(#id, #currentUser)")
    public ResponseEntity<Instructor> updateInstructor(
            @PathVariable Long id,
            @Valid @RequestBody Instructor instructorDetails,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        // Verify permissions
        Instructor existingInstructor = instructorService.getInstructorById(id);
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) &&
                !existingInstructor.getUser().getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        Instructor updatedInstructor = instructorService.updateInstructor(id, instructorDetails);
        return ResponseEntity.ok(updatedInstructor);
    }

    @PutMapping("/{id}/update-rating")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Instructor> updateInstructorRating(@PathVariable Long id) {
        Instructor instructor = instructorService.updateInstructorRating(id);
        return ResponseEntity.ok(instructor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.ok().build();
    }
}
