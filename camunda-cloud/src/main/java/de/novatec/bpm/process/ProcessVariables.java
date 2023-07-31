package de.novatec.bpm.process;

public enum ProcessVariables {

    SEATS_AVAILABLE("seatsAvailable"),
    TICKET("ticket"),
    SEATS("seats"),
    RESERVATION_ID("reservationId"),
    NAME("name");

    private final String name;

    ProcessVariables(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
