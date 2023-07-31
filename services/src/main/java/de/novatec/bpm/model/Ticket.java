package de.novatec.bpm.model;

import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ticket {

    private String code;
    @ToString.Exclude
    private String info;

    public static String createInfo(String name, List<String> seats, String ticketId) {
        String movieStart = new SimpleDateFormat("dd.MM.yy").format(new Date());
        String now = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
        return String.format("Hello there %s, " +
                        "Thanks for your reservation at %s.\n" +
                        "Here are all the details you need:\n" +
                        "Movie: Not another BPM movie, %s 21:00 Uhr\n" +
                        "Seats: %s\n" +
                        "Ticket id: %s\n" +
                        "Have fun at the movies\n\n" +
                        "Your Novatec Cinema Team!",
                name, now, movieStart, String.join(", ", seats), ticketId);
    }
}
