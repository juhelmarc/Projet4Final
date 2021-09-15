package fr.marc.mareu.ui.booking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.marc.mareu.DI.DI;
import fr.marc.mareu.R;
import fr.marc.mareu.dataservice.MeetingApiService;
import fr.marc.mareu.model.Meeting;
import fr.marc.mareu.model.Room;
import fr.marc.mareu.model.User;

public class BookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.datePicker)
    EditText mDate;
    @BindView( R.id.hour )
    EditText mHour;
    @BindView( R.id.end_hour )
    EditText mEndDate;
    @BindView( R.id.resum_date )
    TextView mResumDate;
    @BindView( R.id.mail_autocomplete )
    MultiAutoCompleteTextView mMail;
    @BindView( R.id.room_spinner )
    Spinner mMeetingRoom;
    @BindView( R.id.subject )
    EditText mSubject;
    @BindView( R.id.book )
    Button mBookButton;

    private MeetingApiService mApiService;

    private Long startDatePickedMilli;
    private Long endDatePickedMilli;
    private Long duration;

    private String datePickedFormated;
    private String timePickedFormated;
    private String endTimePickedFormated;

    private List<String> meetingRoomList;
    private List<User> userList;

    private DatePickerDialog mDatePickerDialog;

    private boolean dateEdited;
    private boolean hourEdited;
    private boolean durationEdited;
    private boolean mailEdited;
    private boolean subjectEdited;
    private boolean roomIsTaken;
    private boolean crenelIsFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_booking );
        ButterKnife.bind( this );

        mApiService = DI.getMeetingApiService();

        mSubject.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                subjectEdited = true;
            }
        } );
        initDatePicker();
        initMailMultiAutoComplete();
        initMeetingRoomSpinner();

    }

    public void initMailMultiAutoComplete() {
        List<User> userList = mApiService.getUserList();
        List<String> emailList = mApiService.getEmailInThisUserList( userList );
        ArrayAdapter emailListAdapter = new ArrayAdapter( this, android.R.layout.simple_list_item_1, emailList );
        mMail.setAdapter( emailListAdapter);
        mMail.setTokenizer( new MultiAutoCompleteTextView.CommaTokenizer());
        mMail.setThreshold( 1 );

        mMail.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mailEdited = true;
            }
        } );

    }

    public void initMeetingRoomSpinner () {
        meetingRoomList = mApiService.getRoomNameList();
        //meetingRoomList = new String[]{"Chose room", "Meeting room A", "Meeting room B", "Meeting room C", "Meeting room D", "Meeting room E", "Meeting room F", "Meeting room G", "Meeting room H", "Meeting room I", "Meeting room J"};

        ArrayAdapter meetingRoomListAdapter = new ArrayAdapter( this, android.R.layout.simple_spinner_item, meetingRoomList );
        meetingRoomListAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mMeetingRoom.setAdapter( meetingRoomListAdapter );
        mMeetingRoom.setOnItemSelectedListener( this );
    }

    public void initDatePicker() {

        Calendar calendar = Calendar.getInstance();
        int y = calendar.get( Calendar.YEAR );
        int m = calendar.get( Calendar.MONTH );
        int d = calendar.get( Calendar.DAY_OF_MONTH );
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        Calendar calendarEndTime = Calendar.getInstance();

        int hEndDate = calendarEndTime.get(Calendar.HOUR_OF_DAY);
        int minEndDate = calendarEndTime.get(Calendar.MINUTE);

        mDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //C'est au niveau du onDateSet que nous récupérons les entrées de l'utilisateur et qu'il faut les set à notre calendar pour pouvoir exploiter les valeurs
                        //set value to calendar
                        calendar.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                        calendar.set(Calendar.MONTH, view.getMonth());
                        calendar.set(Calendar.YEAR, view.getYear());
                        calendarEndTime.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                        calendarEndTime.set(Calendar.MONTH, view.getMonth());
                        calendarEndTime.set(Calendar.YEAR, view.getYear());

                        initDuration( calendarEndTime, calendar );
                    }
                };
                // valorisation de mDatePickerDialog par l'instance de DatePickerDialog avec en paramètre l'activité courrante, le dateSetListner et l'année, le mois et le jour
                mDatePickerDialog = new DatePickerDialog( BookingActivity.this, dateSetListener, y, m, d );
                mDatePickerDialog.getDatePicker().setMinDate( new Date().getTime() );
                mDatePickerDialog.show();
            }
        } );

        mDate.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                dateEdited = true;

                //initDuration( calendarEndTime, calendar );
            }
        } );

        mHour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, view.getHour());
                        calendar.set(Calendar.MINUTE, view.getMinute());
                        initDuration( calendarEndTime, calendar );
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this, timeSetListener, h, min, true);
                timePickerDialog.show();
            }
        } );

        mHour.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                hourEdited = true;

            }
        } );

        mEndDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendarEndTime.set(Calendar.HOUR_OF_DAY, view.getHour());
                        calendarEndTime.set(Calendar.MINUTE, view.getMinute());
                        initDuration( calendarEndTime, calendar );
                        if (duration <= 0) {
                            Toast.makeText( getApplicationContext(), getApplicationContext().getString(R.string.later_end_date_than_start_date), Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this, timeSetListener, hEndDate, minEndDate, true);
                timePickerDialog.show();
            }
        });
        this.mEndDate.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                   durationEdited = true;
                   //initDuration( calendarEndTime, calendar );
            }
        } );
    }

    public String formatDate(Long dateMilli) {
        String format = "MMM dd.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
        return simpleDateFormat.format( dateMilli );
    }

    public String formatHour(Long timeMilli) {
        String format = "kk:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
        return simpleDateFormat.format(timeMilli);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected( item );
    }

    @OnClick(R.id.book)
    public void setMeetingToBook() {
        List<User> userListPicked;
        String eMailListPicked = mMail.getText().toString();
        userListPicked = mApiService.getUserListMeeting(eMailListPicked);
                //mettre un while plutôt qu'un fort lorsqu'on veut utiliser break
        List<Meeting> meetingList = mApiService.getMeetingList(false);


        boolean freeRoomCrenel = true;
        if(endDatePickedMilli != null && startDatePickedMilli != null && mMeetingRoom != null) {
            do {
                for (Meeting meeting : meetingList) {
                    crenelIsFree = (endDatePickedMilli <= meeting.getDate().getTime() || startDatePickedMilli >= meeting.getEndDate().getTime());
                    roomIsTaken = mMeetingRoom.getSelectedItem().toString().equals( meeting.getRoom().getRoomName() );
                    if (!crenelIsFree && roomIsTaken) {
                        freeRoomCrenel = false;
                    }
                }
            } while (!crenelIsFree && roomIsTaken);
        }
        if (!dateEdited) {
            mDate.requestFocus();
            Toast.makeText(this, getApplicationContext().getString(R.string.date_not_eddited), Toast.LENGTH_SHORT).show();
        } else if (!hourEdited) {
            mHour.requestFocus();
            Toast.makeText(this, getApplicationContext().getString(R.string.hour_not_eddited), Toast.LENGTH_SHORT).show();
        } else if (!durationEdited){
            mEndDate.requestFocus();
            Toast.makeText(this, getApplicationContext().getString(R.string.end_date_not_eddited), Toast.LENGTH_SHORT).show();
        } else if (!mailEdited ) {
            mMail.requestFocus();
            Toast.makeText( this, getApplicationContext().getString( R.string.mail_not_eddited ), Toast.LENGTH_SHORT ).show();
        } else if(userListPicked == null) {
            mMail.requestFocus();
            Toast.makeText( this, "You have to select existing user", Toast.LENGTH_SHORT ).show();
        } else if (!subjectEdited) {
            mSubject.requestFocus();
            Toast.makeText(this, getApplicationContext().getString(R.string.subject_not_eddited), Toast.LENGTH_SHORT).show();
        } else if (endDatePickedMilli - startDatePickedMilli <= 0) {
            mEndDate.requestFocus();
            Toast.makeText( this, getApplicationContext().getString(R.string.later_end_date_than_start_date), Toast.LENGTH_SHORT ).show();
        } else if (mMeetingRoom.getSelectedItem().toString() == "Chose room") {
            mMeetingRoom.requestFocus();
            Toast.makeText( this, getApplicationContext().getString(R.string.meeting_room_not_eddited), Toast.LENGTH_SHORT ).show();
        }
        else if (!freeRoomCrenel) {
            Toast.makeText( this, "Pick an other room", Toast.LENGTH_SHORT ).show();
            mMeetingRoom.requestFocus();
        }
         else {
            Meeting meetingToBook = new Meeting(
                    new Date( startDatePickedMilli ),
                    new Date(endDatePickedMilli),
                    new Room (mMeetingRoom.getSelectedItemPosition(), mMeetingRoom.getSelectedItem().toString()),
                    //(mMeetingRoom.getSelectedItemId() ne marche pas)
                    mSubject.getText().toString(),
                    userListPicked, false
            );
            mApiService.bookMeeting( meetingToBook );
            Toast.makeText( this,    " Date : " + formatDate(startDatePickedMilli) + " ; Start : " + formatHour( startDatePickedMilli ) + " ; End : " + formatHour( endDatePickedMilli ), Toast.LENGTH_SHORT ).show();
            finish();
      }
    }

    public static void navigate(FragmentActivity activity) {
        Intent intent = new Intent(activity, BookingActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public void initDuration(Calendar calendarEndTime, Calendar calendar) {
        startDatePickedMilli = calendar.getTimeInMillis();
        endDatePickedMilli = calendarEndTime.getTimeInMillis();

        datePickedFormated = formatDate( startDatePickedMilli );
        timePickedFormated = formatHour( startDatePickedMilli );
        endTimePickedFormated = formatHour( endDatePickedMilli );

        duration = (endDatePickedMilli - startDatePickedMilli) / 60 / 1000;
        String textDuration = duration.toString();

        mDate.setText( datePickedFormated );
        mHour.setText(timePickedFormated);
        mEndDate.setText(endTimePickedFormated);

        if (duration <= 0) {
            textDuration = getApplicationContext().getString( R.string.error );
        }
        mResumDate.setText( "Date : " + datePickedFormated + " Start : " + timePickedFormated + " End : " + endTimePickedFormated + "           Time : " + textDuration );
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition( position ).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    // Mettre à jour la liste du spinner ou indiquer à l'utilisateur les salles disponiblent au crénau choisi

    //public void removeRoomSpinner (String room) {

    //    meetingRoomArrayList.remove(room);

    //}
    //public void updateSpinner() {
    //List<Meeting> meetingList = mApiService.getMeetingList();

    //    for (Meeting meeting : meetingList) {
    //        //crenelIsFree = (endDatePickedMilli <= meeting.getDate().getTime() || startDatePickedMilli >= meeting.getEndDate().getTime());
    //        //roomIsTaken = mMeetingRoom.getSelectedItem().toString().equals( meeting.getPlace() );
    //        boolean crenelIsNotFree = (meeting.getDate().getTime() <= endDatePickedMilli && meeting.getEndDate().getTime() >= startDatePickedMilli  );
    //        if (crenelIsNotFree) {
    //            removeRoomSpinner( meeting.getPlace() );
    //        }
    //    }
    //}
   // boolean crenelIsNotFree = (meeting.getDate().getTime() <= endDatePickedMilli && meeting.getEndDate().getTime() >= startDatePickedMilli  );

}