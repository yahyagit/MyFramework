package framework.reporter;

import framework.Service;
import framework.data.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ActionUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.testng.Reporter.log;

/**
 * Created by apaliy on 1/6/2015.
 */
public class TestLog {
    private static final int SCREENSHOT_WIDTH       = 300;
    private static final int SCREENSHOT_HEIGHT      = 180;
    private static final String FULL_TEST_NAME      = "TEST NAME       ==> ";
    private static final String PRECONDITIONS       = "PRECONDITIONS   ==> ";
    private static final String EXPECTED_RESULT     = "EXPECTED RESULT ==> ";
    private static final String STEP                = "STEP    :: ";
    private static final String ERROR               = "ERROR   :: ";
    private static final String WARNING             = "WARNING :: ";
    private static final String INFO                = "INFO    :: ";

    private static Logger logger = LoggerFactory.getLogger("TestLog");

    public static void initReporter() {
        System.setProperty("org.uncommons.reportng.escape-output", "false");
        System.setProperty("org.uncommons.reportng.stylesheet", "src/main/resources/reportng.css");
    }

    public static void fullTestName(String text) {
        log("<div class='testName'>" + FULL_TEST_NAME + text + "</div>");
        logger.info(FULL_TEST_NAME + text);
    }

    public static void preconditions(String text) {
        log("<div class='preconditions'>" + PRECONDITIONS + text + "</div>");
        logger.info(PRECONDITIONS + text);
    }

    public static void expectedResult(String text) {
        log("<div class='expectedResult'>" + EXPECTED_RESULT + text + "</div>");
        logger.info(EXPECTED_RESULT + text);
    }

    public static void step(String text) {
        log("<div class='testStep'>" + STEP + getTimestamp() + " :: " + text + "</div>");
        logger.info(STEP + text);
    }

    public static void info(String text) {
        log("<div class='info'>" + INFO + getTimestamp() + " :: " + text + "</div>");
        logger.info(INFO + text);
    }

    public static void error(String text) {
        log("<div class='error'>" + ERROR + getTimestamp() + " :: " + text + "</div>");
        logger.info(ERROR + text);
    }

    public static void errorWithStackTrace(String header, String message, String stackTrace) {
        String id = "errorWithST-" + new Random().nextInt();
        log(
                "<a title=\"Click to expand/collapse\" href=\"javascript:toggleElement('" + id + "', 'block')\">" +
                        header +
                        "</a>" +
                        "<br>" +
                        "<div id=\"" + id + "\" class=\"error\" style=\"display: none;\">" +
                        "MESSAGE : " + message +
                        "<br>" +
                        "STACKTRACE : " + stackTrace +
                        "</div>"
        );
        logger.info(ERROR + message + "\n" + stackTrace);
    }

    public static void warning(String text) {
        log("<div class='warning'>" + WARNING + getTimestamp() + " :: " + text + "</div>");
        logger.info(WARNING + text);
    }

    public static void comment(String text) {
        log("<div class='comment'>" + text + "</div>");
        logger.info(text);
    }

    public static void takeAndPasteScreenshot() {
        String date = new SimpleDateFormat("yyyy_dd_MM__HH_mm_ss_SSS").format(Calendar.getInstance().getTime());
        String fileName = date + "_screenshot.png";
        ActionUtils.takeScreenshot(PropertiesReader.getInstance().getTestsOutputPath() +
                "/html/screenshots/" + fileName);

        log(getImageTag(fileName, false));
    }

    public static void pasteRandomImage() {
        String storePath = PropertiesReader.getInstance().getTestsOutputPath() + "/html/img/";
        String resourcesPath = "src/main/resources/img";

        List<File> imgList = new ArrayList(listFiles(new File(resourcesPath), new String[]{"gif"}, false));

        File randomImgFile = imgList.get(new Random().nextInt(imgList.size()));
        String randomImgFileName = randomImgFile.getName();

        try {
            File storePathDir = new File(storePath);

            if (!storePathDir.exists()) {
                forceMkdir(storePathDir);
            }

            copyFileToDirectory(randomImgFile, storePathDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        log(getImageTag(randomImgFileName, true));
    }

    public static void savePageSource(){
        String date = new SimpleDateFormat("yyyy_dd_MM__HH_mm_ss_SSS").format(Calendar.getInstance().getTime());
        String fileName = date + ".txt";
        try {
            writeStringToFile(new File(PropertiesReader.getInstance().getTestsOutputPath() + "/html/pagesources/", fileName),
                    Service.getInstance().getCurrentDriver().getPageSource());
        } catch (IOException e) {
            warning("Error happened when saving page source");
        }
        log("<a href=\"pagesources/" + fileName + "\">" + "Get page source" + "</a><br>");
    }

    private static String getImageTag(String fileName, boolean isGif) {
        return  isGif ?
                "<a href=\"img/" + fileName + "\">" +
                        "<img src=\"img/" + fileName + "\" alt=\"\"/></a><br>" :
                "<a href=\"screenshots/" + fileName + "\">" +
                        "<img src=\"screenshots/" + fileName + "\" " +
                        "alt=\"\" height='" + SCREENSHOT_HEIGHT + " 'width='" + SCREENSHOT_WIDTH + "'/></a><br>";
    }

    private static String getTimestamp() {
        return new SimpleDateFormat("HH:mm:ss,SSS").format(new Date());
    }
}
