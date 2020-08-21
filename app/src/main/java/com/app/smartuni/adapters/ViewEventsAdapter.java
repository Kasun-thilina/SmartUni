package com.app.smartuni.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.smartuni.R;
import com.app.smartuni.models.Events;
import java.util.ArrayList;

public class ViewEventsAdapter extends RecyclerView.Adapter<ViewEventsAdapter.ViewHolder> {
  private ArrayList<Events> mEventsList;

  public ViewEventsAdapter() {
    this.mEventsList = new ArrayList<>();
  }

  public void setItems(ArrayList<Events> mTimeTableEvents) {
    this.mEventsList = mTimeTableEvents;
    notifyDataSetChanged();
  }

  public void clearItems() {
    this.mEventsList.clear();
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View listItem = layoutInflater.inflate(R.layout.item_view_event, parent, false);
    return new ViewHolder(listItem);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final Events mEvents = mEventsList.get(position);
    holder.mEventTitle.setText(mEvents.getmEventTitle());
    holder.mEventDate.setText(mEvents.getmEventDate());
    holder.mEventTime.setText(mEvents.getmEventTime());
  }

  @Override
  public int getItemCount() {
    return mEventsList.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mEventTitle, mEventDate, mEventTime;

    public ViewHolder(View itemView) {
      super(itemView);
      this.mEventTitle = itemView.findViewById(R.id.text_event_title);
      this.mEventDate = itemView.findViewById(R.id.text_event_date);
      this.mEventTime = itemView.findViewById(R.id.text_event_time);
    }
  }
}
