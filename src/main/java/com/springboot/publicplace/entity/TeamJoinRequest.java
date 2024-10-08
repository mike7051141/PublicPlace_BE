package com.springboot.publicplace.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "team_join_request")
public class TeamJoinRequest extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)  // RoleType이 문자열로 저장됨
    private RoleType role;

    @Enumerated(EnumType.STRING)  // Status가 문자열로 저장됨
    private Status status;

    private String userName;
    private String userGender;
    private String userAgeRange;
    private String userPhoneNumber;
    private String joinReason;
}
