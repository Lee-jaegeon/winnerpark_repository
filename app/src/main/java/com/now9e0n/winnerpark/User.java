package com.now9e0n.winnerpark;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<String> gameKindList;

    public static User getUserByUserData(Map<String, Object> map) {
        String name = (String) map.get("name");
        String phoneNumber = (String) map.get("phoneNumber");
        String email = (String) map.get("email");
        String password = (String) map.get("password");
        String createdDate = (String) map.get("createdDate");
        List<String> gameKindList = (List<String>) map.get("gameKindArray");

        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .password(password)
                .createdDate(createdDate)
                .gameKindList(gameKindList)
                .build();
    }

    public static List<User> getUserBySnapshot(DataSnapshot snapshot) {
        Map<String, Map<String, Object>> userMap = (Map<String, Map<String, Object>>) snapshot.getValue();
        List<User> userList = new ArrayList<>();

        for (Map<String, Object> userData : userMap.values())
            userList.add(getUserByUserData(userData));

        return userList;
    }
}