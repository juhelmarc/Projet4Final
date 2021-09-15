 package fr.marc.mareu.ui.meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.marc.mareu.DI.DI;
import fr.marc.mareu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.marc.mareu.dataservice.MeetingApiService;
import fr.marc.mareu.events.DeleteMeetingEvent;
import fr.marc.mareu.model.Meeting;
import fr.marc.mareu.ui.booking.BookingActivity;

 //TODO : singlechoicepicker pour les rooms
//TODO : date datepicker exploiter le format date
public class MeetingActivity extends AppCompatActivity    {

    private MeetingApiService mApiService;
    private List<Meeting> mMeetingList;
    private MyMeetingRecyclerViewAdapter mMeetingRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String[] room;
    private String dateFilter;
    private boolean isFiltered;

    @BindView(R.id.toolbar2)
    Toolbar mToolbar;
    @BindView( R.id.recyclerview )
    RecyclerView mRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_meeting );
        ButterKnife.bind( this );
        setSupportActionBar( mToolbar );
        mApiService = DI.getMeetingApiService();
        mMeetingList = mApiService.getMeetingList(false);
        mLinearLayoutManager = new LinearLayoutManager( this );
        mLinearLayoutManager.setOrientation( RecyclerView.VERTICAL );
        mRecyclerView.setLayoutManager( mLinearLayoutManager );
        mMeetingRecyclerViewAdapter = new MyMeetingRecyclerViewAdapter( mMeetingList );
        mRecyclerView.setAdapter( mMeetingRecyclerViewAdapter );
    }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu( menu );
       getMenuInflater().inflate( R.menu.menu_filter, menu );
       return true;
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       room = mApiService.getRoomNameList().toArray( new String[0] );
       room[room.length-1 ] = "Cancel";

       if(item.getItemId() == R.id.room_filter) {
           AlertDialog.Builder builder = new AlertDialog.Builder( this );
           builder.setTitle( "Chose a room" )
                   .setSingleChoiceItems( room, -1, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           if(room[which] != "Cancel") {
                               mApiService.applyRoomFilter( room[which] );
                               dialog.dismiss();
                               Toast.makeText( getApplicationContext(), "taille liste = " + mApiService.getMeetingList( true ).size(), Toast.LENGTH_SHORT ).show();
                               isFiltered = true;
                               initRecyclerview( true );
                           }
                           dialog.dismiss();
                       }
                   } ).show();

       }
       if(item.getItemId() == R.id.date_filter) {

           initDatePicker( item );
       }
       if(item.getItemId() == R.id.clear_filter) {
           isFiltered = false;
           initRecyclerview( false );

       }
       return false;
    }
   @OnClick(R.id.floatingActionButton2)
   void startBookingActivity() {
       BookingActivity.navigate( this );

   }
   private void initDatePicker(MenuItem item) {
       Calendar calendar = Calendar.getInstance();
       int y = calendar.get( Calendar.YEAR );
       int m = calendar.get( Calendar.MONTH );
       int d = calendar.get( Calendar.DAY_OF_MONTH );

       item.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                       calendar.set( Calendar.DAY_OF_MONTH, view.getDayOfMonth() );
                       calendar.set( Calendar.MONTH, view.getMonth() );
                       calendar.set( Calendar.YEAR, view.getYear() );

                       dateFilter = formatDate( calendar.getTimeInMillis());
                       mApiService.applyDateFilter( dateFilter ) ;
                       Toast.makeText( getApplicationContext(), "taille liste = " + mApiService.getMeetingList( true ).size(), Toast.LENGTH_SHORT ).show();
                       isFiltered = true;
                       initRecyclerview( true );
                   }
               };
               DatePickerDialog datePickerDialog = new DatePickerDialog( MeetingActivity.this, dateSetListener, y, m, d );
               datePickerDialog.show();
               return true;
           }
       });
   }
   private String formatDate(Long dateMilli) {
       String format = "MMM dd.yyyy";
       SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
       return simpleDateFormat.format( dateMilli );
   }
   private void initRecyclerview(boolean isFiltered) {
      mMeetingRecyclerViewAdapter = new MyMeetingRecyclerViewAdapter( mApiService.getMeetingList( isFiltered) );
      mRecyclerView.setAdapter( mMeetingRecyclerViewAdapter );
      //mMeetingRecyclerViewAdapter.notifyDataSetChanged();
      }
      /**
    * param event
    */
   @Subscribe
   public void onDeleteMeetingEvent(DeleteMeetingEvent event) {
       mApiService.deleteMeeting(event.meeting);
       initRecyclerview( isFiltered );
   }
      @Override
   public void onResume() {
       super.onResume();
   }
      @Override
   public void onStart() {
       super.onStart();
       EventBus.getDefault().register( this );

   }
      @Override
   public void onStop() {
       super.onStop();
       EventBus.getDefault().unregister( this );
   }

    //Todo : récupérer une référence de MyMeetingRecuclerViewAdapter + .notifyDataSetChange et ajouter la nouvelle liste dans l'adapter. Créer une fonction pour insérer une nouvelle liste

}