package banking;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Application {
    //    Menu mainMenu;
    private Scanner sc;
    private CardStorage storage;
    private CreditCard accountCard;

    public Application(String databasePath) throws SQLException {
        sc = new Scanner(System.in);
        storage = new CardStorage(databasePath);
        storage.initstorage();
    }

    public void run() throws SQLException {
        int option = -1;
        while (option != 0) {
            System.out.println("" +
                    "1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit"

            );
            option = sc.nextInt();
            switch (option) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    option = logIntoAccount();
                    break;
            }
        }
        System.out.println("Good buy!");
    }

    private int logIntoAccount() throws SQLException {
        System.out.println("Enter your card number:");
        String num = sc.next();
        System.out.println("Enter your PIN:");
        String pin = sc.next();
//        try {
//            Thread.sleep(120*2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        accountCard = storage.loadCard(num);
        if (accountCard == null || !Objects.equals(num, accountCard.cardNumberAsString()) || !Objects.equals(pin, accountCard.cardPinAsText())) {
            System.out.println("Wrong card number or PIN!");
            return -1;
        }

        System.out.println("You have successfully logged in!");

        int option = -1;
        while (option != 0) {
            System.out.println();
            System.out.println("" +
                    "1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit\n"
            );
            option = sc.nextInt();
            switch (option) {
                case 1:
                    showBalance();
                    break;
                case 2:
                    addIncome();
                    break;
                case 3:
                    doTransfer();
                    break;
                case 4:
                    closeAccount();
                    accountCard = null;
                    return -1;
                case 5:
                    System.out.println("You have successfully logged out!");
                    accountCard = null;
                    return -1;
            }
        }
        return option;
    }

    private void closeAccount() throws SQLException {
        storage.revokeCard(accountCard);
        System.out.println("The account has been closed!");
    }

    private void doTransfer() throws SQLException {

        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String ccNumber = sc.next();
        if (!CreditCard.isNumberValid(ccNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        CreditCard receptientAccountCard = storage.loadCard(ccNumber);
        if (receptientAccountCard == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int transferAmount = sc.nextInt();
        if (transferAmount > accountCard.getAccountBalance()) {
            System.out.println("Not enough money!");
            return;
        }
        storage.doTransfer(accountCard, receptientAccountCard, transferAmount);
        System.out.println("Success!");

    }


    private void showBalance() {
        System.out.printf("Balance: %d\n", accountCard.getAccountBalance());
    }

    private void addIncome() throws SQLException {
        System.out.println("Enter income:");
        int income = sc.nextInt();
        storage.addAccountBalance(accountCard, income);

    }

    private void createAccount() throws SQLException {
        CreditCard card = storage.createNewCard();
        System.out.printf("" +
                        "Your card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                        "Your card PIN:\n" +
                        "%04d\n",
                card, card.getPin());
    }

    public void test() throws SQLException {
        CreditCard c1 = storage.createNewCard();
        CreditCard c2 = storage.createNewCard();
        CreditCard c3 = storage.createNewCard();
        CreditCard c4 = storage.createNewCard();
        storage.addAccountBalance(c1, 1000);
        storage.addAccountBalance(c2, 10000);
        storage.addAccountBalance(c2, -1000);
        storage.addAccountBalance(c3, 10000);
        storage.addAccountBalance(c4, 10000);
        storage.doTransfer(c3, c4, 6000);

    }
}
