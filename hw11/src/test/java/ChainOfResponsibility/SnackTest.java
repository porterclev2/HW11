package ChainOfResponsibility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnackTest {

    Snack coke = new Snack("Coke", 1.50, 2);

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Coke", coke.getName());
        assertEquals(1.50, coke.getPrice());
        assertEquals(2, coke.getQuantity());
    }

    @Test
    public void testConstructorAllowsZeroQuantity() {
        assertEquals(0, new Snack("Snickers", 1.75, 0).getQuantity());
    }

    @Test
    public void testConstructorWithoutNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Snack(null, 1.50, 2));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Snack(" ", 1.50, 2));
        assertEquals("A snack needs a name", exception.getMessage());
    }

    @Test
    public void testConstructorWithInvalidPriceThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Snack("Coke", 0, 2));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Snack("Coke", -1.50, 2));
        assertEquals("Price must be positive", exception.getMessage());
    }

    @Test
    public void testConstructorWithNegativeQuantityThrows() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Snack("Coke", 1.50, -1));
        assertEquals("Quantity cannot be negative", exception.getMessage());
    }

    @Test
    public void testSetPrice() {
        coke.setPrice(2.00);
        assertEquals(2.00, coke.getPrice());
    }

    @Test
    public void testSetInvalidPriceThrows() {
        assertThrows(IllegalArgumentException.class, () -> coke.setPrice(0));
        assertEquals(1.50, coke.getPrice());
    }

    @Test
    public void testSetQuantity() {
        coke.setQuantity(10);
        assertEquals(10, coke.getQuantity());
        coke.setQuantity(0);
        assertEquals(0, coke.getQuantity());
    }

    @Test
    public void testSetNegativeQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () -> coke.setQuantity(-1));
        assertEquals(2, coke.getQuantity());
    }

    @Test
    public void testIsAvailable() {
        assertTrue(coke.isAvailable());
        coke.setQuantity(0);
        assertFalse(coke.isAvailable());
    }

    @Test
    public void testDispense() {
        coke.dispense();
        assertEquals(1, coke.getQuantity());
        coke.dispense();
        assertEquals(0, coke.getQuantity());
        assertFalse(coke.isAvailable());
    }

    @Test
    public void testDispenseWhenOutOfStockThrows() {
        Snack snickers = new Snack("Snickers", 1.75, 0);
        Exception exception = assertThrows(IllegalStateException.class, snickers::dispense);
        assertEquals("Snickers is out of stock", exception.getMessage());
        assertEquals(0, snickers.getQuantity());
    }

    @Test
    public void testFormatMoney() {
        assertEquals("$1.50", Snack.formatMoney(1.5));
        assertEquals("$0.05", Snack.formatMoney(0.05));
        assertEquals("$12.00", Snack.formatMoney(12));
    }

    @Test
    public void testToString() {
        assertEquals("Coke - $1.50 (2 left)", coke.toString());
        assertEquals("Snickers - $1.75 (0 left)", new Snack("Snickers", 1.75, 0).toString());
    }
}
