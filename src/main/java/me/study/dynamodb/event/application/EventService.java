package me.study.dynamodb.event.application;

import lombok.RequiredArgsConstructor;
import me.study.dynamodb.event.domain.EventEntry;
import me.study.dynamodb.event.domain.EventRepository;
import me.study.dynamodb.event.domain.EventPrize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    void enterEvent(EnterEventRequest request) {
        eventRepository.register(new EventEntry(request.userId(), request.prize()));
    }

    GetUserEventEntryResponse getUserEventEntry(long userId) {
        return eventRepository.getEntryByUserId(userId)
                              .map(eventEntry -> new GetUserEventEntryResponse(eventEntry.getUserId(), eventEntry.getPrize()))
                              .orElse(null);
    }

    GetEntrantsByPrizeResponse getEntrantsByPrize(EventPrize prize) {
        List<EventEntry> entries = eventRepository.getEntriesByPrize(prize);

        return new GetEntrantsByPrizeResponse(entries.stream()
                                                     .map(EventEntry::getUserId)
                                                     .toList(), prize);
    }

}
