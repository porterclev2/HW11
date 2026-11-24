package State;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ChainOfResponsibility.Snack;
import ChainOfResponsibility.SnackDispenseHandler;

public class VendingMachineTest {

    Snack coke = new Snack("Coke", 1.50, 2);
    Snack snickers = new Snack("Snickers", 1.75, 1);
    SnackDispenseHandler snackDispenser = new SnackDispenseHandler(coke);
    VendingMachine vendingMachine = createVendingMachine();

    private VendingMachine createVendingMachine() {
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
    public void testConstructorStartsIdle() {
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
        assertNull(vendingMachine.getSelectedSnack());
    }

    @Test
    public void testConstructorWithoutSnackDispenserThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new VendingMachine(null));
        assertEquals("A vending machine needs a snack dispenser", exception.getMessage());
    }

    @Test
    public void testGetIdleState() {
        assertInstanceOf(IdleState.class, vendingMachine.getIdleState());
        assertSame(vendingMachine, ((IdleState) vendingMachine.getIdleState()).getVendingMachine());
    }

    @Test
    public void testSetState() {
        IdleState otherState = new IdleState(vendingMachine);
        vendingMachine.setState(otherState);
        assertSame(otherState, vendingMachine.getState());
    }

    @Test
    public void testGetSnackDispenser() {
        assertSame(snackDispenser, vendingMachine.getSnackDispenser());
    }

    @Test
    public void testGetSnacksHoldsEverySnackInChain() {
        assertEquals(List.of(coke, snickers), vendingMachine.getSnacks());
    }

    @Test
    public void testGetSnacksIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> vendingMachine.getSnacks().add(coke));
    }

    @Test
    public void testGetSnack() {
        assertSame(coke, vendingMachine.getSnack("Coke"));
        assertSame(snickers, vendingMachine.getSnack("snickers"));
        assertNull(vendingMachine.getSnack("Water"));
    }

    @Test
    public void testSetSelectedSnack() {
        vendingMachine.setSelectedSnack(coke);
        assertSame(coke, vendingMachine.getSelectedSnack());
    }

    @Test
    public void testSelectSnackDelegatesToState() {
        assertEquals("Coke selected. Please insert $1.50" + System.lineSeparator(),
                captureOutput(() -> vendingMachine.selectSnack("Coke")));
        assertSame(coke, vendingMachine.getSelectedSnack());
    }

    @Test
    public void testInsertMoneyDelegatesToState() {
        assertEquals("Please select a snack first. Returning $1.00" + System.lineSeparator(),
                captureOutput(() -> vendingMachine.insertMoney(1.00)));
    }

    @Test
    public void testInsertInvalidMoneyThrows() {
        assertThrows(IllegalArgumentException.class, () -> vendingMachine.insertMoney(0));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> vendingMachine.insertMoney(-1.00));
        assertEquals("Amount inserted must be positive", exception.getMessage());
    }

    @Test
    public void testDispenseSnackDelegatesToState() {
        assertEquals("Please select a snack first" + System.lineSeparator(),
                captureOutput(vendingMachine::dispenseSnack));
    }

    @Test
    public void testToString() {
        assertEquals("Vending machine (Idle):" + System.lineSeparator()
                + "Coke - $1.50 (2 left)" + System.lineSeparator()
                + "Snickers - $1.75 (1 left)", vendingMachine.toString());
    }
}
