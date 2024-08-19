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
public class TeamJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    private String role;

    private LocalDateTime requestDate;

    private LocalDateTime processDate;

    private String userName;
    private String userGender;
    private String userAgeRange;
    private String userPhoneNumber;
    private String joinReason;
}
