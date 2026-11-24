package State;

import ChainOfResponsibility.Snack;
import ChainOfResponsibility.SnackDispenseHandler;

public class VendingMachineDriver {
    public static void main(String[] args) {
        SnackDispenseHandler snackDispenser = new SnackDispenseHandler(new Snack("Coke", 1.50, 5));
        snackDispenser.setNextHandler(new SnackDispenseHandler(new Snack("Pepsi", 1.50, 5)))
                .setNextHandler(new SnackDispenseHandler(new Snack("Cheetos", 1.25, 4)))
                .setNextHandler(new SnackDispenseHandler(new Snack("Doritos", 1.25, 4)))
                .setNextHandler(new SnackDispenseHandler(new Snack("KitKat", 1.00, 3)))
                .setNextHandler(new SnackDispenseHandler(new Snack("Snickers", 1.75, 2)));
        VendingMachine vendingMachine = new VendingMachine(snackDispenser);

        System.out.println("Chain of responsibility: " + snackDispenser);
        System.out.println(vendingMachine);

        System.out.println("\nBuying a Coke with exact change\n");
        vendingMachine.selectSnack("Coke");
        vendingMachine.insertMoney(1.50);

        System.out.println("\nBuying Doritos with extra money\n");
        vendingMachine.selectSnack("Doritos");
        vendingMachine.insertMoney(2.00);

        System.out.println("\nBuying a KitKat a little at a time\n");
        vendingMachine.selectSnack("KitKat");
        vendingMachine.insertMoney(0.25);
        vendingMachine.dispenseSnack();
        vendingMachine.insertMoney(0.25);
        vendingMachine.insertMoney(0.50);

        System.out.println("\nUsing the machine without selecting a snack\n");
        vendingMachine.insertMoney(1.00);
        vendingMachine.dispenseSnack();
        vendingMachine.cancel();
        vendingMachine.selectSnack("Water");

        System.out.println("\nCancelling a Pepsi\n");
        vendingMachine.selectSnack("Pepsi");
        vendingMachine.insertMoney(1.00);
        vendingMachine.selectSnack("Cheetos");
        vendingMachine.cancel();

        System.out.println("\nBuying Snickers until they run out\n");
        vendingMachine.selectSnack("Snickers");
        vendingMachine.insertMoney(2.00);
        vendingMachine.selectSnack("Snickers");
        vendingMachine.insertMoney(1.75);
        System.out.println(vendingMachine.getSnack("Snickers"));
        vendingMachine.selectSnack("Snickers");
        vendingMachine.insertMoney(1.75);

        System.out.println("\nFinal inventory\n");
        System.out.println(vendingMachine);
    }
}
