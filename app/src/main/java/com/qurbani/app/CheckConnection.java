package com.qurbani.app;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CheckConnection extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        try {
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect!=null){
                System.out.println("Connected");
            }else {
                Log.d("","");
            }

          /*  String queryStmt = "Insert into tblUsers " +
                    " (UserId,Password,UserRole) values "
                    + "('"
//                    + emailId
                    + "','"
//                    + password
                    + "','User')";

            PreparedStatement preparedStatement = connect.prepareStatement(queryStmt);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            return "Added successfully";*/
       /* } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage().toString();*/
            return null;
        } catch (Exception e) {
            return "Exception. Please check your code and database.";
        }
    }
}
