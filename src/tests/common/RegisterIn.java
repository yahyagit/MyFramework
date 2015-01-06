package tests.common;

import framework.pagemapping.pagefactory.FindInMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.htmlelements.element.TextInput;
import tests.BaseTests;


/**
 * Created by apaliy on 12/4/2014.
 */
//extends BaseTest
public class RegisterIn extends BaseTests {
   @FindInMap private TextInput textInput;



    @BeforeClass
    public void setUpClass() {


    }

    @Test
    public void testRegister() throws InterruptedException {
        textInput.clear();
    }
}
