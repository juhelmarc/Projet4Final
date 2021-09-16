package fr.marc.mareu.dataservice;

import java.util.Date;
import java.util.List;

import fr.marc.mareu.model.Meeting;
import fr.marc.mareu.model.Room;
import fr.marc.mareu.model.User;

public interface MeetingApiService {

    List<Meeting> getMeetingList(boolean isFiltered);

    void deleteMeeting(Meeting meeting);

    void bookMeeting(Meeting meeting);

    List<User> getUserList();

    List<String> getEmailInThisUserList(List<User> thisUserList);

    List<User> getUserListMeeting (String emailList);



    List<Room> getRoomList();

    List <String> getRoomNameList();

    void applyRoomFilter(Room room);

    void applyDateFilter(String formatDate);

}
