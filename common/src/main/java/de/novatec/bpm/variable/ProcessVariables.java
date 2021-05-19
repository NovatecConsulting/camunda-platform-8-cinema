package de.novatec.bpm.variable;

import de.novatec.bpm.message.ProcessMessage;

public enum ProcessVariables {

    ALT_SEATS("alternativeSeat"),
    SEATS("seats"),
    RESERVATION("reservation"),
    USER_ID("userId"),
    SEATS_AVAILABLE("seatsAvailable"),
    USER_DATA("userData"),
    PROCESS_SUCCESS("processSuccess"),
    TICKET("ticket"),
    QR("qr");

    private String name;

    ProcessVariables(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
