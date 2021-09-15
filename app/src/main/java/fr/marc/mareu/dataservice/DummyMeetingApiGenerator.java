package fr.marc.mareu.dataservice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.marc.mareu.model.Meeting;
import fr.marc.mareu.model.Room;
import fr.marc.mareu.model.User;

public abstract class DummyMeetingApiGenerator {

    public static List<Meeting> DUMMY_MEETING = Arrays.asList(
            new Meeting( new Date(new Date().getTime()), new Date (new Date().getTime() + (45 * 60 * 1000)), new Room(1, "Room A"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )), false),
            new Meeting( new Date(new Date().getTime()), new Date (new Date().getTime() + (45 * 60 * 1000)), new Room (4, "Room D"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )),false ),
            new Meeting( new Date(new Date().getTime()), new Date (new Date().getTime() + (45 * 60 * 1000)), new Room (5, "Room E"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )),false ),
            new Meeting( new Date(new Date().getTime()), new Date (new Date().getTime() + (45 * 60 * 1000)), new Room (6, "Room F"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )),false ),
            new Meeting( new Date(new Date().getTime()), new Date (new Date().getTime() + (45 * 60 * 1000)), new Room (7, "Room G"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )),false ),
            new Meeting( new Date(new Date().getTime()), new Date (new Date().getTime() + (45 * 60 * 1000)), new Room (8, "Room H"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )),false ),
            new Meeting( new Date(new Date().getTime() + (24 * 60 * 60 * 1000)), new Date (new Date().getTime() +(24 * 60 * 60 * 1000) + (45 * 60 * 1000)), new Room (9, "Room I"), "Peach", Arrays.asList( new User ("test" , "test@test.com mail@mail.com testmail@testmail.com"  )), false)
    );

    static List<Meeting> generateMeetings() {
        return new ArrayList<>(DUMMY_MEETING);
    }

    public static List<User> DUMMY_USERS = Arrays.asList(
            new User ("Alexandre" , "alexandre@mareu.com"),
            new User ("Alexandra" , "alexandra@mareu.com"),
            new User ("Clément" , "clément@mareu.com"),
            new User ("Clémentine" , "clémentine@mareu.com"),
            new User ("Damien" , "damien@mareu.com"),
            new User ("Dorine" , "dorine@mareu.com")
    );

    static List<User> generateUser() {
        return new ArrayList<>(DUMMY_USERS);
    }

    public static List<Room> DUMMY_ROOM = Arrays.asList (
            new Room (1, "Room A"),
            new Room (2, "Room B"),
            new Room (3, "Room C"),
            new Room (4, "Room D"),
            new Room (5, "Room E"),
            new Room (6, "Room F"),
            new Room (7, "Room G"),
            new Room (8, "Room H"),
            new Room (9, "Room I"),
            new Room (10, "Room J")

    );

    static List<Room> generateRoom() {
        return new ArrayList<>(DUMMY_ROOM);
    }

}
