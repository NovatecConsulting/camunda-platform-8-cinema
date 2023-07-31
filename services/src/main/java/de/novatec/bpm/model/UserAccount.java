package de.novatec.bpm.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    private String userId;
    private String email;
    private String iban;
    private String bic;

}
