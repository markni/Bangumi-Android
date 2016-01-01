package io.github.scarletsky.bangumi.events;

import java.util.List;

import io.github.scarletsky.bangumi.api.models.Schedule;

/**
 * Created by scarlex on 15-7-3.
 */
public class GetCalendarEvent {

    private List<Schedule> calendars;

    public GetCalendarEvent(List<Schedule> calendars) {
        this.calendars = calendars;
    }

    public List<Schedule> getCalendars() {
        return calendars;
    }
}
