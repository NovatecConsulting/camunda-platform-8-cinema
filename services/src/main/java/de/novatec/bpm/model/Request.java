package de.novatec.bpm.model;

import lombok.Data;

import java.util.List;

@Data
public class Request {
    private List<String> seats;
    private String name;
}
