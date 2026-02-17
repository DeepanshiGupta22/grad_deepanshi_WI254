package com.example.controller;

import com.example.model.Student;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @PostMapping("/insert")
    public String insertStudent(@ModelAttribute Student student, Model model) {
        studentService.saveToBothDatabases(student);
        model.addAttribute("message", "Data successfully inserted into H2 and PostgreSQL!");
        model.addAttribute("student", new Student()); // Refresh the form object
        return "student-form";
    }
}