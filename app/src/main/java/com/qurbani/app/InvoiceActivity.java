package com.qurbani.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceActivity extends AppCompatActivity {

    private TextView tvReceiptNo, tvDate, tvAuthorizedPerson, tvAuthPersonContNo, tvPersonName,
            tvPersonContNo, tvCowNo, tvCowShareNo, tvTime, tvDay, tvAmount, tvReceivedBy, btnSave, btnShare;
    private String receiptNo, date, authPerson, authPersonNo, personName, personNo, cowNo, cowShareNo, time, day, amount, receivedBy;
    InvoiceModel invoiceModel;
    Intent intent;
    LinearLayout layoutPdf;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        intent = getIntent();
        if (intent != null) {
            invoiceModel = (InvoiceModel) intent.getSerializableExtra("info");
        }

        layoutPdf = findViewById(R.id.layoutPdf);

        tvReceiptNo = findViewById(R.id.tvReceiptNo);
        tvDate = findViewById(R.id.tvDate);
        tvAuthorizedPerson = findViewById(R.id.tvAuthorizedPerson);
        tvAuthPersonContNo = findViewById(R.id.tvAuthPersonContNo);
        tvPersonName = findViewById(R.id.tvPersonName);
        tvPersonContNo = findViewById(R.id.tvPersonContNo);
        tvCowNo = findViewById(R.id.tvCowNo);
        tvCowShareNo = findViewById(R.id.tvCowShareNo);
        tvTime = findViewById(R.id.tvTime);
        tvDay = findViewById(R.id.tvDay);
        tvAmount = findViewById(R.id.tvAmount);
        tvReceivedBy = findViewById(R.id.tvReceivedBy);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = LoadBitmap(layoutPdf, layoutPdf.getWidth(), layoutPdf.getHeight());
                getBitmapFromView(false, layoutPdf);
            }
        });
        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = LoadBitmap(layoutPdf, layoutPdf.getWidth(), layoutPdf.getHeight());
                getBitmapFromView(true, layoutPdf);

            }
        });

        updateViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    public void getBitmapFromView(boolean isShare, View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        shareImage(isShare, getApplicationContext(), returnedBitmap);
    }

    private void updateViews() {
        tvReceiptNo.setText(invoiceModel.getReceiptNo());
        tvDate.setText(invoiceModel.getDate());
        tvAuthorizedPerson.setText(invoiceModel.getAuthPerson());
        tvAuthPersonContNo.setText(invoiceModel.getAuthPersonNo());
        tvPersonName.setText(invoiceModel.getPersonName());
        tvPersonContNo.setText(invoiceModel.getPersonNo());
        tvCowNo.setText(invoiceModel.getCowNo());
        tvCowShareNo.setText(invoiceModel.getCowShareNo());
        tvTime.setText(invoiceModel.getTime());
        tvDay.setText(invoiceModel.getDay());
        tvAmount.setText(invoiceModel.getAmount());
        tvReceivedBy.setText(invoiceModel.getReceivedBy());

    }

    private Bitmap LoadBitmap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/QurbaniApp/");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
    }

    private void shareImage(boolean isShare, Context context, Bitmap returnedBitmap) {
        try {
            File file = getOutputMediaFile();
            FileOutputStream fOut = new FileOutputStream(file);
            if (returnedBitmap != null)
                returnedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

            if (isShare) {
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);

                intent.setType("image/png");
                Intent chooser = Intent.createChooser(intent, "Share image via");
                startActivity(chooser);
//                finish();
            }else {
                Toast.makeText(context, "Image Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}