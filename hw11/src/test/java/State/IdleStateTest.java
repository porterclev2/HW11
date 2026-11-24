package State;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import ChainOfResponsibility.Snack;
import ChainOfResponsibility.SnackDispenseHandler;

public class IdleStateTest {

    Snack coke = new Snack("Coke", 1.50, 2);
    Snack snickers = new Snack("Snickers", 1.75, 0);
    VendingMachine vendingMachine = createVendingMachine();
    IdleState idleState = (IdleState) vendingMachine.getIdleState();

    private VendingMachine createVendingMachine() {
        SnackDispenseHandler snackDispenser = new SnackDispenseHandler(coke);
        snackDispenser.setNextHandler(new SnackDispenseHandler(snickers));
        return new VendingMachine(snackDispenser);
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    @Test
    public void testConstructorAndGetVendingMachine() {
        assertSame(vendingMachine, idleState.getVendingMachine());
        assertSame(vendingMachine, new IdleState(vendingMachine).getVendingMachine());
    }

    @Test
    public void testIsStateOfVendingMachine() {
        assertInstanceOf(StateOfVendingMachine.class, idleState);
    }

    @Test
    public void testSelectSnack() {
        assertEquals("Coke selected. Please insert $1.50" + System.lineSeparator(),
                captureOutput(() -> idleState.selectSnack("Coke")));
        assertSame(coke, vendingMachine.getSelectedSnack());
        assertSame(vendingMachine.getWaitingForMoneyState(), vendingMachine.getState());
    }

    @Test
    public void testSelectSnackIgnoresCase() {
        assertEquals("Coke selected. Please insert $1.50" + System.lineSeparator(),
                captureOutput(() -> idleState.selectSnack("coke")));
        assertSame(coke, vendingMachine.getSelectedSnack());
    }

    @Test
    public void testSelectSnackThatIsOutOfStockStillWaitsForMoney() {
        captureOutput(() -> idleState.selectSnack("Snickers"));
        assertSame(snickers, vendingMachine.getSelectedSnack());
        assertSame(vendingMachine.getWaitingForMoneyState(), vendingMachine.getState());
    }

    @Test
    public void testSelectSnackNotSold() {
        assertEquals("Water is not sold in this vending machine" + System.lineSeparator(),
                captureOutput(() -> idleState.selectSnack("Water")));
        assertNull(vendingMachine.getSelectedSnack());
        assertSame(idleState, vendingMachine.getState());
    }

    @Test
    public void testInsertMoneyReturnsMoney() {
        assertEquals("Please select a snack first. Returning $1.25" + System.lineSeparator(),
                captureOutput(() -> idleState.insertMoney(1.25)));
        assertEquals(0, vendingMachine.getInsertedMoney());
        assertSame(idleState, vendingMachine.getState());
    }

    @Test
    public void testDispenseSnack() {
        assertEquals("Please select a snack first" + System.lineSeparator(),
                captureOutput(idleState::dispenseSnack));
        assertEquals(2, coke.getQuantity());
        assertSame(idleState, vendingMachine.getState());
    }

    @Test
    public void testCancel() {
        assertEquals("There is nothing to cancel" + System.lineSeparator(),
                captureOutput(idleState::cancel));
        assertSame(idleState, vendingMachine.getState());
    }

    @Test
    public void testToString() {
        assertEquals("Idle", idleState.toString());
    }
}
