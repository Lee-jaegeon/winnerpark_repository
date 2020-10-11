package com.now9e0n.winnerpark;

import net.nurigo.java_sdk.api.Message;

import java.util.HashMap;

import lombok.Getter;
import lombok.SneakyThrows;

public class SendSMSClient {
    @Getter private static SendSMSClient instance = new SendSMSClient();

    private Message coolSMS;

    private HashMap<String, String> params;
    @Getter private String code;

    private SendSMSClient() {
        String apiKey = "NCSL7HZZRNDEW0B0";
        String apiSecret = "KA0UBKZJGEZ86ZMRI65SBLZC2FVWADRD";
        coolSMS = new Message(apiKey, apiSecret);

        params = new HashMap<>();
        params.put("from", "01067166386");
        params.put("type", "SMS");
    }

    public void sendSMS(String toPhoneNumber, Runnable runnable) {
        params.put("to", toPhoneNumber);

        code = createCode();
        String message = "[Winner Park]\n" + code + " is your verification code.";
        params.put("text", message);

        new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                coolSMS.send(params);
                runnable.run();
            }
        }.start();
    }

    private String createCode() {
        String[] str = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        StringBuilder newCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int random = (int) (Math.random() * str.length);
            newCode.append(str[random]);
        }

        return newCode.toString();
    }
}

