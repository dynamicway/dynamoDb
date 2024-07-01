package me.study.dynamodb.event.application;

import lombok.RequiredArgsConstructor;
import me.study.dynamodb.event.domain.EventEntry;
import me.study.dynamodb.event.domain.EventEntryRepository;
import me.study.dynamodb.event.domain.EventPrize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventEntryRepository eventEntryRepository;

    void enterEvent(EnterEventRequest request) {
        eventEntryRepository.register(new EventEntry(request.userId(), request.prize()));
    }

    GetUserEventEntriesResponse getUserEventEntry(long userId) {
        return eventEntryRepository.getEntryByUserId(userId)
                                   .map(eventEntry -> new GetUserEventEntriesResponse(eventEntry.getUserId(), eventEntry.getPrize()))
                                   .orElse(null);
    }

    GetEntrantsByPrizeResponse getEntrantsByPrize(EventPrize prize) {
        List<EventEntry> entries = eventEntryRepository.getEntriesByPrize(prize);

        return new GetEntrantsByPrizeResponse(entries.stream()
                                                     .map(EventEntry::getUserId)
                                                     .toList(), prize);
    }

}
