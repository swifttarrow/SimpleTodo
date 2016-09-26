package learningandroid.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        EditText etName = (EditText) findViewById(R.id.editExisting);
        etName.setText(getIntent().getExtras().getString("todoItem"));
        pos = getIntent().getExtras().getInt("pos");
    }

    public void onSubmit(View v) {
        EditText etName = (EditText) findViewById(R.id.editExisting);
        Intent data = new Intent();
        data.putExtra("updatedTodoItem", etName.getText().toString());
        data.putExtra("pos", pos);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
