import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import ChainOfResponsibility.SnackTest;
import ChainOfResponsibility.SnackDispenseHandlerTest;
import State.VendingMachineTest;
import State.IdleStateTest;

@Suite
@SelectClasses({SnackTest.class, SnackDispenseHandlerTest.class, VendingMachineTest.class, IdleStateTest.class})
public class CompleteUnitTest {

}
