package com.cs446.group18.timetracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group18.timetracker.R;
import com.cs446.group18.timetracker.entity.Event;
import com.cs446.group18.timetracker.model.TimeLineModel;
import com.cs446.group18.timetracker.ui.TimeLineMarker;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder> {
	private List<TimeLineModel> mDataSet;
	private int i = 0;

	public TimeLineAdapter() {
		// Required constructor
	}

	public void setItems(List<TimeLineModel> models) {
		mDataSet = models;
	}

	@Override
	public int getItemViewType(int position) {
		final int size = mDataSet.size() - 1;
		if (size == 0)
			return ItemType.ATOM;
		else if (position == 0)
			return ItemType.START;
		else if (position == size)
			return ItemType.END;
		else return ItemType.NORMAL;
	}

	@Override
	public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		TimeLineViewHolder viewHolder = new TimeLineViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_timeline, viewGroup, false), viewType);
		viewHolder.setMarkerDrawable();
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(TimeLineViewHolder timeLineViewHolder, int i) {

		timeLineViewHolder.mName.setText(mDataSet.get(i).getEventName());
		timeLineViewHolder.mAge.setText(mDataSet.get(i).getTime());

	}

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	public class ItemType {
		public final static int NORMAL = 0;

		public final static int HEADER = 1;
		public final static int FOOTER = 2;

		public final static int START = 4;
		public final static int END = 8;
		public final static int ATOM = 16;
	}


	public class TimeLineViewHolder extends RecyclerView.ViewHolder {
		private TextView mName;
		private TextView mAge;

		public TimeLineViewHolder(View itemView, int type) {
			super(itemView);

			mName = (TextView) itemView.findViewById(R.id.item_time_line_txt);
			mAge = (TextView) itemView.findViewById(R.id.item_time_line_txt2);

			TimeLineMarker mMarker = (TimeLineMarker) itemView.findViewById(R.id.item_time_line_mark);
			if (type == ItemType.ATOM) {
				mMarker.setBeginLine(null);
				mMarker.setEndLine(null);
			} else if (type == ItemType.START) {
				mMarker.setBeginLine(null);
			} else if (type == ItemType.END) {
				mMarker.setEndLine(null);
			}

		}

		public void setMarkerDrawable() {
			ImageView view = (ImageView) itemView.findViewById(R.id.timeline_event_icon);
			view.setBackgroundResource(mDataSet.get(i).getEvent().getIcon());
			i = i+1;
		}

	}

}
