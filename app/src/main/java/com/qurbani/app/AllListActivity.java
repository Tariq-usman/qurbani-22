package com.qurbani.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AllListActivity extends AppCompatActivity {
    private Connection connection;
    private ArrayList<ItemModel> list = new ArrayList<ItemModel>();
    ItemModel itemModel;
    private RecyclerView recyclerView;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);
        connection = ConnectionHelper.CONN();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(AllListActivity.this, list);
        recyclerView.setAdapter(listAdapter);

        findUsersList();
    }

    private void findUsersList() {
        list.clear();
        try {
            Statement statement = connection.createStatement();
            String query = "select StudentName,NTN from Receipts";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (!resultSet.wasNull()) {
                    itemModel = new ItemModel(resultSet.getString(1), resultSet.getString(2));
                    list.add(itemModel);
                }
            }
            listAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}