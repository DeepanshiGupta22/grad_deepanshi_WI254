package com.example.controller;

import com.example.model.Student;
import com.example.repo.postgres.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository repo;

    // GET /students
    @GetMapping("/all")
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    // GET /students/{regNo}
    @GetMapping("/{regNo}")
    public ResponseEntity<Student> getStudentByRegNo(@PathVariable Long regNo) {
        Optional<Student> student = repo.findById(regNo);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /students
    @PostMapping("/insert")
    public Student insertStudent(@RequestBody Student student) {
        return repo.save(student);
    }

    // PUT /students/{regNo} - Full Replacement
    @PutMapping("/{regNo}")
    public ResponseEntity<Student> updateStudentPut(@PathVariable Long regNo, @RequestBody Student studentDetails) {
        if (!repo.existsById(regNo)) {
            return ResponseEntity.notFound().build();
        }
        // By saving studentDetails directly, any fields missing from the JSON payload 
        // will automatically become null in the database.
        studentDetails.setRegNo(regNo); 
        return ResponseEntity.ok(repo.save(studentDetails));
    }

    // PATCH /students/{regNo} - Partial Update
    @PatchMapping("/{regNo}")
    public ResponseEntity<Student> updateStudentPatch(@PathVariable Long regNo, @RequestBody Student updates) {
        Optional<Student> optionalStudent = repo.findById(regNo);
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Student existing = optionalStudent.get();
        
        // Only updates the field if it was explicitly provided in the JSON payload
        if (updates.getRollNo() != null) existing.setRollNo(updates.getRollNo());
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getStandard() != null) existing.setStandard(updates.getStandard());
        if (updates.getSchool() != null) existing.setSchool(updates.getSchool());
        if (updates.getGender() != null) existing.setGender(updates.getGender());
        if (updates.getPercentage() != null) existing.setPercentage(updates.getPercentage());
        
        return ResponseEntity.ok(repo.save(existing));
    }

    // DELETE /students/{regNo}
    @DeleteMapping("/{regNo}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long regNo) {
        if (!repo.existsById(regNo)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(regNo);
        return ResponseEntity.ok().build();
    }

    // GET /students/school?name=KV
    @GetMapping("/school")
    public List<Student> getStudentsBySchool(@RequestParam String name) {
        return repo.findBySchool(name);
    }

    // GET /students/school/count?name=DPS
    @GetMapping("/school/count")
    public long getSchoolCount(@RequestParam String name) {
        return repo.countBySchool(name);
    }

    // GET /students/school/standard/count?class=5
    @GetMapping("/school/standard/count")
    public long getStandardCount(@RequestParam("class") String standard) {
        return repo.countByStandard(standard);
    }

    // GET /students/result?pass=true/false
    @GetMapping("/result")
    public List<Student> getResults(@RequestParam boolean pass) {
        if (pass) {
            return repo.findByPercentageGreaterThanEqualOrderByPercentageDesc(40.0);
        } else {
            return repo.findByPercentageLessThanOrderByPercentageDesc(40.0);
        }
    }

    // GET /students/strength?gender=MALE&standard=5
    @GetMapping("/strength")
    public long getStrengthByGenderAndStandard(@RequestParam String gender, @RequestParam String standard) {
        return repo.countByGenderAndStandard(gender, standard);
    }
}