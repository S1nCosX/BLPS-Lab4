package com.blps.lab4.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionDeniedException extends RuntimeException {
    private String message;
}
