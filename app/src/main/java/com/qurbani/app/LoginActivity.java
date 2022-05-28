package com.qurbani.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {
    Connection connect;
    private Button btnLogin;
    private EditText etName, etPass;
    SharedPrefClass prefClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connect = ConnectionHelper.CONN();
        prefClass = new SharedPrefClass(this);

/*
        try {
            Statement stmt = connect.createStatement();
            String sql = "CREATE TABLE BookedCows " +
                    "(id INTEGER not NULL IDENTITY(1, 1), " +
                    " cow_serial VARCHAR(255) not NULL, " +
                    " cow_no INTEGER not NULL, " +
                    " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
*/
        etName = findViewById(R.id.etName);
        etPass = findViewById(R.id.etPass);
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    etName.setError("Please enter your name");
                } else if (TextUtils.isEmpty(etPass.getText().toString().trim())) {
                    etPass.setError("Please enter password");
                } else {
                    String name = etName.getText().toString().trim();
                    String pass = etPass.getText().toString().trim();

                    login(name, pass);
                }
            }
        });


    }

    private void login(String id, String pass) {

        String getQuery = "select id from users where id='" + id + "' and pwd='" + pass + "'";
        try {
            Statement preparedStatement = connect.createStatement();
            ResultSet resultSet = preparedStatement.executeQuery(getQuery);
            if (!resultSet.next()) {
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            } else {
//                while (resultSet.next()) {
                Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                String userId = resultSet.getString(1);
                Intent intent = new Intent(LoginActivity.this, SelectOption.class);
                prefClass.setUserId(userId);
//                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
                Log.d("userid", userId);

               /* mMaxVoucherNo = Integer.parseInt(resultSet.getString(1)) + 1;
                Log.i("result", String.valueOf(mMaxVoucherNo));
                tvVoucherEndNo.setText(mMaxVoucherNo + "");*/
//                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}