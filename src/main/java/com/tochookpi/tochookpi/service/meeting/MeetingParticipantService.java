package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingParticipantEntity;

import java.util.List;

public interface MeetingParticipantService {
    List<MeetingParticipantEntity> convertUserIdsToMeetingParticipantEntities(List<Long> userIds, MeetingEntity meetingEntity);
}
