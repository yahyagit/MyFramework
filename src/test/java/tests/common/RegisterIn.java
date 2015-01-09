package tests.common;

import org.testng.annotations.Test;
import tests.BaseTest;
import ui.pages.GoogleMainPage;
import utils.WaitUtils;


/**
 * Created by apaliy on 12/4/2014.
 */
//extends BaseTest
public class RegisterIn extends BaseTest {

    @Test
    public void test() throws InterruptedException {

        GoogleMainPage googleMainPage = new GoogleMainPage();
        googleMainPage.setSearchField("Rozetka");
        googleMainPage.clickSearchButton();
        WaitUtils.sleep(10);
    }
}
