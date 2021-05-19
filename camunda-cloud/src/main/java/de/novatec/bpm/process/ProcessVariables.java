package de.novatec.bpm.process;

public enum ProcessVariables {

    SEATS_AVAILABLE("seatsAvailable");

    private String name;

    ProcessVariables(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}