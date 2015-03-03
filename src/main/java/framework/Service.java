package framework;

import framework.data.PropertiesReader;
import framework.tracingwebdriver.TracingWebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ThreadGuard;
import utils.WaitUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by apaliy on 12/4/2014.
 */

public final class Service {
    private static ThreadLocal<Service> THREAD_LOCAL = new ThreadLocal<Service>() {
        @Override
        public Service initialValue() {
            return new Service();
        }
    };

    private WebDriver driver;
    private PropertiesReader properties;

    private Service() {
        properties = PropertiesReader.getInstance();
    }

    public static Service getInstance() {
        return THREAD_LOCAL.get();
    }

    public WebDriver getCurrentDriver() {
        if (driver != null) {
            return driver;
        } else {
            throw new WebDriverException("WebDriver was not initialized!");
        }
    }

    public void initDriver() {
        DesiredCapabilities desiredCapabilities;

        switch (properties.getBrowser()) {
            case FIREFOX:
                if (!properties.isRemoteEnable()) driver = getFirefoxDriver();
                desiredCapabilities = DesiredCapabilities.firefox();
                break;
            case CHROME:
                if (!properties.isRemoteEnable()) driver = getChromeDriver();
                desiredCapabilities = DesiredCapabilities.chrome();
                break;
            case IEXPLORER:
                if (!properties.isRemoteEnable()) driver = getInternetExplorerDriver();
                desiredCapabilities = DesiredCapabilities.internetExplorer();
                break;
            case SAFARI:
                if (!properties.isRemoteEnable()) driver = getSafariDriver();
                desiredCapabilities = DesiredCapabilities.safari();
                break;
            default:
                throw new WebDriverException("Non correct browser name!");
        }

        if (properties.isRemoteEnable()) {
            driver = ThreadGuard.protect(getRemoteWebDriver(desiredCapabilities));
        }

        if (properties.isTracingEnable()) {
            driver = new TracingWebDriver(driver);
        }

        enableTimeouts();
        driver.manage().window().maximize();
    }

    public void enableTimeouts() {
        setTimeouts(properties.getImplicitlyWait(), properties.getPageLoadTimeout(), properties.getScriptTimeout());
    }

    public void disableTimeouts() {
        setTimeouts(0, 0, 0);
    }

    public void terminateDriver() {
        driver.quit();
        driver = null;
    }

    public JavascriptExecutor getJsExecutor() {
        return (JavascriptExecutor) driver;
    }

    private WebDriver getRemoteWebDriver(DesiredCapabilities desiredCapabilities) {
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(properties.getRemoteAddress(), desiredCapabilities);
        remoteWebDriver.setFileDetector(new LocalFileDetector());
        return new Augmenter().augment(remoteWebDriver);
    }

    private FirefoxDriver getFirefoxDriver() {
        String pathToBinary = properties.getFirefoxBinaryPath();
        String profileFileName = properties.getFirefoxProfilePath();
        if (pathToBinary == null || "".equals(pathToBinary)) {
            if (profileFileName == null || "".equals(profileFileName)) {
                return new FirefoxDriver();
            } else {
                return new FirefoxDriver(new FirefoxProfile(new File(profileFileName)));
            }
        } else {
            FirefoxBinary firefoxBinary = new FirefoxBinary(new File(pathToBinary));
            if (profileFileName == null || "".equals(profileFileName)) {
                DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
                desiredCapabilities.setCapability("firefox_binary", pathToBinary);
                return new FirefoxDriver(desiredCapabilities);
            } else {
                return new FirefoxDriver(firefoxBinary, new FirefoxProfile(new File(profileFileName)));
            }
        }
    }

    private ChromeDriver getChromeDriver() {
        Platform platform = Platform.getCurrent();
        String arch = System.getProperties().getProperty("os.arch");
        String key = "webdriver.chrome.driver";
        String driverPath = "chromedriver";

        switch (platform) {
            case WINDOWS:
            case XP:
            case VISTA:
            case WIN8:
                driverPath += "\\win32.exe";
                break;
            case LINUX:
            case UNIX:
                if (arch.contains("86")) {
                    driverPath += "/linux32";
                } else {
                    driverPath += "/linux64";
                }
                break;
            case MAC:
                driverPath += "/mac32";
                break;
            default:
                throw new WebDriverException("Non correct os or arch name!");
        }

        System.setProperty(key, driverPath);
        return new ChromeDriver();
    }

    private InternetExplorerDriver getInternetExplorerDriver() {
        return new InternetExplorerDriver();
    }

    private SafariDriver getSafariDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.safari();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setJavascriptEnabled(true);
        return new SafariDriver(capabilities);
    }

    private void setTimeouts(int implicitlyWait, int pageLoadTimeout, int setScriptTimeout) {
        try {
            driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(setScriptTimeout, TimeUnit.SECONDS);
        } catch (WebDriverException e) {
            if (!e.getMessage().contains("JavaScript Error:")) {
                throw e;
            }
            WaitUtils.sleep(1);
            setTimeouts(implicitlyWait, pageLoadTimeout, setScriptTimeout);
        }
    }
}
