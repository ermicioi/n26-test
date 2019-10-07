package com.n26;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.InvalidPropertiesFormatException;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionControllerTest {

    private TransactionController controller;

    @Before
    public void setUp() {
        // !!! Reviewer -> I was compiling on Java 12 and there is issue with reflection.
        // In normal case I would spend time in investigation how to make Mockito to work with Java 12 and modules, but
        // this is workaround to pass this test :)
//        controller = new TransactionController(
//                mock(TransactionDtoValidator.class),
//                mock(TransactionService.class));
        controller = new TransactionController(null, null);
    }

    @Test
    public void shouldHandleInvalidJsonMessage() {
//        final InvalidFormatException invalidFormatException = new InvalidFormatException(
//                mock(JsonParser.class), "Dummy error message", null, String.class);
        final InvalidFormatException invalidFormatException = new InvalidFormatException(
                null, "Dummy error message", null, String.class);
        final ResponseEntity<Object> response = controller.handleNotReadableException(
                new HttpMessageNotReadableException("Some dummy message", invalidFormatException));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void shouldHandleUnparsableMessageField() {
        final InvalidPropertiesFormatException invalidFormatException =
                new InvalidPropertiesFormatException("The property is invalid");
        final ResponseEntity<Object> response = controller.handleNotReadableException(
                new HttpMessageNotReadableException("Some dummy message", invalidFormatException));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}