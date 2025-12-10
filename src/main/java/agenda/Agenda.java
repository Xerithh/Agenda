package agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description : An agenda that stores events
 */
public class Agenda {
    
    private List<Event> events;
    
    public Agenda() {
        this.events = new ArrayList<>();
    }
    
    /**
     * Adds an event to this agenda
     *
     * @param e the event to add
     */
    public void addEvent(Event e) {
        events.add(e);
    }

    /**
     * Computes the events that occur on a given day
     *
     * @param day the day toi test
     * @return a list of events that occur on that day
     */
    public List<Event> eventsInDay(LocalDate day) {
        return events.stream()
                .filter(event -> event.isInDay(day))
                .collect(Collectors.toList());
    }
    
    public List<Event> findByTitle(String title) {
        return events.stream()
                .filter(event -> event.getTitle().equals(title))
                .collect(Collectors.toList());
    }
    

    //  Déterminer s'il y a de la place dans l'agenda pour un événement (aucun autre événement au même moment)
    public boolean isFreeFor(Event e) {
        LocalDateTime newEventStart = e.getStart();
        LocalDateTime newEventEnd = e.getStart().plus(e.getDuration());
        
        for (Event existingEvent : events) {
            LocalDateTime existingStart = existingEvent.getStart();
            LocalDateTime existingEnd = existingEvent.getStart().plus(existingEvent.getDuration());
            
            if (newEventStart.isBefore(existingEnd) && newEventEnd.isAfter(existingStart)) {
                return false;
            }
        }
        
        return true;
    }
}
