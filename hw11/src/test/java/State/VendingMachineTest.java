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
        assertEquals(0, vendingMachine.getInsertedMoney());
    }

    @Test
    public void testConstructorWithoutSnackDispenserThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new VendingMachine(null));
        assertEquals("A vending machine needs a snack dispenser", exception.getMessage());
    }

    @Test
    public void testGetStates() {
        assertInstanceOf(IdleState.class, vendingMachine.getIdleState());
        assertInstanceOf(WaitingForMoneyState.class, vendingMachine.getWaitingForMoneyState());
        assertInstanceOf(DispensingSnackState.class, vendingMachine.getDispensingSnackState());
        assertSame(vendingMachine, ((IdleState) vendingMachine.getIdleState()).getVendingMachine());
        assertSame(vendingMachine, ((WaitingForMoneyState) vendingMachine.getWaitingForMoneyState()).getVendingMachine());
        assertSame(vendingMachine, ((DispensingSnackState) vendingMachine.getDispensingSnackState()).getVendingMachine());
    }

    @Test
    public void testSetState() {
        vendingMachine.setState(vendingMachine.getDispensingSnackState());
        assertSame(vendingMachine.getDispensingSnackState(), vendingMachine.getState());
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
    public void testAddMoney() {
        vendingMachine.addMoney(1.00);
        vendingMachine.addMoney(0.25);
        assertEquals(1.25, vendingMachine.getInsertedMoney());
    }

    @Test
    public void testAddMoneyRoundsToCents() {
        vendingMachine.addMoney(0.10);
        vendingMachine.addMoney(0.20);
        assertEquals(0.30, vendingMachine.getInsertedMoney());
    }

    @Test
    public void testGetAmountOwed() {
        assertEquals(0, vendingMachine.getAmountOwed());
        vendingMachine.setSelectedSnack(coke);
        assertEquals(1.50, vendingMachine.getAmountOwed());
        vendingMachine.addMoney(1.10);
        assertEquals(0.40, vendingMachine.getAmountOwed());
        vendingMachine.addMoney(1.00);
        assertEquals(0, vendingMachine.getAmountOwed());
    }

    @Test
    public void testGetChange() {
        assertEquals(0, vendingMachine.getChange());
        vendingMachine.setSelectedSnack(coke);
        vendingMachine.addMoney(1.00);
        assertEquals(0, vendingMachine.getChange());
        vendingMachine.addMoney(1.20);
        assertEquals(0.70, vendingMachine.getChange());
    }

    @Test
    public void testReset() {
        vendingMachine.setSelectedSnack(coke);
        vendingMachine.addMoney(1.00);
        vendingMachine.reset();
        assertNull(vendingMachine.getSelectedSnack());
        assertEquals(0, vendingMachine.getInsertedMoney());
    }

    @Test
    public void testSelectSnackDelegatesToState() {
        vendingMachine.setState(vendingMachine.getDispensingSnackState());
        vendingMachine.setSelectedSnack(coke);
        assertEquals("Please wait, Coke is being dispensed" + System.lineSeparator(),
                captureOutput(() -> vendingMachine.selectSnack("Snickers")));
        assertSame(coke, vendingMachine.getSelectedSnack());
    }

    @Test
    public void testInsertMoneyDelegatesToState() {
        assertEquals("Please select a snack first. Returning $1.00" + System.lineSeparator(),
                captureOutput(() -> vendingMachine.insertMoney(1.00)));
        assertEquals(0, vendingMachine.getInsertedMoney());
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
    public void testFullPurchase() {
        captureOutput(() -> {
            vendingMachine.selectSnack("Coke");
            vendingMachine.insertMoney(1.00);
            vendingMachine.insertMoney(1.00);
        });
        assertEquals(1, coke.getQuantity());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
        assertNull(vendingMachine.getSelectedSnack());
        assertEquals(0, vendingMachine.getInsertedMoney());
    }

    @Test
    public void testToString() {
        assertEquals("Vending machine (Idle):" + System.lineSeparator()
                + "Coke - $1.50 (2 left)" + System.lineSeparator()
                + "Snickers - $1.75 (1 left)", vendingMachine.toString());
        vendingMachine.setState(vendingMachine.getWaitingForMoneyState());
        assertEquals("Vending machine (Waiting for Money):", vendingMachine.toString().lines().findFirst().get());
    }
}
