package State;

import ChainOfResponsibility.Snack;

public class IdleState implements StateOfVendingMachine {
    private VendingMachine vendingMachine;

    public IdleState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    public VendingMachine getVendingMachine() {
        return vendingMachine;
    }

    @Override
    public void selectSnack(String snackName) {
        Snack snack = vendingMachine.getSnack(snackName);
        if (snack == null) {
            System.out.println(snackName + " is not sold in this vending machine");
            return;
        }
        vendingMachine.setSelectedSnack(snack);
        vendingMachine.setState(vendingMachine.getWaitingForMoneyState());
        System.out.println(snack.getName() + " selected. Please insert " + Snack.formatMoney(snack.getPrice()));
    }

    @Override
    public void insertMoney(double amount) {
        System.out.println("Please select a snack first. Returning " + Snack.formatMoney(amount));
    }

    @Override
    public void dispenseSnack() {
        System.out.println("Please select a snack first");
    }

    @Override
    public void cancel() {
        System.out.println("There is nothing to cancel");
    }

    @Override
    public String toString() {
        return "Idle";
    }
}
