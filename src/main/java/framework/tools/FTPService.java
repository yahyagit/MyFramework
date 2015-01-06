package framework.tools;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class FTPService {
    private final String hostName;
    private final int port;
    private final String userName;
    private final String passWord;
    private FTPClient client;

    public FTPService(String hostName, int port, String userName, String passWord) {
        client = new FTPClient();
        this.hostName = hostName;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
    }

    public List<FTPFile> getFiles(String pathToFolder) {
        try {
            client.connect(hostName, port);
            client.login(userName, passWord);
            client.changeWorkingDirectory(pathToFolder);
            return Arrays.asList(client.listFiles());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public File downloadFile(String pathToFolder, String downloadPath, String fileName) {
        File resultFile = null;
        OutputStream stream = null;
        try {
            resultFile = new File(downloadPath, fileName);

            if (resultFile.exists()) {
                throw new RuntimeException(downloadPath + "/" + fileName + " exist!");
            }

            client.connect(hostName, port);
            client.login(userName, passWord);
            client.changeWorkingDirectory(pathToFolder);

            stream = new BufferedOutputStream(new FileOutputStream(resultFile));

            if (!client.retrieveFile(fileName, stream)) {
                resultFile.delete();
                resultFile = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultFile;
    }

    public File downloadFile(String fullName, String downloadPath){
        String fileName = fullName.substring(fullName.lastIndexOf('/')+1);
        String pathToFolder = fullName.substring(0, fullName.lastIndexOf('/'));
        return downloadFile(pathToFolder, downloadPath, fileName);
    }

    public void putFile(File file, String pathToFolder) {
        try {
            client.connect(hostName, port);
            client.login(userName, passWord);
            client.changeWorkingDirectory(pathToFolder);
            client.appendFile(file.getName(), new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteFile(String pathToFolder, String fileName) {
        try {
            client.connect(hostName, port);
            client.login(userName, passWord);
            client.changeWorkingDirectory(pathToFolder);
            return client.deleteFile(pathToFolder + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
