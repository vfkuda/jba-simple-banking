package banking;

import java.util.Arrays;

public class CreditCard {
    //    String number;
    private int bin;
    private int accountNumber;
    private int checksum;
    private int pin;
    private int accountBalance;


    public CreditCard() {
        this.accountBalance = 0;
    }

    public CreditCard(int bin, int accountNumber) {
        this();
        this.bin = bin;
        this.accountNumber = accountNumber;
        forceChecksum();

    }

    public CreditCard(String cardNumber) {
        this();
        this.bin = Integer.parseInt(cardNumber.substring(0, 6));
        this.accountNumber = Integer.parseInt(cardNumber.substring(6, 15));
        this.checksum = Integer.parseInt(cardNumber.substring(15, 16));
    }


    public static boolean isNumberValid(String ccNumber) {
        CreditCard cc = new CreditCard(ccNumber);
        int chs = cc.calculateChecksum();
        return chs == cc.checksum;
    }

    public int getBin() {
        return bin;
    }

    public void setBin(int bin) {
        this.bin = bin;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

//    public void setPin(String pin) {
//        this.pin = Integer.parseInt(pin);
//    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int calculateChecksum() {
        return calculateChecksum(this.bin, this.accountNumber);
    }

    public int calculateChecksum(int bin, int accountNumber) {
        int[] digits = new int[15];

        int x = accountNumber;
        for (int i = 0; i < 9; i++) {
            digits[15 - i - 1] = x % 10;
            x = x / 10;
        }

        x = bin;
        for (int i = 0; i < 6; i++) {
            digits[15 - 9 - i - 1] = x % 10;
            x = x / 10;
        }

        for (int i = 0; i < 15; i = i + 2) {
            digits[i] = digits[i] * 2;
            if (digits[i] > 9) {
                digits[i] = digits[i] - 9;
            }
        }

        int sum = Arrays.stream(digits).sum();
        return (10 - (sum % 10)) % 10;
    }

    public void forceChecksum() {
        checksum = calculateChecksum(bin, accountNumber);
    }

    public long cardNumberAsLong() {
        return bin * 100_0000_0000L + accountNumber * 10 + checksum;
    }

    @Override
    public String toString() {
        return cardNumberAsString();
    }

    public String cardNumberAsString() {
        return String.format("%06d%09d%01d", bin, accountNumber, checksum);
    }

    public String cardPinAsText() {
        return String.format("%04d", pin);
    }

    public void addAccountBalance(int amount) {
        accountBalance = accountBalance + amount;
    }
}
