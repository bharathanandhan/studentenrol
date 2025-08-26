package com.example.student_erp.controller;

import com.example.student_erp.model.Student;
import com.example.student_erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // GET all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // GET student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE new student
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        if (student.getName() == null || student.getEmail() == null) {
            return ResponseEntity.badRequest().build(); // basic validation
        }
        Student savedStudent = studentRepository.save(student);
        System.out.println("Saved student with ID: " + savedStudent.getId()); // Debug log
        return ResponseEntity.ok(savedStudent);
    }

    // UPDATE existing student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        return studentRepository.findById(id).map(student -> {
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setCourse(studentDetails.getCourse());
            Student updated = studentRepository.save(student);
            System.out.println("Updated student with ID: " + updated.getId()); // Debug log
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        return studentRepository.findById(id).map(student -> {
            studentRepository.delete(student);
            System.out.println("Deleted student with ID: " + id); // Debug log

            // Reset AUTO_INCREMENT if table is empty
            if (studentRepository.count() == 0) {
                jdbcTemplate.update("ALTER TABLE students AUTO_INCREMENT = 1");
                System.out.println("AUTO_INCREMENT reset to 1"); // Debug log
            }

            return ResponseEntity.ok("Student deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE all students & reset auto-increment
    @DeleteMapping("/reset")
    public ResponseEntity<String> deleteAllAndResetId() {
        studentRepository.deleteAll();
        jdbcTemplate.update("ALTER TABLE students AUTO_INCREMENT = 1");
        System.out.println("All students deleted and AUTO_INCREMENT reset to 1"); // Debug log
        return ResponseEntity.ok("All students deleted and ID reset to 1");
    }
}
