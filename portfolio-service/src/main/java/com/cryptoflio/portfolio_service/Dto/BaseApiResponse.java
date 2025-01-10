package com.cryptoflio.portfolio_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BaseApiResponse<T> {
    private T data;
    private String message;
}
