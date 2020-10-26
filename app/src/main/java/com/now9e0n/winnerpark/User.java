package com.now9e0n.winnerpark;

import java.io.Serializable;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User implements Serializable {
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String createdDate;
    private String gameKind;

    public static User getUserBySnapshot(Map<String, String> map) {
        String name = map.get("name");
        String phoneNumber = map.get("phoneNumber");
        String email = map.get("email");
        String password = map.get("password");
        String createdDate = map.get("createdDate");
        String gameKind = map.get("gameKind");

        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .password(password)
                .createdDate(createdDate)
                .gameKind(gameKind)
                .build();
    }
}