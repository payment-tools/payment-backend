package com.sn.onepay.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void threeArgConstructor_formatsMessage() {
        var ex = new ResourceNotFoundException("Enterprise", "ID", 42L);
        assertThat(ex.getMessage()).contains("Enterprise");
        assertThat(ex.getMessage()).contains("ID");
        assertThat(ex.getMessage()).contains("42");
    }

    @Test
    void singleArgConstructor_setsMessage() {
        var ex = new ResourceNotFoundException("Custom message");
        assertThat(ex.getMessage()).isEqualTo("Custom message");
    }

    @Test
    void isRuntimeException() {
        var ex = new ResourceNotFoundException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void threeArgConstructor_withNullValue() {
        var ex = new ResourceNotFoundException("Config", "configId", null);
        assertThat(ex.getMessage()).contains("Config");
        assertThat(ex.getMessage()).contains("configId");
    }
}