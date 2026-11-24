import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import ChainOfResponsibility.SnackTest;
import ChainOfResponsibility.SnackDispenseHandlerTest;
import State.VendingMachineTest;
import State.IdleStateTest;
import State.WaitingForMoneyStateTest;
import State.DispensingSnackStateTest;
import State.VendingMachineDriverTest;

@Suite
@SelectClasses({SnackTest.class, SnackDispenseHandlerTest.class, VendingMachineTest.class, IdleStateTest.class, WaitingForMoneyStateTest.class, DispensingSnackStateTest.class, VendingMachineDriverTest.class})
public class CompleteUnitTest {

}
