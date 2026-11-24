package State;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VendingMachineDriverTest {

    List<String> output;

    @BeforeEach
    public void runDriver() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            VendingMachineDriver.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }
        output = List.of(captured.toString().split(System.lineSeparator()));
    }

    private List<String> section(String header, String nextHeader) {
        int start = output.indexOf(header);
        int end = nextHeader == null ? output.size() : output.indexOf(nextHeader);
        assertTrue(start >= 0 && end > start, "Missing section " + header);
        List<String> lines = new ArrayList<>();
        for (String line : output.subList(start + 1, end)) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    @Test
    public void testDriverHasSixSnacksInChainOrder() {
        assertEquals("Chain of responsibility: Coke -> Pepsi -> Cheetos -> Doritos -> KitKat -> Snickers", output.get(0));
        assertEquals(List.of(
                "Vending machine (Idle):",
                "Coke - $1.50 (5 left)",
                "Pepsi - $1.50 (5 left)",
                "Cheetos - $1.25 (4 left)",
                "Doritos - $1.25 (4 left)",
                "KitKat - $1.00 (3 left)",
                "Snickers - $1.75 (2 left)"),
                output.subList(1, 8));
    }

    @Test
    public void testExactChangeDispensesSnack() {
        assertEquals(List.of(
                "Coke selected. Please insert $1.50",
                "Inserted $1.50. Total inserted: $1.50",
                "Dispensed Coke"),
                section("Buying a Coke with exact change", "Buying Doritos with extra money"));
    }

    @Test
    public void testExtraMoneyReturnsChange() {
        assertEquals(List.of(
                "Doritos selected. Please insert $1.25",
                "Inserted $2.00. Total inserted: $2.00",
                "Dispensed Doritos. Returning change $0.75"),
                section("Buying Doritos with extra money", "Buying a KitKat a little at a time"));
    }

    @Test
    public void testWaitsForEnoughMoney() {
        assertEquals(List.of(
                "KitKat selected. Please insert $1.00",
                "Inserted $0.25. Total inserted: $0.25",
                "Please insert $0.75 more",
                "Please insert $0.75 more for KitKat",
                "Inserted $0.25. Total inserted: $0.50",
                "Please insert $0.50 more",
                "Inserted $0.50. Total inserted: $1.00",
                "Dispensed KitKat"),
                section("Buying a KitKat a little at a time", "Using the machine without selecting a snack"));
    }

    @Test
    public void testIdleMachineNeedsSnackSelected() {
        assertEquals(List.of(
                "Please select a snack first. Returning $1.00",
                "Please select a snack first",
                "There is nothing to cancel",
                "Water is not sold in this vending machine"),
                section("Using the machine without selecting a snack", "Cancelling a Pepsi"));
    }

    @Test
    public void testCancelReturnsMoney() {
        assertEquals(List.of(
                "Pepsi selected. Please insert $1.50",
                "Inserted $1.00. Total inserted: $1.00",
                "Please insert $0.50 more",
                "Pepsi is already selected. Please insert $0.50",
                "Cancelled Pepsi. Returning $1.00"),
                section("Cancelling a Pepsi", "Buying Snickers until they run out"));
    }

    @Test
    public void testSnickersQuantityHitsZero() {
        assertEquals(List.of(
                "Snickers selected. Please insert $1.75",
                "Inserted $2.00. Total inserted: $2.00",
                "Dispensed Snickers. Returning change $0.25",
                "Snickers selected. Please insert $1.75",
                "Inserted $1.75. Total inserted: $1.75",
                "Dispensed Snickers",
                "Snickers - $1.75 (0 left)",
                "Snickers selected. Please insert $1.75",
                "Inserted $1.75. Total inserted: $1.75",
                "Snickers is out of stock. Returning $1.75"),
                section("Buying Snickers until they run out", "Final inventory"));
    }

    @Test
    public void testFinalInventory() {
        assertEquals(List.of(
                "Vending machine (Idle):",
                "Coke - $1.50 (4 left)",
                "Pepsi - $1.50 (5 left)",
                "Cheetos - $1.25 (4 left)",
                "Doritos - $1.25 (3 left)",
                "KitKat - $1.00 (2 left)",
                "Snickers - $1.75 (0 left)"),
                section("Final inventory", null));
    }
}
