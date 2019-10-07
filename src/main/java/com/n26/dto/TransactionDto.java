package com.n26.dto;

import com.google.common.base.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TransactionDto {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDto that = (TransactionDto) o;
        return Objects.equal(amount, that.amount) &&
                Objects.equal(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount, timestamp);
    }

    public static TransactionDto of(@Nonnull final BigDecimal amount, @Nonnull final LocalDateTime timestamp) {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(amount);
        dto.setTimestamp(timestamp);
        return dto;
    }
}
