package com.now9e0n.winnerpark;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.util.Consumer;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSReceiver extends BroadcastReceiver {

    private Consumer<String> codeConsumer;

    public SMSReceiver(Consumer<String> codeConsumer) {
        this.codeConsumer = codeConsumer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS :
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    String code = message.replaceAll("\\D", "").substring(0, 4);
                    codeConsumer.accept(code);
                    break;

                case CommonStatusCodes.TIMEOUT :
                    break;
            }
        }
    }
}