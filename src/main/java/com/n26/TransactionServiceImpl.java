package com.n26;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TransactionServiceImpl implements TransactionService {

    private static final int VALUE_SCALE = 2;
    private static final RoundingMode VALUE_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal SCALED_ZERO = BigDecimal.ZERO.setScale(VALUE_SCALE, VALUE_ROUNDING_MODE);

    private final Clock clock;
    private final int retentionTime;

    /**
     * !!! Note to reviewer :)
     * A better data structure may be used.
     * Week point here I would say is the key, which may produce collisions.
     */
    private final Cache<Integer, TransactionDto> cache;

    public TransactionServiceImpl(@Nonnull final Clock clock, @Value("${retentionTime}") final int retentionTime) {
        this.clock = clock;
        this.retentionTime = retentionTime;
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(retentionTime * 2, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void addTransaction(@Nonnull final TransactionDto transaction) {
        cache.put(transaction.hashCode(), transaction);
    }

    @Override
    @Nonnull
    public StatisticDto getStatistics() {
        final LocalDateTime now = LocalDateTime.now(clock);
        final LocalDateTime lBoundary = now.minusSeconds(retentionTime);
        final LocalDateTime hBoundary = now.plusSeconds(1);

        final AtomicReference<BigDecimal> sum = new AtomicReference<>(SCALED_ZERO);
        final AtomicReference<BigDecimal> min = new AtomicReference<>(BigDecimal.valueOf(Double.MAX_VALUE));
        final AtomicReference<BigDecimal> max = new AtomicReference<>(BigDecimal.valueOf(Double.MIN_VALUE));

        final ImmutableList<TransactionDto> transactions = cache.asMap().values().stream()
                .filter(tx -> {
                    final LocalDateTime timestamp = tx.getTimestamp();
                    return timestamp.isAfter(lBoundary) && timestamp.isBefore(hBoundary);
                })
                .peek(tx -> {
                    sum.accumulateAndGet(tx.getAmount(), BigDecimal::add);
                    min.getAndUpdate(x -> x.min(tx.getAmount()));
                    max.getAndUpdate(x -> x.max(tx.getAmount()));
                })
                .collect(ImmutableList.toImmutableList());

        final StatisticDto statistic = new StatisticDto();
        statistic.setSum(sum.get().setScale(VALUE_SCALE, VALUE_ROUNDING_MODE));

        final int count = transactions.size();
        statistic.setCount(count);

        if (count > 0) {
            statistic.setAvg(sum.get().divide(BigDecimal.valueOf(count), VALUE_SCALE, VALUE_ROUNDING_MODE));
            statistic.setMax(max.get().setScale(VALUE_SCALE, VALUE_ROUNDING_MODE));
            statistic.setMin(min.get().setScale(VALUE_SCALE, VALUE_ROUNDING_MODE));
        } else {
            statistic.setAvg(SCALED_ZERO);
            statistic.setMax(SCALED_ZERO);
            statistic.setMin(SCALED_ZERO);
        }

        return statistic;
    }

    @Override
    public void deleteTransactions() {
        cache.invalidateAll();
    }

}
