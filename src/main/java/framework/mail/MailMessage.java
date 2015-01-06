package framework.mail;

import java.util.Date;

/**
 * Created by apaliy on 12/8/2014.
 */
public class MailMessage {
    private String subject;
    private String sender;
    private Date date;
    private String text;

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


    @Override
    public String toString() {
        return "MailMessage{" +
                "subject='" + subject + '\'' +
                ", sender='" + sender + '\'' +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MailMessage)) return false;

        MailMessage that = (MailMessage) o;

        if (!date.equals(that.date)) return false;
        if (!sender.equals(that.sender)) return false;
        if (!subject.equals(that.subject)) return false;
        if (!text.equals(that.text)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
}
