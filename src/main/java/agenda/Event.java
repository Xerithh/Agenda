package agenda;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class Event {

    /**
     * The myTitle of this event
     */
    private String myTitle;
    
    /**
     * The starting time of the event
     */
    private LocalDateTime myStart;

    /**
     * The durarion of the event 
     */
    private Duration myDuration;
    
    /**
     * The repetition of this event (optional)
     */
    private Repetition repetition;


    /**
     * Constructs an event
     *
     * @param title the title of this event
     * @param start the start time of this event
     * @param duration the duration of this event
     */
    public Event(String title, LocalDateTime start, Duration duration) {
        this.myTitle = title;
        this.myStart = start;
        this.myDuration = duration;
    }

    public void setRepetition(ChronoUnit frequency) {
        this.repetition = new Repetition(frequency);
    }

    public void addException(LocalDate date) {
        if (repetition != null) {
            repetition.addException(date);
        }
    }

    public void setTermination(LocalDate terminationInclusive) {
        if (repetition != null) {
            LocalDate startDate = myStart.toLocalDate();
            Termination termination = new Termination(startDate, repetition.getFrequency(), terminationInclusive);
            repetition.setTermination(termination);
        }
    }

    public void setTermination(long numberOfOccurrences) {
        if (repetition != null) {
            LocalDate startDate = myStart.toLocalDate();
            Termination termination = new Termination(startDate, repetition.getFrequency(), numberOfOccurrences);
            repetition.setTermination(termination);
        }
    }

    public int getNumberOfOccurrences() {
        if (repetition != null && repetition.getTermination() != null) {
            return (int) repetition.getTermination().numberOfOccurrences();
        }
        throw new UnsupportedOperationException("Pas de terminaison définie");
    }

    public LocalDate getTerminationDate() {
        if (repetition != null && repetition.getTermination() != null) {
            return repetition.getTermination().terminationDateInclusive();
        }
        throw new UnsupportedOperationException("Pas de terminaison définie");
    }

    /**
     * Tests if an event occurs on a given day
     *
     * @param aDay the day to test
     * @return true if the event occurs on that day, false otherwise
     */
    public boolean isInDay(LocalDate aDay) {
        LocalDate eventStartDate = myStart.toLocalDate();
        LocalDateTime eventEnd = myStart.plus(myDuration);
        LocalDate eventEndDate = eventEnd.toLocalDate();
        
        if (!aDay.isBefore(eventStartDate) && !aDay.isAfter(eventEndDate)) {
            return true;
        }
        
        if (repetition == null) {
            return false;
        }
        
        if (aDay.isBefore(eventStartDate)) {
            return false;
        }
        
        if (repetition.getTermination() != null) {
            LocalDate terminationDate = repetition.getTermination().terminationDateInclusive();
            // Calculer la fin du dernier événement
            LocalDateTime lastEventStart = terminationDate.atTime(myStart.toLocalTime());
            LocalDateTime lastEventEnd = lastEventStart.plus(myDuration);
            LocalDate lastEventEndDate = lastEventEnd.toLocalDate();
            
            if (aDay.isAfter(lastEventEndDate)) {
                return false;
            }
        }
        
        long unitsBetween = repetition.getFrequency().between(eventStartDate, aDay);
        
        if (unitsBetween < 0) {
            return false;
        }
        
        // Calculer la date de début
        LocalDate occurrenceStartDate = eventStartDate.plus(unitsBetween, repetition.getFrequency());
        LocalDateTime occurrenceStart = occurrenceStartDate.atTime(myStart.toLocalTime());
        LocalDateTime occurrenceEnd = occurrenceStart.plus(myDuration);
        LocalDate occurrenceEndDate = occurrenceEnd.toLocalDate();
        
        if (aDay.isBefore(occurrenceStartDate) || aDay.isAfter(occurrenceEndDate)) {
            return false;
        }
        
        if (repetition.getExceptions().contains(occurrenceStartDate)) {
            return false;
        }
        
        return true;
    }
   
    /**
     * @return the myTitle
     */
    public String getTitle() {
        return myTitle;
    }

    /**
     * @return the myStart
     */
    public LocalDateTime getStart() {
        return myStart;
    }


    /**
     * @return the myDuration
     */
    public Duration getDuration() {
        return myDuration;
    }
    
    /**
     * @return the repetition
     */
    public Repetition getRepetition() {
        return repetition;
    }

    @Override
    public String toString() {
        return "Event{title='%s', start=%s, duration=%s}".formatted(myTitle, myStart, myDuration);
    }
}
