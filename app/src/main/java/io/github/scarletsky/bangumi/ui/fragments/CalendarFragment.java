package io.github.scarletsky.bangumi.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;
import java.util.Calendar;

import io.github.scarletsky.bangumi.R;
import io.github.scarletsky.bangumi.adapters.FragmentAdapter;
import io.github.scarletsky.bangumi.api.ApiManager;
import io.github.scarletsky.bangumi.api.models.Schedule;
import io.github.scarletsky.bangumi.events.GetCalendarEvent;
import io.github.scarletsky.bangumi.utils.BusProvider;
import io.github.scarletsky.bangumi.utils.ToastManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scarlex on 15-7-2.
 */
public class CalendarFragment extends BaseToolbarFragment {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private List<Schedule> mCalendars;
    private int currentPosition = 0;


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    private int getColorByPosition(int position){
        int color;
        Log.d("s",Integer.toString(position));
        switch (position) {
            case 0:  color = ContextCompat.getColor(getContext(), R.color.calendar_sunday);
                break;
            case 1:  color = ContextCompat.getColor(getContext(), R.color.calendar_monday);
                break;
            case 2:  color = ContextCompat.getColor(getContext(), R.color.calendar_tuesday);
                break;
            case 3:  color = ContextCompat.getColor(getContext(), R.color.calendar_wednesday);
                break;
            case 4:  color = ContextCompat.getColor(getContext(), R.color.calendar_thursday);
                break;
            case 5:  color = ContextCompat.getColor(getContext(), R.color.calendar_friday);
                break;
            case 6:  color = ContextCompat.getColor(getContext(), R.color.calendar_saturday);
                break;
            default: color = ContextCompat.getColor(getContext(), R.color.primary);
                break;
        }
        return color;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK); // 2 for monday, starts with 1
        int position = convertDayToCalendarPosition(day);


        FragmentAdapter pagerAdapter = new FragmentAdapter(
                getActivity(),
                getActivity().getSupportFragmentManager(),
                FragmentAdapter.PagerType.CALENDAR);

        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs_wrapper).findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) getView().findViewById(R.id.pager);

        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);

        pager.setCurrentItem(position, true);
        tabs.setIndicatorColor(getColorByPosition(position));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (currentPosition != position) {

                    currentPosition = position;

                    if (mCalendars != null) {
                        BusProvider.getInstance().post(new GetCalendarEvent(mCalendars));
                    }

                }
            }

            @Override
            public void onPageSelected(int position) {
                tabs.setIndicatorColor(getColorByPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });

        ApiManager.getBangumiApi().listCalendar(new Callback<List<Schedule>>() {
            @Override
            public void success(List<Schedule> calendars, Response response) {
                mCalendars = calendars;
                BusProvider.getInstance().post(new GetCalendarEvent(mCalendars));
            }

            @Override
            public void failure(RetrofitError error) {
                ToastManager.show(getActivity(), getString(R.string.toast_collection_update_successfully));

            }
        });
    }

    @Override
    protected void setToolbarTitle() {
        getToolbar().setTitle(getString(R.string.title_calendar));
    }

    private int convertDayToCalendarPosition(int day){
        int position = 0;
        if (day == 0){
            position = 5;
        }
        else if(day == 1){
            position = 6;
        }
        else{
            position = day - 2;
        }
        return position;
    }
}
