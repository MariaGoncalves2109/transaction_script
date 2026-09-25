package dbutils;

import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class SetupDatabase {

    /**
     * Reads and executes the schema script to initialize the database.
     * @throws ApplicationException if the script cannot be run.
     */
    public static void run() throws PersistenceException {
        System.out.println("Attempting to set up database schema...");
        try {
            DataSource.INSTANCE.connect();
            Connection conn = DataSource.INSTANCE.getConnection();

            String[] commands = getCommands();

            for (String command : commands) {
                if (command.trim().isEmpty()) continue;
                try (Statement statement = conn.createStatement()) {
                    statement.execute(command);
                }
            }
            System.out.println("Database schema setup successful.");
        } catch (Exception e) {
            throw new PersistenceException("Failed to run database setup script.", e);
        } finally {
            DataSource.INSTANCE.close();
        }
    }

    private static String[] getCommands() throws PersistenceException, IOException {
        InputStream is = SetupDatabase.class.getResourceAsStream("/schema-postgres.sql");
        if (is == null) {
            throw new PersistenceException("Cannot find schema-postgres.sql in resources.");
        }

        String script;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            script = reader.lines().collect(Collectors.joining("\n"));
        }
        String[] commands = script.split(";");
        return commands;
    }
}