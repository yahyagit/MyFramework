package framework.mail;

import java.util.ArrayList;

/**
 * Created by apaliy on 12/9/2014.
 */
public class MailHelpers {
    public static String getLinkToConfirmEmail(MailMessage mailMessage){
        String textMessage = mailMessage.getText();
        String subText = textMessage.substring(textMessage.indexOf("http://luckymailfiles.com/icecasino/may20/header.png"),
                textMessage.indexOf("http://luckymailfiles.com/icecasino/aug12/btn_ice_en.png"));
        String linkForComfirmEmail = subText.substring(subText.indexOf("href=\"") + 6,
                subText.indexOf("\" style=\"padding: 0; margin: 0 auto; display: block; border: 0; width: 326px; height: 52px\" target=\"_blank\">"));

        return linkForComfirmEmail;
    }

    public static MailMessage getMessageFormList(ArrayList<MailMessage> list, String sender, String subject) {
        MailMessage messageForCheck = null;
        for (MailMessage mailMessage: list) {
            if (mailMessage !=null && mailMessage.getSender().contains(sender) && mailMessage.getSubject().contains(subject)) {
                messageForCheck = mailMessage;
            }
        }
        return messageForCheck;
    }
}
