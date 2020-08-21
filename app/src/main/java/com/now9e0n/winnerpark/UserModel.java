package com.now9e0n.winnerpark;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UserModel implements Serializable {
    private String name;
    private String phoneNumber;
    private String email;
    private String birthDate;
    private String id;
    private String password;
}