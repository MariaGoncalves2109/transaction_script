package dataaccess.rdgw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;

public class SaleProductRowDataGateway {

    private int saleId;
    private int productId;
    private double qty;

    private static final String INSERT_PRODUCT_SALE_SQL = "INSERT INTO sale_product (sale_id, product_id, qty) VALUES (?, ?, ?)";
    private static final String GET_SALE_PRODUCTS_SQL = "SELECT sale_id, product_id, qty FROM sale_product WHERE sale_id = ?";

    public SaleProductRowDataGateway(int saleId, int productId, double qty) {
        this.saleId = saleId;
        this.productId = productId;
        this.qty = qty;
    }

    public int getProductId() { return productId; }
    public double getQty() { return qty; }

    public void insert() throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(INSERT_PRODUCT_SALE_SQL)) {
            statement.setInt(1, this.saleId);
            statement.setInt(2, this.productId);
            statement.setDouble(3, this.qty);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Error adding product to sale.", e);
        }
    }

    public static Set<SaleProductRowDataGateway> findSaleProducts(int saleId) throws PersistenceException {
        Set<SaleProductRowDataGateway> result = new HashSet<>();
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_PRODUCTS_SQL)) {
            statement.setInt(1, saleId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(load(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("Error finding products for sale " + saleId, e);
        }
    }

    private static SaleProductRowDataGateway load(ResultSet rs) throws SQLException {
        return new SaleProductRowDataGateway(
                rs.getInt("sale_id"),
                rs.getInt("product_id"),
                rs.getDouble("qty")
        );
    }
}