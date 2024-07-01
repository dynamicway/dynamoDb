package me.study.dynamodb.event.application;

import lombok.RequiredArgsConstructor;
import me.study.dynamodb.event.domain.EventEntry;
import me.study.dynamodb.event.domain.EventEntryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventEntryRepository eventEntryRepository;

    /*
- 응모
    - 1000명 선착순 응모 가능
    - 유저 1명당 1번만 응모 가능
    - 응모하면 바로 경품 지급됨
    - 경품은 포인트, 쿠폰 두가지가 있음
- 조회
    - 특정 유저 응모 내역 조회
    - 포인트 받은 유저 모두 조회
    - 쿠폰 받은 유저 모두 조회
    *
    * */

    void enterEvent(EnterEventRequest request) {
        eventEntryRepository.register(new EventEntry(request.getUserId(), request.getPrize()));
    }

    GetUserEventEntriesResponse getUserEventEntry(long userId) {
        return eventEntryRepository.getUserEventEntry(userId)
                                   .map(eventEntry -> new GetUserEventEntriesResponse(eventEntry.getUserId(), eventEntry.getPrize()))
                                   .orElse(null);
    }

    GetEntrantsByPrizeResponse getEntrantsByPrize() {
        return null;
    }

}
