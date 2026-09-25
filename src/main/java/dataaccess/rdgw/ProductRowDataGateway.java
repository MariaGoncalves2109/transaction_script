package dataaccess.rdgw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

public class ProductRowDataGateway {

	private int id;
	private int prodCod;
	private String description;
	private double faceValue;
	private double qty;
	private String discountEligibility;
	private int unitId;

	private static final String	UPDATE_STOCK_SQL = "update product set qty = ? where id = ?";
	private static final String GET_PRODUCT_BY_PROD_COD_SQL = "select id, prodcod, description, facevalue, qty, discounteligibility, unit_id from product where prodcod = ?";
	private static final String GET_PRODUCT_BY_ID_SQL = "select id, prodcod, description, facevalue, qty, discounteligibility, unit_id from product where id = ?";

	private static final String ELIGIBLE = "E";
	private static final String NOT_ELIGIBLE = "N";
    
    // Private constructor to be used by loaders
    private ProductRowDataGateway() {}

	public int getProductId() { return id; }
	public int getProdCod() { return prodCod; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public double getFaceValue() { return faceValue; }
	public void setFaceValue(double faceValue) { this.faceValue = faceValue; }
	public double getQty() { return qty; }
	public void setQty(double qty) { this.qty = qty; }
	public boolean isEligibleForDiscount() { return ELIGIBLE.equals(discountEligibility); }
	public void setEligibleForDiscount(boolean eligibleForDiscount) { this.discountEligibility = eligibleForDiscount ? ELIGIBLE : NOT_ELIGIBLE; }
	public int getUnitId() { return unitId; }
	public void setUnitId(int unitId) { this.unitId = unitId; }

	public static ProductRowDataGateway findWithProdCod (int prodCod) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_PRODUCT_BY_PROD_COD_SQL)) {
			statement.setInt(1, prodCod);
			try (ResultSet rs = statement.executeQuery()) {
				return loadProduct(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting product with code " + prodCod, e);
		}
	}

	public static ProductRowDataGateway find (int id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_PRODUCT_BY_ID_SQL)) {
			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				return loadProduct(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting product with id " + id, e);
		}
	}

	public void updateStockValue () throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_STOCK_SQL)){
			statement.setDouble(1, qty);
			statement.setInt(2, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Internal error updating product " + id + " stock amount.", e);
		}
	}

	private static ProductRowDataGateway loadProduct(ResultSet rs) throws RecordNotFoundException {
		try {
			if (!rs.next()) {
                throw new RecordNotFoundException("Product not found");
            }
			ProductRowDataGateway newProduct = new ProductRowDataGateway();
			newProduct.id = rs.getInt("id");
			newProduct.prodCod = rs.getInt("prodcod");
			newProduct.description = rs.getString("description");
			newProduct.faceValue = rs.getDouble("facevalue");
			newProduct.qty = rs.getDouble("qty");
			newProduct.discountEligibility = rs.getString("discounteligibility");
			newProduct.unitId = rs.getInt("unit_id");
			return newProduct;
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Product not found", e);
		}
	}
}