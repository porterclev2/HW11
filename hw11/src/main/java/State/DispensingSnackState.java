package State;

import ChainOfResponsibility.Snack;

public class DispensingSnackState implements StateOfVendingMachine {
    private VendingMachine vendingMachine;

    public DispensingSnackState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    public VendingMachine getVendingMachine() {
        return vendingMachine;
    }

    @Override
    public void selectSnack(String snackName) {
        System.out.println("Please wait, " + vendingMachine.getSelectedSnack().getName() + " is being dispensed");
    }

    @Override
    public void insertMoney(double amount) {
        System.out.println("Please wait, " + vendingMachine.getSelectedSnack().getName()
                + " is being dispensed. Returning " + Snack.formatMoney(amount));
    }

    @Override
    public void dispenseSnack() {
        Snack snack = vendingMachine.getSelectedSnack();
        double insertedMoney = vendingMachine.getInsertedMoney();
        if (vendingMachine.getSnackDispenser().dispenseSnack(snack.getName(), insertedMoney)) {
            double change = vendingMachine.getChange();
            System.out.println("Dispensed " + snack.getName()
                    + (change > 0 ? ". Returning change " + Snack.formatMoney(change) : ""));
        } else if (!snack.isAvailable()) {
            System.out.println(snack.getName() + " is out of stock. Returning " + Snack.formatMoney(insertedMoney));
        } else {
            System.out.println("Not enough money for " + snack.getName() + ". Returning "
                    + Snack.formatMoney(insertedMoney));
        }
        vendingMachine.reset();
        vendingMachine.setState(vendingMachine.getIdleState());
    }

    @Override
    public String toString() {
        return "Dispensing Snack";
    }
}
