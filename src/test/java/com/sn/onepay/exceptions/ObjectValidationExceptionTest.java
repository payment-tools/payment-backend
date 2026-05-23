package com.sn.onepay.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectValidationExceptionTest {

    @Test
    void constructor_setsMessage() {
        var ex = new ObjectValidationException("msg");
        assertThat(ex.getMessage()).isEqualTo("msg");
    }

    @Test
    void isRuntimeException() {
        var ex = new ObjectValidationException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}