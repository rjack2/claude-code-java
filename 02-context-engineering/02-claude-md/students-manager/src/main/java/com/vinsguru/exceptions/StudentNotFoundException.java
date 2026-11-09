package com.vinsguru.exceptions;

public class StudentNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Student with id [%d] not found";

    public StudentNotFoundException(Long id) {
        super(MESSAGE.formatted(id));
    }

}
