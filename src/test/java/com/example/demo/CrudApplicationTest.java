package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CrudApplicationTest {

    @Test
    void main_shouldRunWithoutExceptions() {
        assertDoesNotThrow(() -> CrudApplication.main(new String[] {}));
    }
}
