package com.app.smartuni.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.smartuni.R;
import com.app.smartuni.models.TimeTableEvent;
import java.util.ArrayList;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {
  private ArrayList<TimeTableEvent> mTimeTableEvents;

  public TimeTableAdapter() {
    this.mTimeTableEvents = new ArrayList<>();
  }

  public void setItems(ArrayList<TimeTableEvent> mTimeTableEvents) {
    this.mTimeTableEvents = mTimeTableEvents;
    notifyDataSetChanged();
  }

  public void clearItems() {
    this.mTimeTableEvents.clear();
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View listItem = layoutInflater.inflate(R.layout.item_time_table, parent, false);
    ViewHolder viewHolder = new ViewHolder(listItem);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final TimeTableEvent mTimeTableEvent = mTimeTableEvents.get(position);
    holder.mCourseId.setText(mTimeTableEvent.getmCourse());
    holder.mHall.setText(mTimeTableEvent.getmHall());
    holder.mLecturer.setText(mTimeTableEvent.getmLecturer());
  }

  @Override
  public int getItemCount() {
    return mTimeTableEvents.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mCourseId, mLecturer, mHall;

    public ViewHolder(View itemView) {
      super(itemView);
      this.mCourseId = itemView.findViewById(R.id.text_course_id);
      this.mLecturer = itemView.findViewById(R.id.text_lecturer);
      this.mHall = itemView.findViewById(R.id.text_hall);
    }
  }
}
