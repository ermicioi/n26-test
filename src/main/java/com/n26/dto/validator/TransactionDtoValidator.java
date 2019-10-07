package com.n26.dto.validator;

import com.n26.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class TransactionDtoValidator implements Validator {

    private final Clock clock;
    private final int retentionTime;

    public TransactionDtoValidator(final Clock clock, @Value("${retentionTime}") int retentionTime) {
        this.clock = clock;
        this.retentionTime = retentionTime;
    }


    @Override
    public boolean supports(Class<?> aClass) {
        return TransactionDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        final LocalDateTime now = LocalDateTime.now(clock);
        final TransactionDto transaction = (TransactionDto) o;

        if (transaction.getTimestamp().isAfter(now)) {
            throw new TimestampInFutureException();
        }

        if (transaction.getTimestamp().isBefore(now.minusSeconds(retentionTime))) {
            throw new TimestampInPastException();
        }
    }
}
