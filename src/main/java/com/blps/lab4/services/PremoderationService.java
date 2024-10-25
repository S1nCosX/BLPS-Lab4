package com.blps.lab4.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PremoderationService {

    private final List<String> offensiveWords = List
            .of(
                "пися",
                "попа",
                "какашечка"
            ); // представим что тут банк плохих слов...

    public boolean containsOffensiveLanguage(String text) {
        for (String word : offensiveWords) {
            if (text.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }
}
