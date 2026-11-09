package com.vinsguru.service;

import com.vinsguru.dto.StudentRequest;
import com.vinsguru.dto.StudentResponse;
import com.vinsguru.entity.Student;
import com.vinsguru.exceptions.StudentNotFoundException;
import com.vinsguru.mapper.StudentMapper;
import com.vinsguru.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository, StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return this.repository.findAll(Sort.by("id"))
                              .stream()
                              .map(this.mapper::toResponse)
                              .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return this.mapper.toResponse(this.getStudent(id));
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        var saved = this.repository.save(this.mapper.toEntity(request));
        return this.mapper.toResponse(saved);
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        var student = this.getStudent(id);
        this.mapper.updateEntity(student, request);
        return this.mapper.toResponse(this.repository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        if (!this.repository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        this.repository.deleteById(id);
    }

    private Student getStudent(Long id) {
        return this.repository.findById(id)
                              .orElseThrow(() -> new StudentNotFoundException(id));
    }

}
