package me.study.dynamodb.event.application;

import me.study.dynamodb.event.domain.EnterEventException;
import me.study.dynamodb.event.domain.EventPrize;
import me.study.dynamodb.event.infrastructure.EventEntryTestRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        eventEntryTestRepository.clear();
    }

    @Test
    void can_not_enter_if_reached_the_maximum_entries() {
        eventEntryTestRepository.setCurrentEntrantsToMaximum();
        long userId = 1L;

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(userId, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Reached the maximum entries.");
        assertThat(sut.getUserEventEntry(userId)).isNull();
    }

    @Test
    void can_not_enter_if_already_entered() {
        long userId = 1L;
        EventPrize originallyEnteredPrize = EventPrize.POINT;
        sut.enterEvent(new EnterEventRequest(userId, originallyEnteredPrize));

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(userId, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Already entered.");
        assertThat(sut.getUserEventEntry(userId)).isEqualTo(new GetUserEventEntriesResponse(userId, originallyEnteredPrize));
    }

    @Test
    void get_user_event_entry() {
        long userId = 1L;
        EnterEventRequest enterEventRequest = new EnterEventRequest(userId, EventPrize.COUPON);
        sut.enterEvent(enterEventRequest);

        assertThat(sut.getUserEventEntry(userId)).isEqualTo(new GetUserEventEntriesResponse(userId, EventPrize.COUPON));
    }

    @Test
    void if_there_is_no_entry_history_get_null() {
        assertThat(sut.getUserEventEntry(1L)).isNull();
    }

}
