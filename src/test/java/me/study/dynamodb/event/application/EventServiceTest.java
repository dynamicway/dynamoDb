package me.study.dynamodb.event.application;

import me.study.dynamodb.event.domain.EnterEventException;
import me.study.dynamodb.event.domain.EventEntry;
import me.study.dynamodb.event.domain.EventPrize;
import me.study.dynamodb.event.infrastructure.EventEntryTestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class EventServiceTest {
    @Autowired
    private EventEntryTestRepository eventEntryTestRepository;

    @Autowired
    private EventService sut;

    @Test
    void can_not_enter_if_reached_the_maximum_entries() {
        eventEntryTestRepository.setCurrentEntrantsToMaximum();
        long userId = 1L;

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(userId, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Reached the maximum entries.");
        assertThat(sut.getUserEventEntries(userId)).isNull();
    }

    @Test
    void can_not_enter_if_already_entered() {
        long userId = 1L;
        eventEntryTestRepository.registerEntry(new EventEntry(userId, EventPrize.COUPON));

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(userId, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Already entered.");
        assertThat(sut.getUserEventEntries(userId)).isEqualTo(new GetUserEventEntriesResponse(userId, EventPrize.POINT));
    }

}