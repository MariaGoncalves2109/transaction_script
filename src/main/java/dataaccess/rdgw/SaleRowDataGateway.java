package dataaccess.rdgw;

import business.SaleStatus;
import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class SaleRowDataGateway {

    private int id;
    private Date date;
    private SaleStatus status;
    private int customerId;
    private double totalSale;
    private double totalDiscount;

    private static final String GET_SALE_SQL = "SELECT id, open_date, status, customer_id FROM sale WHERE id = ?";
    private static final String INSERT_SALE_SQL = "INSERT INTO sale (open_date, status, customer_id) VALUES (?, ?, ?)";

    private SaleRowDataGateway() {}

    public SaleRowDataGateway(int customerId, Date date) {
        this.customerId = customerId;
        this.date = date;
        this.status = SaleStatus.OPEN;
    }

    public int getId() { return id; }
    public SaleStatus getStatus() { return status; }
    public int getCustomerId() { return customerId; }
    public double getDiscount() { return totalDiscount; }

     public void insert() throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_SALE_SQL)) {
            statement.setDate(1, new java.sql.Date(date.getTime()));
            statement.setString(2, status.toString());
            statement.setInt(3, customerId);
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                } else {
                    // If no key is returned, something is wrong.
                    throw new PersistenceException("Database did not return a generated key for the new sale.");
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error inserting new sale.", e);
        }
    }

    public static SaleRowDataGateway find(int id) throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_SQL)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return load(rs);
                } else {
                    throw new RecordNotFoundException("Sale with id " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error finding sale.", e);
        }
    }

    private static SaleRowDataGateway load(ResultSet rs) throws SQLException {
        SaleRowDataGateway sale = new SaleRowDataGateway();
        sale.id = rs.getInt("id");
        sale.date = rs.getDate("open_date");
        sale.status = SaleStatus.valueOf(rs.getString("status"));
        sale.customerId = rs.getInt("customer_id");
        return sale;
    }
}