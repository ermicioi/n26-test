package com.n26;

import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionServiceImplTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2019-09-15T10:10:30.00Z"), ZoneId.of("UTC"));

    private TransactionServiceImpl service;

    @Before
    public void setUp() {
        service = new TransactionServiceImpl(CLOCK, 60);
    }

    @Test
    public void shouldProvideZeroValuesWhenNoTransactions() {
        final BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        final StatisticDto statistics = service.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.getSum()).isEqualTo(zero);
        assertThat(statistics.getAvg()).isEqualTo(zero);
        assertThat(statistics.getMax()).isEqualTo(zero);
        assertThat(statistics.getMin()).isEqualTo(zero);
        assertThat(statistics.getCount()).isEqualTo(0);
    }

    @Test
    public void shouldProvideZeroValuesWhenNoValidTransactions( ) {
        service.addTransaction(TransactionDto.of(BigDecimal.ONE, LocalDateTime.now(CLOCK).minusSeconds(60)));
        service.addTransaction(TransactionDto.of(BigDecimal.ONE, LocalDateTime.now(CLOCK).plusSeconds(1)));

        final BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        final StatisticDto statistics = service.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.getSum()).isEqualTo(zero);
        assertThat(statistics.getAvg()).isEqualTo(zero);
        assertThat(statistics.getMax()).isEqualTo(zero);
        assertThat(statistics.getMin()).isEqualTo(zero);
        assertThat(statistics.getCount()).isEqualTo(0);
    }

    @Test
    public void shouldTakeOnlyValidTransactions() {
        service.addTransaction(TransactionDto.of(BigDecimal.ONE, LocalDateTime.now(CLOCK).minusSeconds(60)));
        service.addTransaction(TransactionDto.of(BigDecimal.valueOf(10), LocalDateTime.now(CLOCK).minusSeconds(59)));
        service.addTransaction(TransactionDto.of(BigDecimal.valueOf(20), LocalDateTime.now(CLOCK)));
        service.addTransaction(TransactionDto.of(BigDecimal.ONE, LocalDateTime.now(CLOCK).plusSeconds(1)));

        final StatisticDto statistics = service.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.getSum()).isEqualTo(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_UP));
        assertThat(statistics.getAvg()).isEqualTo(BigDecimal.valueOf(15).setScale(2, RoundingMode.HALF_UP));
        assertThat(statistics.getMax()).isEqualTo(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP));
        assertThat(statistics.getMin()).isEqualTo(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP));
        assertThat(statistics.getCount()).isEqualTo(2);
    }

}