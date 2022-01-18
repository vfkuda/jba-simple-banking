package banking;

import java.sql.*;
import java.util.Random;

public class CardStorage {

    static final int BIN = 400000;
    static public int accountIdSequence = 0;
    static Random rand = new Random();
    String database_url;

    public CardStorage(String databasePath) throws SQLException {
        database_url = "jdbc:sqlite:" + databasePath;
        initstorage();
    }

    public int incNextAccount() throws SQLException {
        int accountNumber;
        try (Connection con = getConnection()) {
            accountNumber = getNextAccountId(con.createStatement()) + 1;
            updateNextAccountId(con.createStatement(), accountNumber);
        }
        return accountNumber;
    }

    protected void updateNextAccountId(Statement stm, int id) throws SQLException {
        stm.executeUpdate(String.format("UPDATE SETTINGS SET ACCOUNT_NEXT_ID=%d;", id));
    }

    protected int getNextAccountId(Statement stm) throws SQLException {
        int nextId;
        ResultSet rs = stm.executeQuery("SELECT ACCOUNT_NEXT_ID from SETTINGS");
        if (rs.next()) {
            nextId = rs.getInt("ACCOUNT_NEXT_ID");
        } else {
            stm.executeUpdate("INSERT INTO SETTINGS (ACCOUNT_NEXT_ID) VALUES(1);");
            nextId = 1;
        }
        return nextId;
    }

    protected void saveCard(CreditCard card) throws SQLException {
        try (Connection con = getConnection()) {
            Statement stm = con.createStatement();
            stm.executeUpdate(String.format("INSERT INTO card (number, pin, balance) VALUES ('%s','%s',%d);",
                    card.cardNumberAsString(), card.cardPinAsText(), card.getAccountBalance()));
        }
    }

    protected CreditCard loadCard(String cardNumber) throws SQLException {
        CreditCard card = null;
        try (Connection con = getConnection()) {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(String.format("SELECT number, pin, balance FROM card WHERE number='%s';", cardNumber));
            if (rs.next()) {
                card = new CreditCard(rs.getString("number"));
                card.setPin(Integer.parseInt(rs.getString("pin")));
                card.setAccountBalance(rs.getInt("balance"));
            }
        }
        return card;
    }

    public CreditCard createNewCard() throws SQLException {
        int accountNumber = incNextAccount();
        CreditCard card = new CreditCard(BIN, accountNumber);
        card.setPin(rand.nextInt(9999));

        saveCard(card);
        return card;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(database_url);
    }

    public void initstorage() throws SQLException {
        try (Connection con = getConnection()) {
            Statement stm = con.createStatement();
            stm.execute("CREATE TABLE IF NOT EXISTS card (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "number TEXT, " +
                    "pin TEXT, " +
                    "balance INTEGER DEFAULT 0 " +
                    ")");
            stm.execute("CREATE TABLE IF NOT EXISTS SETTINGS (ID INTEGER, ACCOUNT_NEXT_ID INTEGER);");
        }
    }

    public void addAccountBalance(CreditCard accountCard, int amount) throws SQLException {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE card SET balance=? WHERE number =?;");
            ps.setInt(1, accountCard.getAccountBalance() + amount);
            ps.setString(2, accountCard.cardNumberAsString());
            ps.execute();
            accountCard.addAccountBalance(amount);
        }
    }

    public void doTransfer(CreditCard accountCard, CreditCard receptientAccountCard, int transferAmount) throws SQLException {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("UPDATE card SET balance=? WHERE number =?;");

            ps.setInt(1, accountCard.getAccountBalance() - transferAmount);
            ps.setString(2, accountCard.cardNumberAsString());
            ps.execute();

            ps.setInt(1, receptientAccountCard.getAccountBalance() + transferAmount);
            ps.setString(2, receptientAccountCard.cardNumberAsString());
            ps.execute();

            con.commit();

            accountCard.addAccountBalance(-transferAmount);
            receptientAccountCard.addAccountBalance(+transferAmount);
        }
    }

    public void revokeCard(CreditCard card) throws SQLException {
        try (Connection con = getConnection()) {
            Statement stm = con.createStatement();
            stm.execute("DELETE FROM card WHERE number ='" + card.cardNumberAsLong() + "';");
        }
    }
}
