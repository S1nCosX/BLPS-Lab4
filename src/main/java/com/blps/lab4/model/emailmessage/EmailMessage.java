package com.blps.lab4.model.emailmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
    private String recipientEmail;
    private String topic;
    private String message;
}
