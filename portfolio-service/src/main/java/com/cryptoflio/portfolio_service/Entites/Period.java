package com.cryptoflio.portfolio_service.Entites;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public enum Period {
    HOUR,
    DAY,
    WEEK,
    MONTH,
    ALL_TIME
}
