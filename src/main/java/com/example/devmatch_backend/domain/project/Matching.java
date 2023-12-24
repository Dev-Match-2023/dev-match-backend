package com.example.devmatch_backend.domain.project;

import com.example.devmatch_backend.domain.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "matchings")
public class Matching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long matchingId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;

}
