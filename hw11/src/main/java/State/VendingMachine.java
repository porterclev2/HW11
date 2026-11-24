package State;

import java.util.Collections;
import java.util.List;

import ChainOfResponsibility.Snack;
import ChainOfResponsibility.SnackDispenseHandler;

public class VendingMachine {
    private StateOfVendingMachine idleState;
    private StateOfVendingMachine waitingForMoneyState;
    private StateOfVendingMachine dispensingSnackState;
    private StateOfVendingMachine state;
    private SnackDispenseHandler snackDispenser;
    private List<Snack> snacks;
    private Snack selectedSnack;
    private double insertedMoney;

    public VendingMachine(SnackDispenseHandler snackDispenser) {
        if (snackDispenser == null) {
            throw new IllegalArgumentException("A vending machine needs a snack dispenser");
        }
        this.snackDispenser = snackDispenser;
        this.snacks = snackDispenser.getSnacksInChain();
        this.idleState = new IdleState(this);
        this.waitingForMoneyState = new WaitingForMoneyState(this);
        this.dispensingSnackState = new DispensingSnackState(this);
        this.state = idleState;
    }

    public StateOfVendingMachine getState() {
        return state;
    }

    void setState(StateOfVendingMachine state) {
        this.state = state;
    }

    public StateOfVendingMachine getIdleState() {
        return idleState;
    }

    public StateOfVendingMachine getWaitingForMoneyState() {
        return waitingForMoneyState;
    }

    public StateOfVendingMachine getDispensingSnackState() {
        return dispensingSnackState;
    }

    public SnackDispenseHandler getSnackDispenser() {
        return snackDispenser;
    }

    public List<Snack> getSnacks() {
        return Collections.unmodifiableList(snacks);
    }

    public Snack getSnack(String snackName) {
        for (Snack snack : snacks) {
            if (snack.getName().equalsIgnoreCase(snackName)) {
                return snack;
            }
        }
        return null;
    }

    public Snack getSelectedSnack() {
        return selectedSnack;
    }

    void setSelectedSnack(Snack selectedSnack) {
        this.selectedSnack = selectedSnack;
    }

    public double getInsertedMoney() {
        return insertedMoney;
    }

    void addMoney(double amount) {
        insertedMoney = roundToCents(insertedMoney + amount);
    }

    public double getAmountOwed() {
        if (selectedSnack == null) {
            return 0;
        }
        return Math.max(0, roundToCents(selectedSnack.getPrice() - insertedMoney));
    }

    public double getChange() {
        if (selectedSnack == null) {
            return 0;
        }
        return Math.max(0, roundToCents(insertedMoney - selectedSnack.getPrice()));
    }

    void reset() {
        selectedSnack = null;
        insertedMoney = 0;
    }

    public void selectSnack(String snackName) {
        state.selectSnack(snackName);
    }

    public void insertMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount inserted must be positive");
        }
        state.insertMoney(amount);
    }

    public void dispenseSnack() {
        state.dispenseSnack();
    }

    public void cancel() {
        state.cancel();
    }

    private static double roundToCents(double amount) {
        return Math.round(amount * 100) / 100.0;
    }

    @Override
    public String toString() {
        StringBuilder machine = new StringBuilder("Vending machine (" + state + "):");
        for (Snack snack : snacks) {
            machine.append(System.lineSeparator()).append(snack);
        }
        return machine.toString();
    }
}
