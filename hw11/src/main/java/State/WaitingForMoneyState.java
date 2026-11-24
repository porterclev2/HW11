package State;

import ChainOfResponsibility.Snack;

public class WaitingForMoneyState implements StateOfVendingMachine {
    private VendingMachine vendingMachine;

    public WaitingForMoneyState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    public VendingMachine getVendingMachine() {
        return vendingMachine;
    }

    @Override
    public void selectSnack(String snackName) {
        System.out.println(vendingMachine.getSelectedSnack().getName() + " is already selected. Please insert "
                + Snack.formatMoney(vendingMachine.getAmountOwed()));
    }

    @Override
    public void insertMoney(double amount) {
        vendingMachine.addMoney(amount);
        System.out.println("Inserted " + Snack.formatMoney(amount) + ". Total inserted: "
                + Snack.formatMoney(vendingMachine.getInsertedMoney()));
        if (vendingMachine.getAmountOwed() > 0) {
            System.out.println("Please insert " + Snack.formatMoney(vendingMachine.getAmountOwed()) + " more");
            return;
        }
        vendingMachine.setState(vendingMachine.getDispensingSnackState());
        vendingMachine.dispenseSnack();
    }

    @Override
    public void dispenseSnack() {
        System.out.println("Please insert " + Snack.formatMoney(vendingMachine.getAmountOwed()) + " more for "
                + vendingMachine.getSelectedSnack().getName());
    }

    @Override
    public String toString() {
        return "Waiting for Money";
    }
}
