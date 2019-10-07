package com.n26;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import com.n26.dto.validator.TimestampInFutureException;
import com.n26.dto.validator.TimestampInPastException;
import com.n26.dto.validator.TransactionDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionDtoValidator transactionDtoValidator;
    private final TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/transactions")
    public void postTransaction(@RequestBody @NotNull @Valid final TransactionDto transaction) {
        transactionService.addTransaction(transaction);
    }

    @GetMapping("/statistics")
    @ResponseBody
    public StatisticDto getTransactions() {
        return transactionService.getStatistics();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/transactions")
    public void deleteTransactions() {
        transactionService.deleteTransactions();
    }

    @InitBinder
    public void init(final WebDataBinder binder) {
        binder.addValidators(transactionDtoValidator);
    }

    /**
     * !!! Note to reviewer :)
     * I am not confident in that handling as didn't investigate all possible exceptions which may be thrown on
     * unserialize, a deeper investigation required.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleNotReadableException(final HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimestampInFutureException.class)
    public ResponseEntity<Object> handleTimestampInFuture(final TimestampInFutureException e) {
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TimestampInPastException.class)
    public ResponseEntity<Object> handleTimestampInPast(final TimestampInPastException e) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
