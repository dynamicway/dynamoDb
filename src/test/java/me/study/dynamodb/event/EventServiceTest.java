package me.study.dynamodb.event;

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

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(1L, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Reached the maximum entries.");
        assertThat(sut.getUserEventEntries(1L)).isNull();
    }

}