package com.app.smartuni.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.smartuni.R;
import com.app.smartuni.models.AddMarks;
import java.util.ArrayList;

public class AddMarksAdapter extends RecyclerView.Adapter<AddMarksAdapter.ViewHolder> {
  private ArrayList<AddMarks> mMarksList;
  private Boolean isShowMarks = false;

  public AddMarksAdapter() {
    this.mMarksList = new ArrayList<>();
  }

  public void setItems(ArrayList<AddMarks> mTimeTableEvents) {
    this.mMarksList = mTimeTableEvents;
    notifyDataSetChanged();
  }

  public void setIsShowMarks(Boolean mIsShowMarks) {
    this.isShowMarks = mIsShowMarks;
  }

  public void clearItems() {
    this.mMarksList.clear();
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View listItem = layoutInflater.inflate(R.layout.item_add_marks, parent, false);
    return new ViewHolder(listItem);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final AddMarks mAddMarks = mMarksList.get(position);
    holder.mCourseName.setText(mAddMarks.getmCourseName());
    holder.mMarks.setText(mAddMarks.getmMarks());
    if (isShowMarks) {
      holder.mStudentId.setVisibility(View.INVISIBLE);
      holder.mStudentIdTitle.setVisibility(View.INVISIBLE);
    } else {
      holder.mStudentId.setText(mAddMarks.getmStudentId());
    }
  }

  @Override
  public int getItemCount() {
    return mMarksList.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mCourseName, mStudentId, mMarks, mStudentIdTitle;

    public ViewHolder(View itemView) {
      super(itemView);
      this.mCourseName = itemView.findViewById(R.id.text_course_name);
      this.mStudentId = itemView.findViewById(R.id.text_student_id);
      this.mStudentIdTitle = itemView.findViewById(R.id.textView10);
      this.mMarks = itemView.findViewById(R.id.text_course_grade);
    }
  }
}
