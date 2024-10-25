package com.blps.lab4.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DidNotCompletePremoderationExeption extends RuntimeException{
    String message;
}
