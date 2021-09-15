package fr.marc.mareu.ui.meeting;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.marc.mareu.DI.DI;
import fr.marc.mareu.R;
import fr.marc.mareu.dataservice.MeetingApiService;
import fr.marc.mareu.events.DeleteMeetingEvent;
import fr.marc.mareu.model.Meeting;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyMeetingRecyclerViewAdapter extends RecyclerView.Adapter<MyMeetingRecyclerViewAdapter.ViewHolder> {

    private MeetingApiService mApiService;
    private final List<Meeting> mMeetings;
    private int day;
    private Date date;
    private Date endDate;
    //ImageView day
    int[]  imagesOfDays = {R.drawable.day_lundi, R.drawable.day_mardi, R.drawable.day_mercredi, R.drawable.day_jeudi, R.drawable.day_vendredi, R.drawable.day_vendredi, R.drawable.day_vendredi };

    public MyMeetingRecyclerViewAdapter(List<Meeting> items) {
        mMeetings = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.item_meeting, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Meeting meeting = mMeetings.get( position );
        mApiService = DI.getMeetingApiService();
        date = meeting.getDate();
        endDate = meeting.getEndDate();
        String format = "u/kk/mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
        String formattedDate = simpleDateFormat.format(date);
        String [] splitDate = formattedDate.split("/");
        day = Integer.parseInt( splitDate[0] );

        holder.mDay.setBackgroundResource( imagesOfDays[day-1] );
        holder.mHour.setText(" - " + splitDate[1] + "h" + splitDate[2] + " - ");
        holder.mRoom.setText( meeting.getRoom().getRoomName() );
        holder.mSubject.setText( meeting.getSubjectMeeting() );
        holder.mMails.setText( mApiService.getEmailInThisUserList( meeting.getUser()).toString().replace("[", "").replace( "]", "" ) );

        holder.mDeleteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post( new DeleteMeetingEvent( meeting ) );
            }
        } );
    }
    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        ImageView mDay;
        @BindView(R.id.place)
        TextView mRoom;
        @BindView(R.id.hour)
        TextView mHour;
        @BindView(R.id.subject)
        TextView mSubject;
        @BindView(R.id.mails)
        TextView mMails;
        @BindView(R.id.delete)
        ImageView mDeleteButton;

        public ViewHolder(View view) {
            super( view );
            ButterKnife.bind( this, view );


        }
    }
}