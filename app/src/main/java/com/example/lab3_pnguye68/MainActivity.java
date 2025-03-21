package com.example.lab3_pnguye68;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editTextItem;
    private Button buttonAdd, buttonDeleteDone;
    private ListView listView;
    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextItem = findViewById(R.id.editTextItem);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonDeleteDone = findViewById(R.id.buttonDeleteDone);
        listView = findViewById(R.id.listView);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        loadData();

        // Add new item to the database and the list
        buttonAdd.setOnClickListener(v -> {
            String item = editTextItem.getText().toString().trim();
            if (!item.isEmpty()) {
                // Add new item to the db
                dbHelper.insertItem(item);
                loadData();
                editTextItem.setText("");
            }
        });

        // Remove item with long click
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String item = itemList.get(position);
                        dbHelper.deleteItem(item);
                        loadData();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        // Handle the done item
        // Mark item as done and move it to the bottom or top
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = itemList.get(position);

            if (selectedItem.startsWith("Done: ")) {
                // Remove "Done: " prefix and move to the top
                String updatedItem = selectedItem.replace("Done: ", "");
                dbHelper.updateItem(selectedItem, updatedItem);
            }
            else {
                // Mark as done and move to the bottom
                dbHelper.updateItem(selectedItem, "Done: " + selectedItem);
            }
            loadData();
        });

        // Remove all completed items
        buttonDeleteDone.setOnClickListener(v -> {
            dbHelper.deleteDoneItems();
            loadData();
        });
    }

    private void loadData() {
        new LoadDB().execute();
    }

    private class LoadDB extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            itemList.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    itemList.add(cursor.getString(0));  // Fetch the "item" column
                } while (cursor.moveToNext());
                cursor.close();
            }
            adapter.notifyDataSetChanged();
        }
    }
}