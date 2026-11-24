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
        Snack snack = vendingMachine.getSelectedSnack();
        double insertedMoney = vendingMachine.getInsertedMoney();
        if (vendingMachine.getSnackDispenser().dispenseSnack(snack.getName(), insertedMoney)) {
            double change = vendingMachine.getChange();
            System.out.println("Dispensed " + snack.getName()
                    + (change > 0 ? ". Returning change " + Snack.formatMoney(change) : ""));
        } else {
            System.out.println(snack.getName() + " is out of stock. Returning " + Snack.formatMoney(insertedMoney));
        }
        vendingMachine.reset();
        vendingMachine.setState(vendingMachine.getIdleState());
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
