import java.util.Random;
import java.util.Scanner;

class Player {
    private String name;
    private double balance;

    public Player(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        balance += amount;
    }

    public boolean deductBalance(double amount) {
        if (amount > balance) {
            System.out.println("❌ Insufficient balance! Please enter a lower amount.");
            return false;
        }
        balance -= amount;
        return true;
    }
}

class BetColorGame {
    private static final String[] COLORS = {"Red", "Green", "Blue", "Yellow"};
    private static final Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);
    private Player player;

    public BetColorGame(Player player) {
        this.player = player;
    }

    public void start() {
        System.out.println("\n🎲 Welcome to the Bet Color Game, " + player.getName() + "! 🎲");
        System.out.println("🎨 Available colors: Red, Green, Blue, Yellow");

        while (true) {
            System.out.println("\n💰 Current Balance: $" + player.getBalance());
            System.out.print("Enter the color you want to bet on (or type 'exit' to quit): ");
            String chosenColor = scanner.nextLine().trim();

            if (chosenColor.equalsIgnoreCase("exit")) {
                System.out.println("👋 Thank you for playing! Your final balance: $" + player.getBalance());
                break;
            }

            if (!isValidColor(chosenColor)) {
                System.out.println("❌ Invalid color! Choose from Red, Green, Blue, or Yellow.");
                continue;
            }

            System.out.print("Enter your bet amount: $");
            double betAmount;
            try {
                betAmount = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid bet amount! Please enter a numeric value.");
                continue;
            }

            if (betAmount <= 0) {
                System.out.println("❌ Bet amount must be greater than zero!");
                continue;
            }

            if (!player.deductBalance(betAmount)) {
                continue;
            }

            String winningColor = COLORS[random.nextInt(COLORS.length)];
            System.out.println("🎡 Spinning the wheel... 🎡");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("🎉 The winning color is: " + winningColor + " 🎉");

            if (winningColor.equalsIgnoreCase(chosenColor)) {
                double winnings = betAmount * 2;
                player.addBalance(winnings);
                System.out.println("✅ Congratulations! You won $" + winnings);
            } else {
                System.out.println("❌ Oops! You lost $" + betAmount);
            }

            System.out.println("\n🔄 Do you want to play again? (yes/no): ");
            String playAgain = scanner.nextLine().trim();
            if (!playAgain.equalsIgnoreCase("yes")) {
                System.out.println("👋 Exiting game... Your final balance: $" + player.getBalance());
                break;
            }
        }
    }

    private boolean isValidColor(String color) {
        for (String validColor : COLORS) {
            if (validColor.equalsIgnoreCase(color)) {
                return true;
            }
        }
        return false;
    }
}

public class ColorBetMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🛑 Welcome to the Color Bet Game! 🛑");
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();
        
        System.out.print("Enter your starting balance: $");
        double initialBalance;
        try {
            initialBalance = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid balance! Setting default balance to $100.");
            initialBalance = 100;
        }
        
        Player player = new Player(playerName, initialBalance);
        BetColorGame game = new BetColorGame(player);
        game.start();
        
        scanner.close();
    }
}
