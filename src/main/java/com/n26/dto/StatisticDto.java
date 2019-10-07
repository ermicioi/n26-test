package com.n26.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StatisticDto {

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal sum;

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal avg;

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal max;

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal min;

    private long count;

}
