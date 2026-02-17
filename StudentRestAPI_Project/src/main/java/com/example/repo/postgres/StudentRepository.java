package com.example.repo.postgres;

import com.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findBySchool(String school);
    
    long countBySchool(String school);
    
    long countByStandard(String standard);
    
    List<Student> findByPercentageGreaterThanEqualOrderByPercentageDesc(Double percentage);
    
    List<Student> findByPercentageLessThanOrderByPercentageDesc(Double percentage);
    
    long countByGenderAndStandard(String gender, String standard);
}