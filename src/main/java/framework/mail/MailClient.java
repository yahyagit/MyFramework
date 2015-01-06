package framework.mail;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by apaliy on 12/8/2014.
 */
public class MailClient {
    private static final String SSL = "ssl";
    private static final String TLS = "tls";

    private static final String MAIL_HOST = "mail.smtp.host";
    private static final String MAIL_PORT = "mail.smtp.port";
    private static final String MAIL_AUTH = "mail.smtp.auth";
    private static final String MAIL_STORE = "mail.store.protocol";
    private static final String MAIL_TLS = "mail.smtp.starttls.enable";
    private static final String MAIL_SOCKET_PORT = "mail.smtp.socketFactory.port";
    private static final String MAIL_SOCKET_FACTORY = "mail.smtp.socketFactory.class";

    private static final String SSL_SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String TRUE = "true";
    private static final String IMAPS = "imaps";

    private static final String MIME_TEXT= "text/*";
    private static final String MIME_ALTERNATIVE = "multipart/alternative";
    private static final String MIME_MULTI_PART = "multipart/*";

    private String smtpServer;
    private int smtpPort;
    private String imapServer;
    private int imapPort;
    private String securityProtocol;
    private String userMail;
    private String password;

    public MailClient(String smtpServer, String imapServer, String userMail, String password) {
        this.userMail = userMail;
        this.password = password;
        this.smtpServer = smtpServer;
        this.smtpPort = 465;
        this.imapServer = imapServer;
        this.imapPort = 993;
        this.securityProtocol = "SSL/TLS";
    }

    public ArrayList<MailMessage> getAllMessagesFromFolder(String storeName) {
        return getMessages(storeName, 0, true);
    }

    public ArrayList<MailMessage> getLastMessagesFromFolder(String storeName, int count) {
        return getMessages(storeName, count, false);
    }

    public void sendMail(final String recipientMail, final String subject, final String text) {
        try {
            Transport.send(new MimeMessage(getSession()) {{
                setFrom(new InternetAddress(userMail));
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientMail));
                setSubject(subject);
                setText(text);
            }});
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<MailMessage> getMessages(String storeName, int count, boolean all) {
        ArrayList<MailMessage> mailMessages = new ArrayList<MailMessage>();
        Store store = null;
        Folder folder = null;
        try {
            store = getSession().getStore(IMAPS);
            store.connect(imapServer, imapPort, userMail, password);
            System.out.println("Folders: " + Arrays.toString(store.getDefaultFolder().list()));
            folder = store.getFolder(storeName);
            folder.open(Folder.READ_ONLY);
            int countTotal = folder.getMessageCount();
            if (all) {
                count = countTotal - 1;
            }
            Message[] messages = folder.getMessages(countTotal - count, countTotal);
            for (final Message message : messages) {
                mailMessages.add(getMailMessage(message));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (store != null && folder != null) {
                    folder.close(true);
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return mailMessages;
    }

    private MailMessage getMailMessage(final Message message) throws MessagingException {
        MailMessage mailMessage = null;
        try {
            mailMessage = new MailMessage(){{
                setSubject(message.getSubject());
                setSender(message.getFrom()[0].toString());
                setDate(message.getReceivedDate());
                setText(getTextFromMessage(message.getContent()));
            }};
        } catch (MessagingException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unable to load bodystructure")) {
                MimeMessage msgDownloaded = new MimeMessage((MimeMessage) message);
                mailMessage = getMailMessage(msgDownloaded);
            } else {
                throw e;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mailMessage;
    }

    private String getTextFromMessage(Object content) {
        String text = "";
        try {
            if (content instanceof Multipart) {
                Multipart parts = (Multipart) content;
                for (int i = 0; i < parts.getCount(); i++) {
                    text += getTextFromPart(parts.getBodyPart(i));
                }
            } else {
                text = (String) content;
            }
        } catch (MessagingException e) {

            e.printStackTrace();
        }
        return text;
    }

    private String getTextFromPart(Part part) {
        String text = "";
        try {
            if (part.isMimeType(MIME_TEXT)) {
                text = (String) part.getContent();
            } else if (part.isMimeType(MIME_ALTERNATIVE) || part.isMimeType(MIME_MULTI_PART)) {
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    text += getTextFromPart(multipart.getBodyPart(i));
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private Session getSession() {
        return Session.getInstance(initProps(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userMail, password);
            }
        });
    }

    private Properties initProps() {
        Properties properties = new Properties();
        properties.put(MAIL_HOST, smtpServer);
        properties.put(MAIL_PORT, smtpPort);
        properties.put(MAIL_AUTH, TRUE);
        properties.put(MAIL_STORE, IMAPS);

        if (securityProtocol.equals(SSL)) {
            properties.put(MAIL_SOCKET_PORT, smtpPort);
            properties.put(MAIL_SOCKET_FACTORY, SSL_SOCKET_FACTORY);
        } else if (securityProtocol.equals(TLS)) {
            properties.put(MAIL_TLS, TRUE);
        }
        return properties;
    }
}
