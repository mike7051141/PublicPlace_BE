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

    private Double latitude;

    private Double longitude;

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

    // 범위의 중간값을 이용해 평균 나이를 계산하는 방식
    @Transient
    public double getAverageAge() {
        return teamUsers.stream()
                .mapToDouble(teamUser -> {
                    String ageRange = teamUser.getUser().getAgeRange();
                    String[] range = ageRange.split("~");

                    if (range.length != 2) {
                        System.err.println("Invalid age range format: " + ageRange);
                        return 0;
                    }

                    try {
                        int lower = Integer.parseInt(range[0].trim());
                        int upper = Integer.parseInt(range[1].trim());
                        return (lower + upper) / 2.0; // 중간값을 계산
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid age range values: " + ageRange);
                        return 0; // 기본값 0 반환
                    }
                })
                .average()
                .orElse(0); // 평균을 계산, 팀원이 없으면 0 반환
    }
}
