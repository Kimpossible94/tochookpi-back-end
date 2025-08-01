package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingParticipantEntity;
import com.tochookpi.tochookpi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingParticipantServiceImpl implements MeetingParticipantService {
    private final UserRepository userRepository;

    public List<MeetingParticipantEntity> convertUserIdsToMeetingParticipantEntities(List<Long> userIds, MeetingEntity meetingEntity) {
        return userRepository.findAllById(userIds).stream()
                .map(userEntity -> {
                    MeetingParticipantEntity participant = new MeetingParticipantEntity();
                    participant.setUser(userEntity);
                    participant.setMeeting(meetingEntity);
                    return participant;
                })
                .collect(Collectors.toList());
    }
}
