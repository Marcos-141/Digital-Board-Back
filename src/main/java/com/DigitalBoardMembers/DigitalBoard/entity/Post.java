package com.DigitalBoardMembers.DigitalBoard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "posts")
@NoArgsConstructor

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image")
    private String imageUrl;

    @Column(name = "creation_date")
    private LocalDate createdAt;

    @Column(name = "last_update")
    private LocalDate updateAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false )
    private User postUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic postTopic;

    @OneToMany(mappedBy = "commentPost", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

}