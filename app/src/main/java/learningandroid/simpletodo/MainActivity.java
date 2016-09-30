package learningandroid.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import learningandroid.simpletodo.model.Item;

import static learningandroid.simpletodo.ModelSource.DB;
import static learningandroid.simpletodo.ModelSource.FILE;

public class MainActivity extends AppCompatActivity {
    private List<String> itemDescriptions = new ArrayList<>();
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private final int REQUEST_CODE = 20;
    private static final ModelSource source = DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.initialize(this);
        readItems();
        setContentView(R.layout.activity_main);
        lvItems = (ListView)findViewById(R.id.lvItems);
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, itemDescriptions);
        lvItems.setAdapter(itemsAdapter);
        setupListeners();
    }

    public void onAddItem(View view) {
        EditText etItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etItem.getText().toString();
        if (StringUtils.isBlank(itemText)){
            Toast.makeText(this, "Please enter a valid todo item.", Toast.LENGTH_SHORT).show();
            etItem.setText("");
            return;
        } else {
            itemsAdapter.add(itemText);
            etItem.setText("");
            writeItems();
        }
    }

    private void setupListeners() {
        lvItems.setOnItemLongClickListener(
            new android.widget.AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapter,
                                               View item, int pos, long id) {
                    itemDescriptions.remove(pos);
                    itemsAdapter.notifyDataSetChanged();
                    writeItems();
                    return true;
                }
            });
        lvItems.setOnItemClickListener(
                new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                               View item, int pos, long id) {
                        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                        i.putExtra("todoItem", itemDescriptions.get(pos)); // pass arbitrary data to launched activity
                        i.putExtra("pos", pos);
                        startActivityForResult(i, REQUEST_CODE);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                    }
                });
    }

    private void readItems() {
        if (source == FILE){
            readItemsFromFile();
        } else if (source == DB) {
            readItemsFromDB();
        }
    }

    private void readItemsFromDB(){
        List<Item> items = Item.getItems();
        List<String> descriptions = new ArrayList<>();
        for (Item i : items){
            descriptions.add(i.description);
        }
    }

    private void readItemsFromFile() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            itemDescriptions = new ArrayList<>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            itemDescriptions = new ArrayList<>();
        }
    }

    private void writeItems() {
        if (source == FILE){
            writeItemsToFile();
        } else if (source == DB) {
            writeItemsToDB();
        }
    }

    private void writeItemsToDB(){
        ActiveAndroid.beginTransaction();
        try {
            for (String description : itemDescriptions) {
                Item item = new Item();
                item.description = description;
                item.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void writeItemsToFile() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, itemDescriptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String newTodoItem = data.getExtras().getString("updatedTodoItem");
            int pos = data.getExtras().getInt("pos");
            itemDescriptions.set(pos, newTodoItem);
            Toast.makeText(this, "Item updated.", Toast.LENGTH_SHORT).show();
        }
    }
}
