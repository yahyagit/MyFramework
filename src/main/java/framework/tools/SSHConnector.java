package framework.tools;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import framework.data.PropertiesReader;

import java.io.*;

public class SSHConnector {
    private String host = PropertiesReader.getInstance().getSshHost();
    private String username = PropertiesReader.getInstance().getSshLogin();
    private String password = PropertiesReader.getInstance().getSshPassword();
    private OutputStreamWriter writer = null;
    private Connection conn = null;
    private Session sess = null;

    public void writeCommand(String s) {
        try {
            writer.write(s + "\n");
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        if (writer == null) return;
        try {
            writer.close();
            sess.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (writer != null) writer = null;
    }

    public void run(String[] commands, String outputLogPath, String errorLogPath, long timeout) {  //200000
        try {
            int bufferSize = 4096;
            conn = new Connection(host);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if (!isAuthenticated) throw new IOException("Authentication failed.");
            sess = conn.openSession();
            new Thread(new SyncPipe(sess.getStderr(), System.err, bufferSize, errorLogPath)).start();
            new Thread(new SyncPipe(sess.getStdout(), System.out, bufferSize, outputLogPath)).start();
            sess.requestPTY("bash");
            sess.startShell();

            writer = new OutputStreamWriter(sess.getStdin(), "utf-8");
            for (int i = 0; i < commands.length; i++) {
                writeCommand(commands[i]);
            }
            sess.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF |
                    ChannelCondition.EXIT_STATUS, timeout);
            //System.out.println("Exit status : " + sess.getExitStatus());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }


    private class SyncPipe implements Runnable {
        private final byte[] buffer;
        private final OutputStream ostrm;
        private final InputStream istrm;
        private boolean closeAfterCopy = false;
        private String logFilePath;

        public SyncPipe(InputStream istrm, OutputStream ostrm, int bufferSize, String logFilePath) {
            if (istrm == null) throw new IllegalArgumentException("'istrm' cannot be null");
            if (ostrm == null) throw new IllegalArgumentException("'ostrm' cannot be null");
            if (bufferSize < 1024)
                throw new IllegalArgumentException("a buffer size less than 1024 makes little sense");
            this.istrm = istrm;
            this.ostrm = ostrm;
            this.buffer = new byte[bufferSize];
            this.logFilePath = logFilePath;
        }

        public void handleException(IOException e) {
            e.printStackTrace();
        }

        public SyncPipe setCloseAfterCopy(boolean closeAfterCopy) {
            this.closeAfterCopy = closeAfterCopy;
            return this;
        }

        public void run() {
            String outputFilePath = this.logFilePath;
            File outputFile = new File(outputFilePath);
            OutputStream fileIn;

            try {
                fileIn = new FileOutputStream(outputFile, true);
                for (int bytesRead = 0; (bytesRead = istrm.read(buffer)) != -1; ) {
                    ostrm.write(buffer, 0, bytesRead);
                    fileIn.write(buffer, 0, bytesRead);
                    ostrm.flush();
                    fileIn.flush();
                }
                if (closeAfterCopy) ostrm.close();
                if (closeAfterCopy) fileIn.close();
            } catch (IOException e) {
                handleException(e);
            }
        }
    }
}
