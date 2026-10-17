package com.DigitalBoardMembers.DigitalBoard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "subjects")
@NoArgsConstructor

public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_description")
    private String subjectDescription;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.PERSIST)
    private List<Topic> topics;
}
