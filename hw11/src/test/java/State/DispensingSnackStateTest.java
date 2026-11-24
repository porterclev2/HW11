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

public class DispensingSnackStateTest {

    Snack coke = new Snack("Coke", 1.50, 2);
    Snack snickers = new Snack("Snickers", 1.75, 1);
    VendingMachine vendingMachine = createVendingMachine();
    DispensingSnackState dispensingSnackState = (DispensingSnackState) vendingMachine.getDispensingSnackState();

    private VendingMachine createVendingMachine() {
        SnackDispenseHandler snackDispenser = new SnackDispenseHandler(coke);
        snackDispenser.setNextHandler(new SnackDispenseHandler(snickers));
        return new VendingMachine(snackDispenser);
    }

    private void startDispensing(Snack snack, double insertedMoney) {
        vendingMachine.setSelectedSnack(snack);
        vendingMachine.addMoney(insertedMoney);
        vendingMachine.setState(dispensingSnackState);
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
        assertSame(vendingMachine, dispensingSnackState.getVendingMachine());
        assertSame(vendingMachine, new DispensingSnackState(vendingMachine).getVendingMachine());
    }

    @Test
    public void testIsStateOfVendingMachine() {
        assertInstanceOf(StateOfVendingMachine.class, dispensingSnackState);
    }

    @Test
    public void testSelectSnackWhileDispensing() {
        startDispensing(coke, 1.50);
        assertEquals("Please wait, Coke is being dispensed" + System.lineSeparator(),
                captureOutput(() -> dispensingSnackState.selectSnack("Snickers")));
        assertSame(coke, vendingMachine.getSelectedSnack());
        assertSame(dispensingSnackState, vendingMachine.getState());
    }

    @Test
    public void testInsertMoneyWhileDispensingReturnsMoney() {
        startDispensing(coke, 1.50);
        assertEquals("Please wait, Coke is being dispensed. Returning $1.00" + System.lineSeparator(),
                captureOutput(() -> dispensingSnackState.insertMoney(1.00)));
        assertEquals(1.50, vendingMachine.getInsertedMoney());
        assertSame(dispensingSnackState, vendingMachine.getState());
    }

    @Test
    public void testDispenseSnack() {
        startDispensing(coke, 1.50);
        assertEquals("Dispensed Coke" + System.lineSeparator(),
                captureOutput(dispensingSnackState::dispenseSnack));
        assertEquals(1, coke.getQuantity());
        assertNull(vendingMachine.getSelectedSnack());
        assertEquals(0, vendingMachine.getInsertedMoney());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
    }

    @Test
    public void testDispenseSnackWithChange() {
        startDispensing(snickers, 2.00);
        assertEquals("Dispensed Snickers. Returning change $0.25" + System.lineSeparator(),
                captureOutput(dispensingSnackState::dispenseSnack));
        assertEquals(0, snickers.getQuantity());
    }

    @Test
    public void testDispenseSnackOutOfStockReturnsMoney() {
        snickers.setQuantity(0);
        startDispensing(snickers, 2.00);
        assertEquals("Snickers is out of stock. Returning $2.00" + System.lineSeparator(),
                captureOutput(dispensingSnackState::dispenseSnack));
        assertEquals(0, snickers.getQuantity());
        assertNull(vendingMachine.getSelectedSnack());
        assertEquals(0, vendingMachine.getInsertedMoney());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
    }

    @Test
    public void testDispenseSnackWithNotEnoughMoneyReturnsMoney() {
        startDispensing(coke, 1.00);
        assertEquals("Not enough money for Coke. Returning $1.00" + System.lineSeparator(),
                captureOutput(dispensingSnackState::dispenseSnack));
        assertEquals(2, coke.getQuantity());
        assertSame(vendingMachine.getIdleState(), vendingMachine.getState());
    }

    @Test
    public void testToString() {
        assertEquals("Dispensing Snack", dispensingSnackState.toString());
    }
}
