package com.app.smartuni;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.app.smartuni.adapters.AddMarksAdapter;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.AddMarks;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.app.smartuni.AddMarksActivityPermissionsDispatcher.openFilePickerWithPermissionCheck;

@RuntimePermissions
public class AddMarksActivity extends BaseActivity {

  @BindView(R.id.recycle_marks) RecyclerView mRecycleView;
  @BindView(R.id.text_empty_recycle) TextView mEmptyRecycleView;
  private AddMarksAdapter mAddMarksAdapter;
  private ArrayList<Uri> docPaths = new ArrayList<>();
  private ArrayList<AddMarks> mMarks = new ArrayList<>();
  private Integer mResults = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_marks);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ButterKnife.bind(this);
    mAddMarksAdapter = new AddMarksAdapter();
    mRecycleView.setAdapter(mAddMarksAdapter);
    mRecycleView.setHasFixedSize(true);
    mRecycleView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.add_marks, menu);
    return true;
  }

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.new_game:
        openFilePickerWithPermissionCheck(this);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @OnClick({ R.id.fab_save })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.fab_save:
        saveData();
        break;
    }
  }

  private void saveData() {
    if (mMarks.size() > 0) {
      showProgress();
      saveToDb();
      hideProgress();
    } else {
      showWarning("No Marks Data To Save!");
    }
  }

  private void saveToDb() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Connection connection = DbConnection.getConnect();
          Statement mStatement = connection.createStatement();
          for (int r = 0; r < mMarks.size(); r++) {
            String mquery =
                "INSERT INTO marks (marks,course_idcourse,user_iduser) VALUES ('"
                    +
                    mMarks.get(r).getmMarks()
                    + "','"
                    +
                    mMarks.get(r).getmCourseId()
                    + "','"
                    +
                    mMarks.get(r).getmStudentId()
                    + "')";
            System.out.println("----" + mquery);
            mResults = mStatement.executeUpdate(mquery);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                if (mResults == 1) {
                } else {
                  showError("Failed To Add Marks");
                }
              }
            });
          }

          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              hideProgress();
              showSuccess("Successful!");
              mMarks = new ArrayList<>();
              mAddMarksAdapter.clearItems();
              setData();
            }
          });
        } catch (SQLException e) {
          hideProgress();
          e.printStackTrace();
        }
      }
    }).start();
  }

  @Override public void onRequestPermissionsResult(int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    AddMarksActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
        grantResults);
  }

  @NeedsPermission({
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
  })
  public void openFilePicker() {
    String[] mExcel = { ".xls" };
    FilePickerBuilder.getInstance()
        .setMaxCount(1)
        .setActivityTheme(R.style.AppThemeFullScreen)
        .addFileSupport("Excel", mExcel)
        .setActivityTitle("Select a Excel File")
        .enableCameraSupport(false)
        .enableSelectAll(false)
        .sortDocumentsBy(SortingTypes.name)
        .withOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .pickFile(this);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case FilePickerConst.REQUEST_CODE_DOC:
        if (resultCode == Activity.RESULT_OK && data != null) {
          ArrayList<Uri> dataList =
              data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
          if (dataList != null) {
            docPaths = new ArrayList<>();
            docPaths.addAll(dataList);
            Log.e("**********", docPaths.get(0).getPath());
            readExcel();
          }
        }
        break;
    }
  }

  public void readExcel() {
    showProgress();
    //InputStream stream = getResources().openRawResource(R.raw.test1);
    try {
      InputStream stream = getContentResolver().openInputStream(docPaths.get(0));
      //InputStream stream = new FileInputStream(docPaths.get(0).toFile(0));
      XSSFWorkbook workbook = new XSSFWorkbook(stream);
      XSSFSheet sheet = workbook.getSheetAt(0);
      int rowsCount = sheet.getPhysicalNumberOfRows();
      FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
      mMarks = new ArrayList<AddMarks>();
      for (int r = 1; r < rowsCount; r++) {
        Row row = sheet.getRow(r);
        int cellsCount = row.getPhysicalNumberOfCells();
        String mStudentId = null;
        String mCourseId = null;
        String mGrade = null;
        String mCourseName = null;
        for (int c = 0; c < cellsCount; c++) {
          String value = getCellAsString(row, c, formulaEvaluator);

          if (c == 0) {
            mStudentId = value;
            Log.e("---student_id-----", value);
          }
          if (c == 1) {
            mCourseId = value;
            Log.e("---subject_id-----", value);
          }
          if (c == 2) {
            mGrade = value;
            Log.e("---grade-----", value);
          }
          if (c == 3) {
            mCourseName = value;
            Log.e("---grade-----", value);
          }
        }
        mMarks.add(new AddMarks(mCourseId, mStudentId, mGrade, mCourseName));
      }
      setData();
      hideProgress();
    } catch (Exception e) {
      hideProgress();
      e.printStackTrace();
    }
  }

  protected String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
    String value = "";
    try {
      Cell cell = row.getCell(c);
      CellValue cellValue = formulaEvaluator.evaluate(cell);
      switch (cellValue.getCellType()) {
        case Cell.CELL_TYPE_BOOLEAN:
          value = "" + cellValue.getBooleanValue();
          break;
        case Cell.CELL_TYPE_NUMERIC:
          double numericValue = cellValue.getNumberValue();
          if (HSSFDateUtil.isCellDateFormatted(cell)) {
            double date = cellValue.getNumberValue();
            SimpleDateFormat formatter =
                new SimpleDateFormat("dd/MM/yy");
            value = formatter.format(HSSFDateUtil.getJavaDate(date));
          } else {
            value = "" + numericValue;
          }
          break;
        case Cell.CELL_TYPE_STRING:
          value = "" + cellValue.getStringValue();
          break;
        default:
      }
    } catch (NullPointerException e) {
      /* proper error handling should be here */
      e.printStackTrace();
    }
    return value;
  }

  private void setData() {
    if (mMarks.size() > 0) {
      mEmptyRecycleView.setVisibility(View.GONE);
      mRecycleView.setVisibility(View.VISIBLE);
      mAddMarksAdapter.setItems(mMarks);
    } else {
      mEmptyRecycleView.setVisibility(View.VISIBLE);
      mRecycleView.setVisibility(View.GONE);
    }
  }
}
