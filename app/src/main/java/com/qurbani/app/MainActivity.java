package com.qurbani.app;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 200;
    final Calendar myCalendar = Calendar.getInstance();
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Spinner spinnerVoucher, spinnerCowSerial, spinnerCowNo;
    ArrayAdapter<String> spinnerVoucherAdapter;
    ArrayAdapter<String> spinnerCowSerialAdapter;
    ArrayAdapter<String> spinnerCowNoAdapter;
    private List<String> voucherList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private List<String> cowNoList = new ArrayList<>();
    private List<String> cowNoList1 = new ArrayList<>();
    // fields of voucher view
    private TextView tvVoucherDate, tvQAP, tvVoucherMonthYear, tVReceivedBy, tvVoucherEndNo, etAuthorizedPerson;
    private EditText etQurbaniPlace, etAuthPersonContNo, etReceivedByContNo;
    // fields of person info
    private EditText etPersonName, etPersonContNo, etPaidBy, etPaidByContNo, etAddress;
    // fields of cow serial
    private EditText etCowNo, etTime, etDay, etTotalAmount, etAmountReceived;
    private TextView tvBalance, tvSharedAmount, tvCowShareNo;
    // fields of ref person
    private EditText etRefPersonName, etRefPersonContNo;

    private Button btnSubmit, btnPdf;

    int mMaxVoucherNo;
    private String mDate;
    private String mMonthYear;
    private String mQAP;
    private String mVoucherNo;
    private String mQurbaniPlace;
    private String mAuthorizedPerson;
    private String mAuthorizedPersonContNo;
    private String mReceivedBy;
    private String mReceiverContNo;
    private String mPersonName, mPersonContNo, mPaidBy, mPaidByContNo, mPersonAddress;
    private String mCowSerialNo, mQurbaniTime, mQurbaniDay;
    int mTotalAmount, mSharedAmount, mReceivedAmount, mBalance;
    private int mCowNo, mCowShareNo;
    private String mRefPersonName, mRefPersonContNo;
    Connection connect;
    LinearLayout layoutPdf;
    private Bitmap bitmap;

    private Intent intent;
    private SharedPrefClass prefClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefClass = new SharedPrefClass(this);
        intent = getIntent();
        if (intent != null) {
//            mReceivedBy = intent.getStringExtra("userId");
            mReceivedBy = prefClass.getReferralId();
        }
//        havePermissions(this);
        // checking our permissions.
        if (checkPermission()) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        connect = ConnectionHelper.CONN();

        voucherList.add("RS");
        ititViews();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("N");
        list.add("Z1");
        list.add("Z2");
        spinnerVoucher = findViewById(R.id.spinnerVoucher);
        spinnerVoucherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, voucherList);
        spinnerVoucher.setAdapter(spinnerVoucherAdapter);

        setSpinnerCowSerial();


    }

    private void setSpinnerCowSerial() {
        spinnerCowSerial = findViewById(R.id.spinnerCowSerial);
        spinnerCowSerialAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
        spinnerCowSerial.setAdapter(spinnerCowSerialAdapter);
        spinnerCowSerial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCowSerialNo = list.get(position);
                findCowRefNo(mCowSerialNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerCowNo(List<String> cowNoList1) {
        spinnerCowNo = findViewById(R.id.spinnerCowNo);
        spinnerCowNoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cowNoList1);
        spinnerCowNo.setAdapter(spinnerCowNoAdapter);
        spinnerCowNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int cowNo = Integer.parseInt(MainActivity.this.cowNoList1.get(position));
                mCowNo = Integer.parseInt(MainActivity.this.cowNoList1.get(position));
                findCowShareNo(cowNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void findCowRefNo(String mCowSerialNo) {
        cowNoList.clear();
        cowNoList1.clear();
        try {
            Statement statement = connect.createStatement();
            String query = "select cow_no from BookedCows where cow_serial = '" + mCowSerialNo + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                cowNoList.add(resultSet.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 30; i++) {
            if (!cowNoList.contains(String.valueOf(i))) {
                cowNoList1.add(String.valueOf(i));
            }
        }
        setSpinnerCowNo(cowNoList1);
    }


    private void ititViews() {
        // fields of voucher view
        btnPdf = findViewById(R.id.btnPdf);
        btnPdf.setOnClickListener(this);
        layoutPdf = findViewById(R.id.shareLayout);
        tvVoucherDate = findViewById(R.id.tvVoucherDate);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat df1 = new SimpleDateFormat("MM-yy", Locale.getDefault());
        String currentDate = df.format(c);
        String modifiedDate =df1.format(c);
//        mDate = currentDate;
        tvVoucherDate.setText(currentDate);

        tvQAP = findViewById(R.id.tvQAP);
        tvVoucherMonthYear = findViewById(R.id.tvVoucherMonthYear);
        String[] newDate = modifiedDate.split("-");
        mMonthYear = newDate[0] + "-" + newDate[1];
        tvVoucherMonthYear.setText(mMonthYear);
        tvVoucherEndNo = findViewById(R.id.tvVoucherEndNo);
        etQurbaniPlace = findViewById(R.id.etQurbaniPlace);
        etAuthorizedPerson = findViewById(R.id.etAuthorizedPerson);
        etAuthPersonContNo = findViewById(R.id.etAuthPersonContNo);
        tVReceivedBy = findViewById(R.id.etReceivedBy);
        tVReceivedBy.setText(mReceivedBy);
        etReceivedByContNo = findViewById(R.id.etReceivedByContNo);

        // fields of person info
        etPersonName = findViewById(R.id.etPersonName);
        etPersonContNo = findViewById(R.id.etPersonContNo);
        etPaidBy = findViewById(R.id.etPaidBy);
        etPaidByContNo = findViewById(R.id.etPaidByContNo);
        etAddress = findViewById(R.id.etAddress);

        // fields of cow serial

        tvCowShareNo = findViewById(R.id.tvCowShareNo);
        etTime = findViewById(R.id.etTime);
        etDay = findViewById(R.id.etDay);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        mTotalAmount = 140000;
        etTotalAmount.setText(mTotalAmount + "");
        mSharedAmount = mTotalAmount / 7;
        tvSharedAmount = findViewById(R.id.etSharedAmount);
        tvSharedAmount.setText(mSharedAmount + "");
        etAmountReceived = findViewById(R.id.etAmountReceived);
        tvBalance = findViewById(R.id.tvBalance);
        etTotalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTotalAmount = Integer.parseInt(String.valueOf(s));
                mSharedAmount = mTotalAmount / 7;
                tvSharedAmount.setText(mSharedAmount + "");

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etAmountReceived.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mReceivedAmount = Integer.parseInt(String.valueOf(s));
                    mBalance = mSharedAmount - mReceivedAmount;
                    tvBalance.setText(mBalance + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // fields of ref person
        etRefPersonName = findViewById(R.id.etRefPersonName);
        etRefPersonContNo = findViewById(R.id.etRefPersonContNo);

        String getQuery = "select MAX(vno) as max_vno from Receipts";
        try {
            Statement preparedStatement = connect.createStatement();
            ResultSet resultSet = preparedStatement.executeQuery(getQuery);
            while (resultSet.next()) {
                mMaxVoucherNo = Integer.parseInt(resultSet.getString(1)) + 1;
                Log.i("result", String.valueOf(mMaxVoucherNo));
                tvVoucherEndNo.setText(mMaxVoucherNo + "");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);


    }

    private void findCowShareNo(int cowNo) {
        String getShareNo = "select MAX(ShareNo) as max_share_no from Receipts where RefVoucher_No = '" + cowNo + "' and CowSerial = '" + mCowSerialNo + "'";
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(getShareNo);
            while (resultSet.next()) {
                if (resultSet.getString(1) == null) {
                    mCowShareNo = 1;
                    tvCowShareNo.setText("1");
                } else {
                    int shareNo = Integer.parseInt(resultSet.getString(1));
                    mCowShareNo = shareNo + 1;
                    tvCowShareNo.setText(mCowShareNo + "");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                submitDetails();
                break;
        }
    }

    private Bitmap LoadBitmap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void openPdf() {
        File file = new File("/sdcard/page.pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No Application for pdf view", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submitDetails() {
        try {
            mDate = tvVoucherDate.getText().toString().trim();
            mQAP = tvQAP.getText().toString().trim();
            mQurbaniPlace = etQurbaniPlace.getText().toString().trim();
            mAuthorizedPerson = etAuthorizedPerson.getText().toString().trim();
            mAuthorizedPersonContNo = etAuthPersonContNo.getText().toString().trim();
            mReceivedBy = tVReceivedBy.getText().toString().trim();
            mReceiverContNo = etReceivedByContNo.getText().toString().trim();
            mCowShareNo = Integer.parseInt(tvCowShareNo.getText().toString().trim());
            if (mCowSerialNo.equalsIgnoreCase("N") || mCowSerialNo.equalsIgnoreCase("Z2")) {
                mQurbaniDay = "2";
            } else {
                mQurbaniDay = "1";
            }
            mTotalAmount = Integer.parseInt(etTotalAmount.getText().toString().trim());
            mSharedAmount = Integer.parseInt(tvSharedAmount.getText().toString().trim());
            mRefPersonName = etRefPersonName.getText().toString().trim();
            mRefPersonContNo = etRefPersonContNo.getText().toString().trim();

            // fields of person info
            if (TextUtils.isEmpty(etPersonName.getText().toString().trim())) {
                etPersonName.setError("Enter Name");
            } else if (etPersonContNo.getText().toString().trim().isEmpty()) {
                etPersonContNo.setError("Enter Contact No");
            } else if (etAddress.getText().toString().trim().isEmpty()) {
                etAddress.setError("Enter Address");
            } else if (etTime.getText().toString().trim().isEmpty()) {
                etTime.setError("Enter Time");
            } else if (etAmountReceived.getText().toString().trim().isEmpty()) {
                etAmountReceived.setError("Enter Amount");
            } else {
                dialog(MainActivity.this).show();
                mBalance = Integer.parseInt(tvBalance.getText().toString().trim());
                mPersonName = etPersonName.getText().toString().trim();
                mPersonContNo = etPersonContNo.getText().toString().trim();
                mPaidBy = etPaidBy.getText().toString().trim();
                mPaidByContNo = etPaidByContNo.getText().toString().trim();
                mPersonAddress = etAddress.getText().toString().trim();
                mQurbaniTime = etTime.getText().toString().trim();
                mReceivedAmount = Integer.parseInt(etAmountReceived.getText().toString().trim());
                Date c = Calendar.getInstance().getTime();
//                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = df.format(c);
                java.sql.Timestamp timestamp = new java.sql.Timestamp(myCalendar.getTimeInMillis());
                String queryStmt = "Insert into Receipts " +
                        " (Dated,Location,VTP,Mnth,vno,chqofbank,MemberType,bank_ref,ReceivedBy,ReceivedByContact,StudentName,NTN,PaidBy,PaidByContact,Address,CowSerial," +
                        "RefVoucher_No,ShareNo,CardFee,AdmissionFee,TotalAmt,ShareAmt,MembershipFee,Balance,rcvdfrom,narr) values "
                        + "(?,'QAP','RS','" + mMonthYear + "'," + mMaxVoucherNo + ",'" + mQurbaniPlace + "','" + mAuthorizedPerson + "','" + mAuthorizedPersonContNo + "','" + mReceivedBy + "','" + mReceiverContNo + "'," +
                        "'" + mPersonName + "','" + mPersonContNo + "','" + mPaidBy + "','" + mPaidByContNo + "','" + mPersonAddress + "','" + mCowSerialNo + "'," + mCowNo + "," + mCowShareNo + ",'" + mQurbaniTime + "'," + mQurbaniDay + "," +
                        "" + mTotalAmount + "," + mSharedAmount + "," + mReceivedAmount + "," + mBalance + ",'" + mRefPersonName + "','" + mRefPersonContNo + "')";
                PreparedStatement preparedStatement = null;
                try {

                    preparedStatement = connect.prepareStatement(queryStmt);
                    preparedStatement.setTimestamp(1, timestamp);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();

                    if (mCowShareNo == 7) {
                        try {
                            String cowShareRecordQuery = "Insert into BookedCows (cow_serial,cow_no) values ('" + mCowSerialNo + "', " + mCowNo + ")";
                            PreparedStatement statement = null;

                            statement = connect.prepareStatement(cowShareRecordQuery);
                            statement.executeUpdate();
                            statement.close();
                            Log.d("success", "seccess");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }
                    String balanceMessage = "";
                    if (mBalance > 0) {
                        balanceMessage = " Apka balance " + mBalance + " hy.";
                    }
                    String message = "Salam! Dawat-e-Islami Qurbani Majlis k sath Hissa dalny ka Shukria," + balanceMessage + " Contact No: " + mAuthorizedPersonContNo + " Janwar No: " + mCowSerialNo + "-" + mCowNo;
                    sendSMS("", mPersonContNo, message, "", "");

                    Toast.makeText(this, "Data Added Successfully!", Toast.LENGTH_SHORT).show();
                    InvoiceModel invoiceModel = new InvoiceModel(String.valueOf(mMaxVoucherNo), mDate, mAuthorizedPerson, mAuthorizedPersonContNo, mPersonName, mPersonContNo, mCowSerialNo + "-" + mCowNo, String.valueOf(mCowShareNo), mQurbaniTime, mQurbaniDay, String.valueOf(mReceivedAmount), mReceivedBy);
                    Intent intent = new Intent(this, InvoiceActivity.class);
                    intent.putExtra("info", invoiceModel);
                    startActivity(intent);
                    dialog(MainActivity.this).dismiss();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    dialog(MainActivity.this).dismiss();
                    Toast.makeText(this, "Sorry Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendSMS(String Masking, String toNumber, String MessageText, String MyUsername, String MyPassword) {
        String request = "https://sendpk.com/api/sms.php?username=923219226463&password=t6663811&mobile=" + toNumber + "&sender=SMS Alert&message=" + MessageText + "";
        try {
            URL url = new URL(request);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            } else {
                System.out.println("Please enter an HTTP URL.");
                return;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()/* / POSTInputStream()*/));
            String urlString = "";
            String current;
            while ((current = in.readLine()) != null) {
                urlString += current;
            }
            System.out.println(urlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void showDateDialog() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        mDate = dateFormat.format(myCalendar.getTime());
        tvVoucherDate.setText(mDate);

    }

    public ProgressDialog dialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading…");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/QurbaniApp/");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
    }

    private void saveImageFilePath(boolean isShare, View view) {

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + timeStamp + ".jpg";

        File selectedOutputPath = getOutputMediaFile();
//        Log.d(YOUR_FOLDER_NAME, "selected camera path " + selectedOutputPath);

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

        int maxSize = 1080;

        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

        if (bWidth > bHeight) {
            int imageHeight = (int) Math.abs(maxSize * ((float) bitmap.getWidth() / (float) bitmap.getHeight()));
            bitmap = Bitmap.createScaledBitmap(bitmap, maxSize, imageHeight, true);
        } else {
            int imageWidth = (int) Math.abs(maxSize * ((float) bitmap.getWidth() / (float) bitmap.getHeight()));
            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, maxSize, true);
        }
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        OutputStream fOut = null;
        try {
            File file = new File(String.valueOf(selectedOutputPath));
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            if (isShare) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(String.valueOf(file));

                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return selectedOutputPath;
    }

}
