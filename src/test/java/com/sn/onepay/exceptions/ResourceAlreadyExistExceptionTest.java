package com.sn.onepay.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceAlreadyExistExceptionTest {

    @Test
    void singleArgConstructor_setsMessage() {
        var ex = new ResourceAlreadyExistException("already exists");
        assertThat(ex.getMessage()).isEqualTo("already exists");
    }

    @Test
    void twoArgConstructor_containsEntity() {
        var ex = new ResourceAlreadyExistException("Partnership", "someRef");
        assertThat(ex.getMessage()).contains("Partnership");
        assertThat(ex.getMessage()).contains("someRef");
    }

    @Test
    void isRuntimeException() {
        var ex = new ResourceAlreadyExistException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}