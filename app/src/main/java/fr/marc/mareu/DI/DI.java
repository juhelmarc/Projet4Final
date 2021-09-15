package fr.marc.mareu.DI;

import fr.marc.mareu.dataservice.DummyMeetingApiService;
import fr.marc.mareu.dataservice.MeetingApiService;
import fr.marc.mareu.model.Meeting;

public class DI {

    private static MeetingApiService service = new DummyMeetingApiService();

    public static MeetingApiService getMeetingApiService() {
        return service;
    }

    public static MeetingApiService getNewInstanceApiService() {
        return new DummyMeetingApiService();
    }
}
