package com.blps.lab4.model.cv;

import com.blps.lab4.model.common.Status;
import com.blps.lab4.model.common.WorkMode;
import com.blps.lab4.services.PremoderationService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "resumes",
    indexes = {
            @Index(name = "resume_created_by_idx", columnList = "created_by"),
            @Index(name = "resume_moderator_id_idx", columnList = "moderator_id"),
    }
)
public class Resume implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String position;

    @ElementCollection(targetClass = WorkMode.class,
    fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "user_work_modes",
        joinColumns = @JoinColumn(name = "id")
    )
    @Column(nullable = false)
    private Set<WorkMode> modes = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "educations")
    @Column(nullable = false)
    private Set<Education> educations = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "work_expierences")
    @Column(nullable = false)
    private Set<WorkExperience> workExperiences = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status premoderationStatus;


    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt = createdAt;

    private Long moderatorId;

    public static Resume deserialize(String json) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(json);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Resume resume = (Resume) objectInputStream.readObject();
        objectInputStream.close();
        return resume;
    }

    public String serialize() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

    }
}
