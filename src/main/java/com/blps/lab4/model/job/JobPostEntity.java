package com.blps.lab4.model.job;

import com.blps.lab4.model.common.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPostEntity {
    private Long id;

    @NotNull
    @NotBlank
    private String city;

    @NotNull
    @NotBlank
    private String position;

    @NotNull
    @NotBlank
    private String company;

    @NotNull
    private Integer salary;

    @NotNull
    private Integer expectedExperience;

    @NotNull
    @Size(min = 155, max = 600)
    private String description;

    @NotNull
    private WorkMode workMode;

    public void validate() {
        if (salary <= 0)  throw new IllegalArgumentException("Зарплата должна  быть как минимум больше нуля (а еще лучше, чтобы больше прожиточного минимума раз в 5)");
        if (expectedExperience < 0) throw new IllegalArgumentException("Ожидаемый опыт не может быть меньше  нуля");
    }

    public JobPost.JobPostBuilder fromRequest() {
        return JobPost.builder()
                .city(city)
                .position(position)
                .company(company)
                .salary(salary)
                .expectedExperience(expectedExperience)
                .description(description)
                .workMode(workMode);
    }
}
