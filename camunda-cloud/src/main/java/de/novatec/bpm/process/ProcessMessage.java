package de.novatec.bpm.process;

public enum ProcessMessage {

    SEATS_VERIFIED("SeatsVerifiedByCustomer");

    private final String name;

    ProcessMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


}
