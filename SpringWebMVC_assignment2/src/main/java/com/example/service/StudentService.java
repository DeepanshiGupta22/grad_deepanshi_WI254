package com.example.service;

import com.example.model.Student;
import com.example.repo.postgres.PostgresStudentRepo;
import com.example.repo.h2.H2StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    @Autowired 
    private PostgresStudentRepo postgresRepo;
    
    @Autowired 
    private H2StudentRepo h2Repo;

    @Transactional(transactionManager = "postgresTransactionManager")
    public boolean saveToBothDatabases(Student student) {
        
        if (postgresRepo.existsById(student.getRollNo())) {
            return false; 
        }
        postgresRepo.save(student);
        h2Repo.save(student);
        return true; 
    }
}