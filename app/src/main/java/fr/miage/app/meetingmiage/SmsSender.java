package fr.miage.app.meetingmiage;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SmsSender {

    // send SMS se déclanche lorsque l'on click sur le bouton d'envoi des coordonnées
    static void sendSMS(String numero, String message) {
        SmsManager.getDefault().sendTextMessage(numero, null, message, null, null);
    }

    static boolean isValidNumber(Context context, String numero) {
        // compilation de la regex du numero de telephone français
        Pattern p = Pattern.compile("^(?:(?:\\+|00)33[\\s.-]{0,3}(?:\\(0\\)[\\s.-]{0,3})?|0)[1-9](?:(?:[\\s.-]?\\d{2}){4}|\\d{2}(?:[\\s.-]?\\d{3}){2})$");
        // création d'un moteur de recherche
        Matcher m = p.matcher(numero);
        // lancement de la recherche de toutes les occurrences
        boolean b = m.matches();
        // si recherche infructueuse
        if (!b) {
            return false;
        }
        return true;
    }
}