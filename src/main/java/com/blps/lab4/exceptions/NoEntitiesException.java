package com.blps.lab4.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NoEntitiesException extends RuntimeException {
    private String message;
}
