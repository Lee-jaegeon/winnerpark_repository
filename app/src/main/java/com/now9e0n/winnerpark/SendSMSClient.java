package com.now9e0n.winnerpark;

import net.nurigo.java_sdk.api.Message;

import java.util.HashMap;

import lombok.Getter;
import lombok.SneakyThrows;

public class SendSMSClient {
    @Getter private static SendSMSClient instance = new SendSMSClient();

    private Message coolSMS;

    private HashMap<String, String> params;
    private String message;
    @Getter private String code;

    private SendSMSClient() {
        String apiKey = "NCSEQYOMRUHCX7HF";
        String apiSecret = "BGJNK4K5NSHJ86D3IFO2D7L1LRTMANSX";
        coolSMS = new Message(apiKey, apiSecret);

        params = new HashMap<>();
        params.put("from", "01067166386");
        params.put("type", "SMS");

        String appHash = "M+HJuMIR4LC";
        message = "<#> [Winner Park]\n" +"#" + " is your verification code.\n" + appHash;
    }

    public void sendSMS(String toPhoneNumber, Runnable runnable) {
        params.put("to", toPhoneNumber);

        code = createCode();
        params.put("text", message.replace("#", code));

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
        for (int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * str.length);
            newCode.append(str[random]);
        }

        return newCode.toString();
    }
}