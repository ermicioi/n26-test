package com.n26.dto.validator;

import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;


public class TransactionDtoValidatorTest {

    private TransactionDtoValidator validator;

    @Before
    public void setUp() {
        final Clock clock = Clock.fixed(Instant.parse("2019-09-15T10:10:30.00Z"), ZoneId.of("UTC"));
        validator = new TransactionDtoValidator(clock, 60);
    }

    @Test
    public void supports() {
        assertThat(validator.supports(TransactionDto.class)).isTrue();
        assertThat(validator.supports(StatisticDto.class)).isFalse();
    }

    @Test
    public void shouldValidateSuccessful() {
        final TransactionDto tx1 = new TransactionDto();
        tx1.setTimestamp(LocalDateTime.parse("2019-09-15T10:09:30"));
        validator.validate(tx1, null);

        final TransactionDto tx2 = new TransactionDto();
        tx2.setTimestamp(LocalDateTime.parse("2019-09-15T10:10:30"));
        validator.validate(tx2, null);
    }

    @Test(expected = TimestampInPastException.class)
    public void shouldFailWhenTxOld() {
        final TransactionDto tx = new TransactionDto();
        tx.setTimestamp(LocalDateTime.parse("2019-09-15T10:09:29"));
        validator.validate(tx, null);
    }

    @Test(expected = TimestampInFutureException.class)
    public void shouldFailWhenTxInFuture() {
        final TransactionDto tx = new TransactionDto();
        tx.setTimestamp(LocalDateTime.parse("2019-09-15T10:10:31"));
        validator.validate(tx, null);
    }

}