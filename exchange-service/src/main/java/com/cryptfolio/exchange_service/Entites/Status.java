package com.cryptfolio.exchange_service.Entites;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Status {

    private Timestamp timestamp;

    private int error_code;

    private String error_message;

    private int elapsed;

}
