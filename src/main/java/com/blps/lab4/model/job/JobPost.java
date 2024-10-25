package com.blps.lab4.model.job;

import com.blps.lab4.model.common.Status;
import com.blps.lab4.model.common.WorkMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "job_posts",
    indexes = {
        @Index(name = "job_created_by_idx", columnList = "created_by"),
        @Index(name = "job_moderator_id_idx", columnList = "moderator_id"),
    }
)
public class JobPost {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private Integer salary;

    @Column(nullable = false)
    private Integer expectedExperience;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private WorkMode workMode;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt = createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private Long moderatorId;
}
