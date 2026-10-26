package com.DigitalBoardMembers.DigitalBoard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "comments")
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image")
    private String image;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentUser;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post commentPost;

}