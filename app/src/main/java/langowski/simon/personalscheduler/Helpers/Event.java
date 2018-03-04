package langowski.simon.personalscheduler.Helpers;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Instant;

import java.io.Serializable;

/**
 * Created by Slang on 1/26/2018.
 */

public class Event implements Serializable, Comparable<Event>{

    static final long serialVersionUID = 409448262860737303L;

    private String title;
    private String description;

    private DateTime eventDate;
    private double importance;
    private boolean flagged;
    private boolean complete;
    private int daysRemaining;

    public Event(String title, String description, DateTime eventDate, double importance){
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.importance = importance;
        this.flagged = false;
        this.complete = false;
        this.daysRemaining = -Days.daysBetween(eventDate.toLocalDate(), Instant.now().toDateTime().toLocalDate()).getDays();
    }

    public double getScore(){
        daysRemaining = -Days.daysBetween(eventDate.toLocalDate(), Instant.now().toDateTime().toLocalDate()).getDays();
        if (daysRemaining <= 1){
            flagged = true;
            if (daysRemaining < 0){
                return 10;
            }
        } else {
            flagged = false;
        }
        double result = (90 + importance - Math.pow(daysRemaining, 2))/10;
        if (result < 0){
            return 0;
        } else {
            return result;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(DateTime eventDate) {
        this.eventDate = eventDate;
    }

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
        if (complete){
            flagged = false;
        } else {
            getScore();
        }
    }

    public String getFormattedScore(){
        double score2 = Math.round(getScore() * 10)*0.1;
        if (score2 > 10){
            return "10";
        } else {
            return Double.toString(score2).substring(0,3);
        }
    }

    public int getDaysRemaining() {
        daysRemaining = -Days.daysBetween(eventDate.toLocalDate(), Instant.now().toDateTime().toLocalDate()).getDays();
        return daysRemaining;
    }

    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    @Override
    public int compareTo(Event e) {
        if (complete && e.isComplete()) {
            return -eventDate.compareTo(e.getEventDate());
        } else if (complete && !e.isComplete()){
            return 1;
        } else if (e.isComplete() && !complete){
            return -1;
        }
        double score = getScore();
        double yourScore = e.getScore();
        if (score > yourScore){
            return -1;
        } else if (score < yourScore){
            return 1;
        } else {
            return eventDate.compareTo(e.getEventDate());
        }
    }
}
