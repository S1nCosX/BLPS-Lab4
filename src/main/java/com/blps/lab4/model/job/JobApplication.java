package com.blps.lab4.model.job;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "job_applications",
    indexes = {
        @Index(name = "job_post_id_idx", columnList = "job_post_id")
    }
)
public class JobApplication {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long jobPostId;

    @Column(nullable = false)
    private Long createdAt;
}
