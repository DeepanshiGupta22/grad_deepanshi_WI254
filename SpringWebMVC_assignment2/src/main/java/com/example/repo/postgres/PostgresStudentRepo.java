package com.example.repo.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Student;

public interface PostgresStudentRepo  extends JpaRepository<Student, Long> {
	
}
