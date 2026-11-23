package ChainOfResponsibility;

public class Snack {
    private String name;
    private double price;
    private int quantity;

    public Snack(String name, double price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A snack needs a name");
        }
        this.name = name;
        setPrice(price);
        setQuantity(quantity);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public void dispense() {
        if (!isAvailable()) {
            throw new IllegalStateException(name + " is out of stock");
        }
        quantity--;
    }

    public static String formatMoney(double amount) {
        return String.format("$%.2f", amount);
    }

    @Override
    public String toString() {
        return name + " - " + formatMoney(price) + " (" + quantity + " left)";
    }
}
