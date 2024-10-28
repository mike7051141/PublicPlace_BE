package com.springboot.publicplace.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "team_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TeamBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamBoardId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String Image;

    private String matchLocation;

    private Double longitude;

    private Double latitude;

    @OneToMany(mappedBy = "teamBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamBoardComment> comments;
}
