package com.now9e0n.winnerpark;

import java.io.Serializable;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserModel implements Serializable {
    private String name;
    private String phoneNumber;
    private String email;
    private String id;
    private String password;
    private String createdDate;
    private String gameKind;

    public static UserModel getUserBySnapshot(Map<String, String> map) {
        String name = map.get("name");
        String phoneNumber = map.get("phoneNumber");
        String email = map.get("email");
        String id = map.get("id");
        String password = map.get("password");
        String createdDate = map.get("createdDate");
        String gameKind = map.get("gameKind");

        return UserModel.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .id(id)
                .password(password)
                .createdDate(createdDate)
                .gameKind(gameKind)
                .build();
    }
}