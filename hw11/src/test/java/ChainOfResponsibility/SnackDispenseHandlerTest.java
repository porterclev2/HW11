package ChainOfResponsibility;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnackDispenseHandlerTest {

    Snack coke = new Snack("Coke", 1.50, 2);
    Snack pepsi = new Snack("Pepsi", 1.50, 2);
    Snack snickers = new Snack("Snickers", 1.75, 1);
    SnackDispenseHandler cokeHandler = new SnackDispenseHandler(coke);
    SnackDispenseHandler pepsiHandler = new SnackDispenseHandler(pepsi);
    SnackDispenseHandler snickersHandler = new SnackDispenseHandler(snickers);

    @BeforeEach
    public void buildChain() {
        cokeHandler.setNextHandler(pepsiHandler).setNextHandler(snickersHandler);
    }

    @Test
    public void testConstructorAndGetSnack() {
        SnackDispenseHandler handler = new SnackDispenseHandler(coke);
        assertSame(coke, handler.getSnack());
        assertNull(handler.getNextHandler());
    }

    @Test
    public void testConstructorWithoutSnackThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SnackDispenseHandler(null));
        assertEquals("A handler needs a snack", exception.getMessage());
    }

    @Test
    public void testSetNextHandler() {
        assertSame(pepsiHandler, cokeHandler.getNextHandler());
        assertSame(snickersHandler, pepsiHandler.getNextHandler());
        assertNull(snickersHandler.getNextHandler());
    }

    @Test
    public void testSetNextHandlerReturnsNextHandler() {
        SnackDispenseHandler handler = new SnackDispenseHandler(new Snack("KitKat", 1.00, 1));
        assertSame(cokeHandler, handler.setNextHandler(cokeHandler));
    }

    @Test
    public void testSetNextHandlerToNull() {
        assertNull(pepsiHandler.setNextHandler(null));
        assertNull(pepsiHandler.getNextHandler());
        assertEquals(List.of(coke, pepsi), cokeHandler.getSnacksInChain());
    }

    @Test
    public void testSetNextHandlerToItselfThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cokeHandler.setNextHandler(cokeHandler));
        assertEquals("Adding Coke after Coke would create a cycle", exception.getMessage());
        assertSame(pepsiHandler, cokeHandler.getNextHandler());
    }

    @Test
    public void testSetNextHandlerThatCreatesCycleThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> snickersHandler.setNextHandler(cokeHandler));
        assertEquals("Adding Coke after Snickers would create a cycle", exception.getMessage());
        assertNull(snickersHandler.getNextHandler());
    }

    @Test
    public void testCanHandle() {
        assertTrue(cokeHandler.canHandle("Coke"));
        assertTrue(cokeHandler.canHandle("coke"));
        assertFalse(cokeHandler.canHandle("Pepsi"));
        assertFalse(cokeHandler.canHandle(null));
    }

    @Test
    public void testDispenseSnackHandledByFirstHandler() {
        assertTrue(cokeHandler.dispenseSnack("Coke", 1.50));
        assertEquals(1, coke.getQuantity());
        assertEquals(2, pepsi.getQuantity());
    }

    @Test
    public void testDispenseSnackPassesRequestDownChain() {
        assertTrue(cokeHandler.dispenseSnack("Snickers", 2.00));
        assertEquals(0, snickers.getQuantity());
        assertEquals(2, coke.getQuantity());
        assertEquals(2, pepsi.getQuantity());
    }

    @Test
    public void testDispenseSnackOnlyLooksForwardInChain() {
        assertFalse(pepsiHandler.dispenseSnack("Coke", 1.50));
        assertEquals(2, coke.getQuantity());
    }

    @Test
    public void testDispenseSnackOutOfStock() {
        assertTrue(cokeHandler.dispenseSnack("Snickers", 1.75));
        assertFalse(cokeHandler.dispenseSnack("Snickers", 1.75));
        assertEquals(0, snickers.getQuantity());
    }

    @Test
    public void testDispenseSnackWithNotEnoughMoney() {
        assertFalse(cokeHandler.dispenseSnack("Pepsi", 1.25));
        assertEquals(2, pepsi.getQuantity());
    }

    @Test
    public void testDispenseSnackNotInChain() {
        assertFalse(cokeHandler.dispenseSnack("Water", 5.00));
        assertEquals(List.of(2, 2, 1), List.of(coke.getQuantity(), pepsi.getQuantity(), snickers.getQuantity()));
    }

    @Test
    public void testGetSnacksInChain() {
        assertEquals(List.of(coke, pepsi, snickers), cokeHandler.getSnacksInChain());
        assertEquals(List.of(pepsi, snickers), pepsiHandler.getSnacksInChain());
        assertEquals(List.of(snickers), snickersHandler.getSnacksInChain());
    }

    @Test
    public void testToString() {
        assertEquals("Coke -> Pepsi -> Snickers", cokeHandler.toString());
        assertEquals("Snickers", snickersHandler.toString());
    }
}
