package com.springboot.publicplace.entity;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table
@Builder
public class Team extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(unique = true)
    private String teamName;

    private String teamInfo;

    private String teamLocation;

    private String teamImg;

    @ElementCollection
    @CollectionTable(name = "team_activity_days", joinColumns = @JoinColumn(name = "team_id"))
    private List<String> activityDays;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamUser> teamUsers;

    @Transient
    public Long getTeamMembers() {
        return (long) teamUsers.size();
    }

}
