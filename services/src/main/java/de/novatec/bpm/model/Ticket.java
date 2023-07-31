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
    private byte[] qrCode;
}
