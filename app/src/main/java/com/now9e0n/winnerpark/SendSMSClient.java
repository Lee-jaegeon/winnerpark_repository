package com.now9e0n.winnerpark;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import lombok.SneakyThrows;

import static com.now9e0n.winnerpark.AppManager.getCurrentDate;

public class SendSMSClient {

    @Getter private static SendSMSClient instance = new SendSMSClient();

    private Coolsms coolSMS;

    private HashMap<String, String> params;
    private String message;
    @Getter private String code;

    private JSONObject result;
    @Getter private Date date;

    private SendSMSClient() {
        String apiKey = "NCSEQYOMRUHCX7HF";
        String apiSecret = "OAL8XIMQC75PXIDPOGAOMSCPYQG52P3K";
        coolSMS = new Coolsms(apiKey, apiSecret);

        params = new HashMap<>();
        params.put("from", "01067166386");
        params.put("type", "SMS");

        String appHash = "M+HJuMIR4LC";
        message = "<#> [Winner Park]\nYour verification code is " + "temp.\n" + appHash;
    }

    public void sendSMS(String toPhoneNumber, Runnable runnable) {
        params.put("to", toPhoneNumber);

        code = createCode();
        params.put("text", message.replace("temp", code));

        TimerTask task = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                if (sentSMS()) new Handler(Looper.getMainLooper()).post(runnable);
            }
        };

        new Thread() {
            @Override
            @SneakyThrows
            public void run() {
                result = coolSMS.send(params);
                new Timer().schedule(task, 0, 500);
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

    private boolean sentSMS() {
        if ((Boolean) result.get("status")) {
            String groupId = (String) result.get("group_id");
            HashMap<String, String> params = new HashMap<>();
            params.put("gid", groupId);

            result = coolSMS.sent(params);
            if((Boolean) result.get("status")) {
                JSONArray data = (JSONArray) result.get("data");
                for (int i = 0; i < data.size(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);
                    int status = Integer.parseInt((String) obj.get("status"));
                    if (status != 0) {
                        date = getCurrentDate(Date.class);
                        return true;
                    }
                }

                Log.e("CoolSMS", "Sent not Completed");
            }

            else Log.e("CoolSMS", "Sent SMS Failed");
        }

        else Log.e("CoolSMS", "Send SMS Failed");

        return false;
    }
}