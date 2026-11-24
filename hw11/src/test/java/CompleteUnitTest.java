import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import ChainOfResponsibility.SnackTest;
import ChainOfResponsibility.SnackDispenseHandlerTest;

@Suite
@SelectClasses({SnackTest.class, SnackDispenseHandlerTest.class})
public class CompleteUnitTest {

}
