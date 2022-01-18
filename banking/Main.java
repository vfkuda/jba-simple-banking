package banking;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            Application app = new Application("../db2.db");
            app.test();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String dbPath = null;
        for (int i = 0; i < args.length; i++) {
            if ("-filename".equals(args[i].toLowerCase())) {
                dbPath = args[i + 1];
                break;
            }
        }
        if (dbPath != null) {
            Application app = null;
            try {
                app = new Application(dbPath);
                app.run();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}