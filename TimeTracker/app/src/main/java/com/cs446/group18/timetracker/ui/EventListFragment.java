package com.cs446.group18.timetracker.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group18.timetracker.R;
import com.cs446.group18.timetracker.adapter.EventListAdapter;
import com.cs446.group18.timetracker.adapter.TimeEntryListAdapter;
import com.cs446.group18.timetracker.entity.Event;
import com.cs446.group18.timetracker.entity.TimeEntry;
import com.cs446.group18.timetracker.utils.InjectorUtils;
import com.cs446.group18.timetracker.vm.EventListViewModelFactory;
import com.cs446.group18.timetracker.vm.EventViewModel;
import com.cs446.group18.timetracker.vm.TimeEntryListViewModelFactory;
import com.cs446.group18.timetracker.vm.TimeEntryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment{

    private List<Event> events = new ArrayList<>();
    private List<TimeEntry> timeEntries = new ArrayList<>();
    RecyclerView recyclerView;
    private TextView textViewEmpty;

    // Let's try
    RecyclerView timeEntryListRecyclerView;

    private RelativeLayout expandableCardView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View eventListView = inflater.inflate(R.layout.fragment_event_list, container, false);
        EventListAdapter eventListAdapter = new EventListAdapter(events);

        EventListViewModelFactory factory = InjectorUtils.provideEventListViewModelFactory(getActivity());
        EventViewModel viewModel = new ViewModelProvider(this, factory).get(EventViewModel.class);

        textViewEmpty = eventListView.findViewById(R.id.empty_event_list);
        recyclerView = eventListView.findViewById(R.id.event_list);
        recyclerView.setAdapter(eventListAdapter);

        // Try
//        View listItemEvent = inflater.inflate(R.layout.list_item_event, container, false);
//        timeEntryListRecyclerView = (RecyclerView) listItemEvent.findViewById(R.id.expandable);
//        timeEntryListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        TimeEntryListAdapter timeEntryListAdapter = new TimeEntryListAdapter();
//        timeEntryListRecyclerView.setAdapter(timeEntryListAdapter);

        // Try Ends


        // Add New Event Action
        FloatingActionButton buttonAddEvent = eventListView.findViewById(R.id.button_add_event);
        buttonAddEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                View promptView = inflater.inflate(R.layout.prompt_add_event, container, false);

                final EditText eventNameText = promptView.findViewById(R.id.event_name);
                final EditText eventDescriptionText = promptView.findViewById(R.id.event_description);
                builder.setView(promptView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String eventName = eventNameText.getText().toString();
                                String eventDescription = eventDescriptionText.getText().toString();
                                try {
                                    viewModel.insert(new Event(1, eventName, eventDescription));
                                    Toast.makeText(eventListView.getContext(), "Add new event: " + eventName, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    dialog.dismiss();
                                    AlertDialog.Builder errorBuilder = new AlertDialog.Builder(getActivity());
                                    errorBuilder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    errorBuilder.setTitle("Error");
                                    errorBuilder.setMessage("Please enter a valid input");
                                    AlertDialog error = errorBuilder.create();
                                    error.show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        // Swipe Right to Delete Event & Swipe Left to Update
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if( direction == ItemTouchHelper.RIGHT){
                    viewModel.delete(eventListAdapter.getEventAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(eventListView.getContext(), "Event Deleted", Toast.LENGTH_SHORT).show();
                }else if( direction == ItemTouchHelper.LEFT){
                    long eventId = eventListAdapter.getEventAt(viewHolder.getAdapterPosition()).getEventId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                    View promptView = inflater.inflate(R.layout.prompt_add_event, container, false);


                    final EditText eventNameText = promptView.findViewById(R.id.event_name);
                    final EditText eventDescriptionText = promptView.findViewById(R.id.event_description);
                    builder.setView(promptView)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int pos = viewHolder.getAdapterPosition();
                                    String eventName = eventNameText.getText().toString();
                                    String eventDescription = eventDescriptionText.getText().toString();
                                    try {
                                        Event event = new Event(1, eventName, eventDescription);
                                        event.setEventId(eventId);
                                        viewModel.update(event);
                                        Toast.makeText(eventListView.getContext(), "Event Update", Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        dialog.dismiss();
                                        AlertDialog.Builder errorBuilder = new AlertDialog.Builder(getActivity());
                                        errorBuilder.setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        errorBuilder.setTitle("Error");
                                        errorBuilder.setMessage("Please enter a valid input");
                                        AlertDialog error = errorBuilder.create();
                                        error.show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        }).attachToRecyclerView(recyclerView);


//        subscribeUI(eventListAdapter, timeEntryListAdapter);
        subscribeUI(eventListAdapter);
        return eventListView;
    }

    private void subscribeUI(EventListAdapter eventListAdapter) {
        EventListViewModelFactory factory = InjectorUtils.provideEventListViewModelFactory(getActivity());
        EventViewModel viewModel = new ViewModelProvider(this, factory).get(EventViewModel.class);
        viewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable List<Event> events) {
                Log.d("observer", "onChanged: Events");
                if (events != null && !events.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewEmpty.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    textViewEmpty.setVisibility(View.VISIBLE);
                }
                eventListAdapter.setEvents(events);
            }
        });



        // Time Entries
//        TimeEntryListViewModelFactory timeEntryListViewModelFactory = InjectorUtils.provideTimeEntryListViewModelFactory((getActivity()));
//        TimeEntryViewModel timeEntryViewModel = new ViewModelProvider(this, timeEntryListViewModelFactory).get(TimeEntryViewModel.class);
//        timeEntryViewModel.getTimeEntries().observe(getViewLifecycleOwner(), new Observer<List<TimeEntry>>() {
//            @Override
//            public void onChanged(List<TimeEntry> timeEntries) {
//                Log.d("Yoo", timeEntries.get(0).getDurationStr());
//                timeEntryListAdapter.setTimeEntries(timeEntries);
//                Log.d("Hey", timeEntries.get(0).getDurationStr());
//            }
//        });
    }


}
