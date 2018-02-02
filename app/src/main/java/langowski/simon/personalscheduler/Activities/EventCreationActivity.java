package langowski.simon.personalscheduler.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import org.joda.time.DateTime;

import butterknife.ButterKnife;
import langowski.simon.personalscheduler.Helpers.Event;
import langowski.simon.personalscheduler.R;
import butterknife.BindView;

//also event view
//and event edit


public class EventCreationActivity extends AppCompatActivity {


    @BindView(R.id.datePicker)
    DatePicker datePicker;

    @BindView(R.id.description)
    EditText notes;

    @BindView(R.id.title_of_event)
    EditText title;

    @BindView(R.id.importance)
    EditText importance;

    @BindView(R.id.submit)
    Button submit;

    @BindView(R.id.delete)
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        ButterKnife.bind(this);
        Event e = (Event) getIntent().getSerializableExtra("Event");
        final int index = getIntent().getIntExtra("Location", -1);
        if (e != null){
            title.setText(e.getTitle());
            notes.setText(e.getDescription());
            importance.setText(Double.toString(e.getImportance()));
            datePicker.init(e.getEventDate().getYear(), e.getEventDate().getMonthOfYear() - 1, e.getEventDate().getDayOfMonth(), null);
            submit.setText("Update Event");
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                DateTime date = new DateTime(year, month, day, 0,0);
                double value;
                try {
                    value = Double.parseDouble(importance.getText().toString());
                } catch (NumberFormatException e){
                    value = 0;
                }
                Event event = new Event(title.getText().toString(), notes.getText().toString(), date, value);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("event", event);
                resultIntent.putExtra("location", index);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                Event e = null;
                resultIntent.putExtra("event", e);
                resultIntent.putExtra("location", index);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

}
