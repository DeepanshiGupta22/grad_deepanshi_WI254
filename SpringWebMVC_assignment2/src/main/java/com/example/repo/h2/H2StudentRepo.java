package com.example.repo.h2;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Student;

public interface H2StudentRepo extends JpaRepository<Student, Long> {
	
}
