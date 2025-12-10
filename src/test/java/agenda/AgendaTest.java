package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgendaTest {
    Agenda agenda;

    // November 1st, 2020
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // January 5, 2021
    LocalDate jan_5_2021 = LocalDate.of(2021, 1, 5);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // Un événement simple
    // November 1st, 2020, 22:30, 120 minutes
    Event simple;

    // Un événement qui se répète toutes les semaines et se termine à une date
    // donnée
    Event fixedTermination;

    // Un événement qui se répète toutes les semaines et se termine après un nombre
    // donné d'occurrences
    Event fixedRepetitions;

    // A daily repetitive event, never ending
    // Un événement répétitif quotidien, sans fin
    // November 1st, 2020, 22:30, 120 minutes
    Event neverEnding;

    @BeforeEach
    public void setUp() {
        simple = new Event("Simple event", nov_1_2020_22_30, min_120);

        fixedTermination = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedTermination.setRepetition(ChronoUnit.WEEKS);
        fixedTermination.setTermination(jan_5_2021);

        fixedRepetitions = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedRepetitions.setRepetition(ChronoUnit.WEEKS);
        fixedRepetitions.setTermination(10);

        neverEnding = new Event("Never Ending", nov_1_2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);

        agenda = new Agenda();
        agenda.addEvent(simple);
        agenda.addEvent(fixedTermination);
        agenda.addEvent(fixedRepetitions);
        agenda.addEvent(neverEnding);
    }

    @Test
    public void testMultipleEventsInDay() {
        assertEquals(4, agenda.eventsInDay(nov_1_2020).size(),
                "Il y a 4 événements ce jour là");
        assertTrue(agenda.eventsInDay(nov_1_2020).contains(neverEnding));
    }

    @Test
    public void testFindByTitle() {
        // Tester la recherche d'événements par titre
        assertEquals(2, agenda.findByTitle("Fixed termination weekly").size(),
                "Il devrait y avoir 2 événements avec ce titre");
        assertEquals(1, agenda.findByTitle("Simple event").size(),
                "Il devrait y avoir 1 événement avec ce titre");
        assertEquals(0, agenda.findByTitle("Non existant").size(),
                "Il ne devrait pas y avoir d'événement avec ce titre");
        assertTrue(agenda.findByTitle("Never Ending").contains(neverEnding),
                "La recherche devrait retourner l'événement 'Never Ending'");
    }

    @Test
    public void testIsFreeFor() {
        // Créer un nouvel agenda pour ce test
        Agenda testAgenda = new Agenda();
        
        // Ajouter un événement : 1er novembre 2020, 10:00-12:00
        Event existing = new Event("Existing", LocalDateTime.of(2020, 11, 1, 10, 0), Duration.ofHours(2));
        testAgenda.addEvent(existing);
        
        // Test 1 : Un événement qui chevauche (10:30-11:30) ne devrait pas être libre
        Event overlapping = new Event("Overlapping", LocalDateTime.of(2020, 11, 1, 10, 30), Duration.ofHours(1));
        assertTrue(!testAgenda.isFreeFor(overlapping),
                "Un événement qui chevauche ne devrait pas être libre");
        
        // Test 2 : Un événement qui commence exactement à la fin (12:00-13:00) devrait être libre
        Event adjacent = new Event("Adjacent", LocalDateTime.of(2020, 11, 1, 12, 0), Duration.ofHours(1));
        assertTrue(testAgenda.isFreeFor(adjacent),
                "Un événement qui commence exactement à la fin d'un autre devrait être libre");
        
        // Test 3 : Un événement complètement avant (08:00-09:00) devrait être libre
        Event before = new Event("Before", LocalDateTime.of(2020, 11, 1, 8, 0), Duration.ofHours(1));
        assertTrue(testAgenda.isFreeFor(before),
                "Un événement avant un autre devrait être libre");
        
        // Test 4 : Un événement complètement après (14:00-15:00) devrait être libre
        Event after = new Event("After", LocalDateTime.of(2020, 11, 1, 14, 0), Duration.ofHours(1));
        assertTrue(testAgenda.isFreeFor(after),
                "Un événement après un autre devrait être libre");
    }

}
