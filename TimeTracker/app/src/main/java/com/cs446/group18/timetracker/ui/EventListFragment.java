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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group18.timetracker.R;
import com.cs446.group18.timetracker.adapter.EventListAdapter;
import com.cs446.group18.timetracker.entity.Event;
import com.cs446.group18.timetracker.utils.InjectorUtils;
import com.cs446.group18.timetracker.vm.EventListViewModelFactory;
import com.cs446.group18.timetracker.vm.EventViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment implements EventListAdapter.OnEventListener{
    private static final String TAG = "Test";
    private List<Event> events = new ArrayList<>();
    RecyclerView recyclerView;
    private TextView textViewEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View eventListView = inflater.inflate(R.layout.fragment_event_list, container, false);
        EventListAdapter adapter = new EventListAdapter(events, this);
        EventListViewModelFactory factory = InjectorUtils.provideEventListViewModelFactory(getActivity());
        EventViewModel viewModel = new ViewModelProvider(this, factory).get(EventViewModel.class);

        textViewEmpty = eventListView.findViewById(R.id.empty_event_list);
        recyclerView = eventListView.findViewById(R.id.event_list);
        recyclerView.setAdapter(adapter);

        // Add New Event
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
                    viewModel.delete(adapter.getEventAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(eventListView.getContext(), "Event Deleted", Toast.LENGTH_SHORT).show();
                }else if( direction == ItemTouchHelper.LEFT){
                    long eventId = adapter.getEventAt(viewHolder.getAdapterPosition()).getEventId();
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


        subscribeUI(adapter);
        return eventListView;
    }

    private void subscribeUI(EventListAdapter adapter) {
        EventListViewModelFactory factory = InjectorUtils.provideEventListViewModelFactory(getActivity());
        EventViewModel viewModel = new ViewModelProvider(this, factory).get(EventViewModel.class);
        viewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable List<Event> events) {
                if (events != null && !events.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewEmpty.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    textViewEmpty.setVisibility(View.VISIBLE);
                }
                adapter.setEvents(events);
            }
        });
    }

    @Override
    public void onEventClick(int position) {

        // reference to the Event selected
//        events.get(position);
        
    }
}
