package com.qurbani.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    private ArrayList<ItemModel> searchList = new ArrayList<ItemModel>();
    ItemModel itemModel;
    private RecyclerView recyclerView;
    ListAdapter listAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);
        connection = ConnectionHelper.CONN();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(AllListActivity.this, list);
        recyclerView.setAdapter(listAdapter);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                if (text.isEmpty()){
                    list.clear();
                    list.addAll(searchList);
                    Log.d("size", String.valueOf(list.size()));
                }else {
                    ArrayList<ItemModel> arrayList = new ArrayList<>();
                    for (ItemModel model : searchList) {
                        if (model.getName().toLowerCase().contains(text.toLowerCase())) {
                            arrayList.add(model);
                        }
                    }
                    if (arrayList.size()>0){
                        list.clear();
                        list.addAll(arrayList);
                    }
                }
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });

        findUsersList();
    }

    private void findUsersList() {
        list.clear();
        searchList.clear();
        try {
            Statement statement = connection.createStatement();
            String query = "select StudentName,NTN from Receipts";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (!resultSet.wasNull()) {
                    itemModel = new ItemModel(resultSet.getString(1), resultSet.getString(2));
                    list.add(itemModel);
                    searchList.add(itemModel);
                }
            }
            listAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}