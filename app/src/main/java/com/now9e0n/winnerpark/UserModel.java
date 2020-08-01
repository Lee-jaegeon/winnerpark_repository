package com.now9e0n.winnerpark;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserModel {
    @Builder.Default private String name = "";
}