package com.n26;

import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;

public interface TransactionService {
    void addTransaction(TransactionDto transaction);

    StatisticDto getStatistics();

    void deleteTransactions();
}
