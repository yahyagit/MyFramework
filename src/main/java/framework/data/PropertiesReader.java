package framework.data;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by apaliy on 1/6/2015.
 */
public class PropertiesReader {
    private static final String PROPS_FILE_NAME = "config.properties";

    private static PropertiesReader instance;
    private Properties properties;

    private PropertiesReader() {
        properties = new Properties();
        try {
            properties.load(FileUtils.openInputStream(new File(ClassLoader.getSystemResource(PROPS_FILE_NAME).toURI())));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesReader getInstance() {
        if (instance == null) {
            instance = new PropertiesReader();
        }
        return instance;
    }

    public Browser getBrowser() {
        return Browser.valueOf(properties.getProperty("system.browser").toUpperCase().trim());
    }

    public boolean isRemoteEnable() {
        return Boolean.parseBoolean(properties.getProperty("system.remote_enable"));
    }

    public URL getRemoteAddress() {
        try {
            return new URL(properties.getProperty("system.remote_address"));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public String getFirefoxProfilePath() {
        return properties.getProperty("system.firefox_profile_path");
    }

    public String getFirefoxBinaryPath() {
        return properties.getProperty("system.firefox_binary_path");
    }

    public boolean isTracingEnable() {
        return Boolean.parseBoolean(properties.getProperty("system.tracing_enable"));
    }

    /*
    BASE TESTS PROPS
     */

    public int getRetryCount() {
        return Integer.parseInt(properties.getProperty("tests.retry_count"));
    }

    public String getTestsOutputPath() {
        return properties.getProperty("tests.output_path");
    }

    public String getDownloadDirPath() {
        return properties.getProperty("tests.download_dir_path");
    }

    /*
    WAITERS
     */

    public int getImplicitlyWait() {
        return Integer.parseInt(properties.getProperty("tests.implicitly_wait"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(properties.getProperty("tests.page_load_timeout"));
    }

    public int getScriptTimeout() {
        return Integer.parseInt(properties.getProperty("tests.script_timeout"));
    }

    public int getMaxSleepTimeout() {
        return Integer.parseInt(properties.getProperty("tests.max_sleep_timeout"));
    }

    public int getMaxWaitForJob() {
        return Integer.parseInt(properties.getProperty("tests.max_wait_for_job"));
    }

    public int getJobDateDeviation() {
        return Integer.parseInt(properties.getProperty("tests.job_date_deviation"));
    }

    public int getMaxWaitForDelivery() {
        return Integer.parseInt(properties.getProperty("tests.max_wait_for_delivery"));
    }

    /*
    URLS
     */

    public String getBaseURL() {
        return properties.getProperty("tests.url.base");
    }

    /*
    FTP
     */

//    public String getFTPHost() {
//        return properties.getProperty("tests.ftp.host");
//    }
//
//    public int getFTPPort() {
//        return Integer.parseInt(properties.getProperty("tests.ftp.port"));
//    }
//
//    public String getFTPUser() {
//        return properties.getProperty("tests.ftp.user");
//    }
//
//    public String getFTPPass() {
//        return properties.getProperty("tests.ftp.pass");
//    }

    /*
    DATABASES
     */

//    public String getMSSQLHost() {
//        return properties.getProperty("tests.db.mssql.host");
//    }
//
//    public String getMSSQLUser() {
//        return properties.getProperty("tests.db.mssql.user");
//    }
//
//    public String getMSSQLPass() {
//        return properties.getProperty("tests.db.mssql.pass");
//    }
//
//    public String getSYBASEHost() {
//        return properties.getProperty("tests.db.sybase.host");
//    }
//
//    public String getSYBASEUser() {
//        return properties.getProperty("tests.db.sybase.user");
//    }
//
//    public String getSYBASEPass() {
//        return properties.getProperty("tests.db.sybase.pass");
//    }

    /*
    USERS
     */

//    public ERPUser getAdminUser() {
//        return getUser("admin");
//    }
//
//    public ERPUser getUser1() {
//        return getUser("user1");
//    }
//
//    public ERPUser getUser2() {
//        return getUser("user2");
//    }
//
//    public ERPUser getUser3() {
//        return getUser("user3");
//    }

    /*
    SSH
     */
//
//    public String getSshHost() {
//        return properties.getProperty("ssh.host");
//    }
//
//    public String getSshLogin() {
//        return properties.getProperty("ssh.login");
//    }
//
//    public String getSshPassword() {
//        return properties.getProperty("ssh.password");
//    }

     /*
    Exchange mail
     */

    public String getExchangeURL() {
        return properties.getProperty("exchange.url");
    }

    public String getExchangeUserName() {
        return properties.getProperty("exchange.user.name");
    }

    public String getExchangePassword() {
        return properties.getProperty("exchange.password");
    }

    /*
    PRIVATES
     */

//    private ERPUser getUser(String innerId) {
//        String[] propsArray = Utils.parseStringWithSeparator(properties.getProperty("tests.user." + innerId), ",");
//        List<UserRole> roles = new ArrayList<UserRole>();
//
//        String rolesInString = propsArray[5];
//
//        if (!rolesInString.equals("")) {
//            for (String role : Utils.parseStringWithSeparator(rolesInString.replace("\"", ""), ",")) {
//                roles.add(UserRole.valueOf(role));
//            }
//        }
//
//        return new ERPUser.Builder(propsArray[0], propsArray[1]).
//                id(propsArray[2]).
//                mail(propsArray[3]).
//                organization(propsArray[4]).
//                roles(roles).
//                build();
//    }
}
