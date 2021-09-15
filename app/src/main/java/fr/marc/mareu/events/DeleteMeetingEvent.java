package fr.marc.mareu.events;

import fr.marc.mareu.model.Meeting;

public class DeleteMeetingEvent {

    public Meeting meeting;

    /**
     * Constructor.
     * @param meeting
     */
    public DeleteMeetingEvent(Meeting meeting) {
        this.meeting = meeting;
    }
}
