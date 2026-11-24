package State;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import ChainOfResponsibility.Snack;
import ChainOfResponsibility.SnackDispenseHandler;

public class WaitingForMoneyStateTest {

    Snack coke = new Snack("Coke", 1.50, 2);
    Snack snickers = new Snack("Snickers", 1.75, 1);
    VendingMachine vendingMachine = createVendingMachine();
    WaitingForMoneyState waitingForMoneyState = (WaitingForMoneyState) vendingMachine.getWaitingForMoneyState();

    private VendingMachine createVendingMachine() {
        SnackDispenseHandler snackDispenser = new SnackDispenseHandler(coke);
        snackDispenser.setNextHandler(new SnackDispenseHandler(snickers));
        return new VendingMachine(snackDispenser);
    }

    @BeforeEach
    public void selectCoke() {
        captureOutput(() -> vendingMachine.selectSnack("Coke"));
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
        assertSame(vendingMachine, waitingForMoneyState.getVendingMachine());
        assertSame(vendingMachine, new WaitingForMoneyState(vendingMachine).getVendingMachine());
    }

    @Test
    public void testIsStateOfVendingMachine() {
        assertInstanceOf(StateOfVendingMachine.class, waitingForMoneyState);
    }

    @Test
    public void testSelectSnackKeepsFirstSelection() {
        captureOutput(() -> waitingForMoneyState.insertMoney(1.00));
        assertEquals("Coke is already selected. Please insert $0.50" + System.lineSeparator(),
                captureOutput(() -> waitingForMoneyState.selectSnack("Snickers")));
        assertSame(coke, vendingMachine.getSelectedSnack());
        assertSame(waitingForMoneyState, vendingMachine.getState());
    }

    @Test
    public void testInsertMoneyNotEnough() {
        assertEquals("Inserted $1.00. Total inserted: $1.00" + System.lineSeparator()
                + "Please insert $0.50 more" + System.lineSeparator(),
                captureOutput(() -> waitingForMoneyState.insertMoney(1.00)));
        assertEquals(1.00, vendingMachine.getInsertedMoney());
        assertSame(waitingForMoneyState, vendingMachine.getState());
        assertEquals(2, coke.getQuantity());
    }

    @Test
    public void testInsertMoneyMultipleTimes() {
        captureOutput(() -> {
            waitingForMoneyState.insertMoney(0.25);
            waitingForMoneyState.insertMoney(0.25);
        });
        assertEquals(0.50, vendingMachine.getInsertedMoney());
        assertEquals(1.00, vendingMachine.getAmountOwed());
    }

    @Test
    public void testInsertExactMoneyDispensesSnack() {
        assertEquals("Inserted $1.50. Total inserted: $1.50" + System.lineSeparator()
                + "Dispensed Coke" + System.lineSeparator(),
                captureOutput(() -> waitingForMoneyState.insertMoney(1.50)));
        assertEquals(1, coke.getQuantity());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
    }

    @Test
    public void testInsertExtraMoneyDispensesSnackWithChange() {
        captureOutput(() -> waitingForMoneyState.insertMoney(1.00));
        assertEquals("Inserted $1.00. Total inserted: $2.00" + System.lineSeparator()
                + "Dispensed Coke. Returning change $0.50" + System.lineSeparator(),
                captureOutput(() -> waitingForMoneyState.insertMoney(1.00)));
        assertEquals(1, coke.getQuantity());
        assertNull(vendingMachine.getSelectedSnack());
        assertEquals(0, vendingMachine.getInsertedMoney());
    }

    @Test
    public void testInsertMoneyForOutOfStockSnackReturnsMoney() {
        coke.setQuantity(0);
        assertEquals("Inserted $1.50. Total inserted: $1.50" + System.lineSeparator()
                + "Coke is out of stock. Returning $1.50" + System.lineSeparator(),
                captureOutput(() -> waitingForMoneyState.insertMoney(1.50)));
        assertEquals(0, vendingMachine.getInsertedMoney());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
    }

    @Test
    public void testDispenseSnackWithoutEnoughMoney() {
        captureOutput(() -> waitingForMoneyState.insertMoney(0.50));
        assertEquals("Please insert $1.00 more for Coke" + System.lineSeparator(),
                captureOutput(waitingForMoneyState::dispenseSnack));
        assertEquals(2, coke.getQuantity());
        assertSame(waitingForMoneyState, vendingMachine.getState());
    }

    @Test
    public void testCancelReturnsMoney() {
        captureOutput(() -> waitingForMoneyState.insertMoney(1.00));
        assertEquals("Cancelled Coke. Returning $1.00" + System.lineSeparator(),
                captureOutput(waitingForMoneyState::cancel));
        assertNull(vendingMachine.getSelectedSnack());
        assertEquals(0, vendingMachine.getInsertedMoney());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
        assertEquals(2, coke.getQuantity());
    }

    @Test
    public void testCancelWithNoMoneyInserted() {
        assertEquals("Cancelled Coke. Returning $0.00" + System.lineSeparator(),
                captureOutput(waitingForMoneyState::cancel));
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
    }

    @Test
    public void testToString() {
        assertEquals("Waiting for Money", waitingForMoneyState.toString());
    }
}
