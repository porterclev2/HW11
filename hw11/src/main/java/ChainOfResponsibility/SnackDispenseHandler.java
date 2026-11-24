package ChainOfResponsibility;

import java.util.ArrayList;
import java.util.List;

public class SnackDispenseHandler {
    private Snack snack;
    private SnackDispenseHandler nextHandler;

    public SnackDispenseHandler(Snack snack) {
        if (snack == null) {
            throw new IllegalArgumentException("A handler needs a snack");
        }
        this.snack = snack;
    }

    public Snack getSnack() {
        return snack;
    }

    public SnackDispenseHandler getNextHandler() {
        return nextHandler;
    }

    public SnackDispenseHandler setNextHandler(SnackDispenseHandler nextHandler) {
        for (SnackDispenseHandler handler = nextHandler; handler != null; handler = handler.nextHandler) {
            if (handler == this) {
                throw new IllegalArgumentException("Adding " + nextHandler.snack.getName()
                        + " after " + snack.getName() + " would create a cycle");
            }
        }
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public boolean canHandle(String snackName) {
        return snack.getName().equalsIgnoreCase(snackName);
    }

    public boolean dispenseSnack(String snackName, double insertedMoney) {
        if (canHandle(snackName)) {
            if (!snack.isAvailable() || insertedMoney < snack.getPrice()) {
                return false;
            }
            snack.dispense();
            return true;
        }
        if (nextHandler == null) {
            return false;
        }
        return nextHandler.dispenseSnack(snackName, insertedMoney);
    }

    public List<Snack> getSnacksInChain() {
        List<Snack> snacks = new ArrayList<>();
        for (SnackDispenseHandler handler = this; handler != null; handler = handler.nextHandler) {
            snacks.add(handler.snack);
        }
        return snacks;
    }

    @Override
    public String toString() {
        List<String> names = new ArrayList<>();
        for (Snack snackInChain : getSnacksInChain()) {
            names.add(snackInChain.getName());
        }
        return String.join(" -> ", names);
    }
}
