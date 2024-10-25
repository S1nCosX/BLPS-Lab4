package com.blps.lab4.model.cv;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Education {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationLevel level;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer endYear;
}
