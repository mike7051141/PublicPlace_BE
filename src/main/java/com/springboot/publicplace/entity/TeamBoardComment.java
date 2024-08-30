package com.springboot.publicplace.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "team_board_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TeamBoardComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "team_board_id")
    private TeamBoard teamBoard;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
}
