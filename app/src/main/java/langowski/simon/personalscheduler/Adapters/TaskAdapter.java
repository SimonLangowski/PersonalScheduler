package langowski.simon.personalscheduler.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import langowski.simon.personalscheduler.Activities.EventCreationActivity;
import langowski.simon.personalscheduler.Helpers.Event;
import langowski.simon.personalscheduler.R;

/**
 * Created by Slang on 1/29/2018.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.AreaViewHolder> {

    private static DateTimeFormatter dtfOut = DateTimeFormat.forPattern("E MM/dd/yyyy");
    private ArrayList<Event> dataset;
    private Context context;
    public static final int EDIT_REQUEST = 7;

    public TaskAdapter(Context context, ArrayList<Event> dataset){
        this.dataset = dataset;
        this.context = context;
        Collections.sort(dataset);
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        Event e = dataset.get(position);
        holder.eventTitle.setText(e.getTitle());
        if (e.getDescription().trim().isEmpty()){
            holder.notes.setVisibility(View.GONE);
        } else {
            holder.notes.setText(e.getDescription());
            holder.notes.setVisibility(View.VISIBLE);
        }
        holder.days.setText(Integer.toString(e.getDaysRemaining()));
        holder.date.setText(dtfOut.print(e.getEventDate()));
        holder.score.setText(e.getFormattedScore());
        if (e.isComplete()){
            holder.score.setText("C");
            holder.score.setTextColor(Color.GREEN);
        } else if (e.isFlagged()){
            holder.score.setText("!");
            holder.score.setTextColor(Color.YELLOW);
            if (e.getDaysRemaining() <= 0){
                holder.score.setText("!!");
                holder.score.setTextColor(Color.RED);
            }
        } else {
            holder.score.setTextColor(Color.BLUE);
        }

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_event)
        CardView eventCard;

        @BindView(R.id.event_title)
        TextView eventTitle;

        @BindView(R.id.score)
        TextView score;

        @BindView(R.id.notes)
        TextView notes;

        @BindView(R.id.days)
        TextView days;

        @BindView(R.id.date)
        TextView date;


        public AreaViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick(R.id.card_event)
        public void onClick(){
            Intent intent = new Intent(context, EventCreationActivity.class);
            Event clickedEvent = dataset.get(this.getLayoutPosition());
            intent.putExtra("Event", clickedEvent);
            intent.putExtra("Location", this.getLayoutPosition());
            ((Activity) context).startActivityForResult(intent, EDIT_REQUEST);
        }
    }

    public void add(Event e){
        dataset.add(e);
        Collections.sort(dataset);
        notifyDataSetChanged();
    }

    public void remove(int index){
        dataset.remove(index);
    }

    public void replace(int index, Event e){
        if (dataset.get(index).getScore() == e.getScore()) {
            dataset.set(index, e);
            notifyItemChanged(index);
        } else {
            dataset.set(index, e);
            Collections.sort(dataset);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Event> getDataset(){
        return dataset;
    }

}
