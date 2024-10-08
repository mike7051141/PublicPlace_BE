package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.entity.Status;
import com.springboot.publicplace.entity.TeamJoinRequest;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.repository.TeamJoinRequestRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.service.JoinTeamCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JoinTeamCleanupServiceImpl implements JoinTeamCleanupService {
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpJoinRequests() {
        try {
            // 현재 시간으로부터 7일 뒤에 삭제
            LocalDateTime onWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

            List<TeamJoinRequest> expiredRequests = teamJoinRequestRepository.findAllByStatusAndUpdatedAtBefore(Status.ACCEPT, onWeekAgo);
            expiredRequests.addAll(teamJoinRequestRepository.findAllByStatusAndUpdatedAtBefore(Status.REJECT, onWeekAgo));

            if (!expiredRequests.isEmpty()) {
                teamJoinRequestRepository.deleteAll(expiredRequests);
                log.info("Expired join requests deleted: {}", expiredRequests.size());
            } else {
                log.info("No expired join requests to delete.");
            }
        } catch (Exception e) {
            log.error("Error during join request cleanup: ", e);
        }
    }
}
