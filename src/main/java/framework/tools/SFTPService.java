package framework.tools;

import com.jcraft.jsch.*;

import java.util.Properties;

public class SFTPService {
    private String host;
    private int port;
    private String username;
    private String password;

    public SFTPService(String host, int port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void downloadFile(String remoteFileName, String localFileName){
        JSch jsch = new JSch();
        Session session = null;
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig(properties);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(remoteFileName, localFileName);
            sftpChannel.exit();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        finally {
            session.disconnect();
        }
    }
}
