package service;

import java.net.URI;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;

import io.github.cdimascio.dotenv.Dotenv;

public class Twillio {

    

    public static String makeCall(String pn,String callbackUrl) {
        Dotenv dotenv = Dotenv.configure().load();

        Twilio.init(
            dotenv.get("TWILIO_ACCOUNT_SID"),
            dotenv.get("TWILIO_AUTH_TOKEN")
        );

        Call call = Call.creator(
            new PhoneNumber("+918459781390"),
            new PhoneNumber("+17372508034"),
            URI.create(
                callbackUrl
            )
        ).create();

        return call.getSid();
    }
}