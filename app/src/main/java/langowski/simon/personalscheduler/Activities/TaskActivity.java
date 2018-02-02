package langowski.simon.personalscheduler.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import langowski.simon.personalscheduler.Adapters.TaskAdapter;
import langowski.simon.personalscheduler.Helpers.Event;
import langowski.simon.personalscheduler.R;

import static langowski.simon.personalscheduler.Adapters.TaskAdapter.EDIT_REQUEST;

public class TaskActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 5;

    @BindView(R.id.mainRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.addEvent)
    Button addEvent;

    private TaskAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        context = this;
        if (adapter == null){
            ArrayList<Event> dataset;
            try {
                ObjectInputStream ois = new ObjectInputStream(openFileInput("events.txt"));
                dataset = (ArrayList<Event>) ois.readObject();
                ois.close();
            } catch (FileNotFoundException e){
                dataset = new ArrayList<Event>();
            } catch (IOException e){
                Toast.makeText(this, "Error retrieving file: " + e.toString(), Toast.LENGTH_LONG).show();
                dataset = new ArrayList<Event>();
            } catch (ClassNotFoundException e){
                Toast.makeText(this, "Data is corrupted: " + e.toString(), Toast.LENGTH_LONG).show();
                dataset = new ArrayList<Event>();
            }
            adapter = new TaskAdapter(this, dataset);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
        }
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventCreationActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if ((swipeDir & (ItemTouchHelper.LEFT | ItemTouchHelper.START)) > 0) {
                    adapter.getDataset().get(viewHolder.getAdapterPosition()).setComplete(false);
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    writeData();
                } else if ((swipeDir & (ItemTouchHelper.RIGHT | ItemTouchHelper.END)) > 0) {
                    adapter.getDataset().get(viewHolder.getAdapterPosition()).setComplete(true);
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    writeData();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE): {
                if (resultCode == Activity.RESULT_OK) {
                    Event addedEvent = (Event) data.getSerializableExtra("event");
                    if (addedEvent == null){
                        break;
                    }
                    adapter.add(addedEvent);
                    adapter.notifyDataSetChanged();
                    writeData();
                }
                break;
            } case (EDIT_REQUEST):{
                if (resultCode == Activity.RESULT_OK) {
                    Event addedEvent = (Event) data.getSerializableExtra("event");
                    int location = data.getIntExtra("location", -1);
                    if (addedEvent == null){
                        adapter.remove(location);
                        adapter.notifyItemRemoved(location);
                        writeData();
                        break;
                    }
                    adapter.replace(location, addedEvent);
                    writeData();
                    adapter.notifyDataSetChanged();
                    writeData();
                }
                break;
            }
        }

    }

    public void writeData(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("events.txt", Context.MODE_PRIVATE));
            oos.writeObject(adapter.getDataset());
            oos.close();
            Toast.makeText(this, "Write successful" , Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            Toast.makeText(this, "Error writing file: " + e.toString() , Toast.LENGTH_LONG).show();
        }
    }
}
