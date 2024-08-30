package com.springboot.publicplace.entity;

import lombok.*;

import javax.persistence.*;

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

    private String content;

    private String Image;

    private String matchLocation;
}
