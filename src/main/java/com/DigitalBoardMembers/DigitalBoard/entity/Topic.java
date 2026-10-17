package com.DigitalBoardMembers.DigitalBoard.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "topics")
@NoArgsConstructor


public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic_name", nullable = false)
    private String topicName;

    @Column(name = "topic_description")
    private String topicDescription;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

}