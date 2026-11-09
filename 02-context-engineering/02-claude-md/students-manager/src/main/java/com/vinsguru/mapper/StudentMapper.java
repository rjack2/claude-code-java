package com.vinsguru.mapper;

import com.vinsguru.dto.StudentRequest;
import com.vinsguru.dto.StudentResponse;
import com.vinsguru.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        return new Student(request.name(), request.age(), request.className());
    }

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getClassName()
        );
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setName(request.name());
        student.setAge(request.age());
        student.setClassName(request.className());
    }

}
