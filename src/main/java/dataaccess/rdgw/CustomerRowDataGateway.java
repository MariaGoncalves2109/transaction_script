package dataaccess.rdgw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import business.DiscountType;
import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

public class CustomerRowDataGateway {

	private int id;
	private int vat;
	private String designation;
	private int phoneNumber;
	private int discountId;

	private static final String INSERT_CUSTOMER_SQL =
			"insert into customer (vatnumber, designation, phonenumber, discount_id) " +
			"values (?, ?, ?, ?)";

	private static final String GET_CUSTOMER_BY_VAT_NUMBER_SQL =
			   "select id, vatnumber, designation, phonenumber, discount_id " +
					"from customer " +
					"where vatnumber = ?";

	private static final String	GET_CUSTOMER_BY_ID_SQL =
				"select id, vatnumber, designation, phonenumber, discount_id " +
					"from customer " +
					"where id = ?";

	private static final int NO_DISCOUNT = 1;
	private static final int SALE_AMOUNT = 2;
	private static final int ELIGIBLE_PRODUCTS = 3;

	public CustomerRowDataGateway(int vat, String designation, int phoneNumber,
			DiscountType discountType) {
		this.vat = vat;
		this.designation = designation;
		this.phoneNumber = phoneNumber;
		setDiscountType(discountType);
	}

	public int getCustomerId() { return id; }
	public int getVAT() { return vat; }
	public String getDesignation() { return designation; }
	public void setDesignation(String designation) { this.designation = designation; }
	public int getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }

	public DiscountType getDiscountType() {
		return toDiscountType(discountId);
	}

	public void setDiscountType(DiscountType discountType) {
		this.discountId = discountType == DiscountType.SALE_AMOUNT ? SALE_AMOUNT :
			discountType == DiscountType.ELIGIBLE_PRODUCTS ? ELIGIBLE_PRODUCTS : NO_DISCOUNT;
	}

	private static DiscountType toDiscountType(int discountId) {
		return discountId == ELIGIBLE_PRODUCTS ? DiscountType.ELIGIBLE_PRODUCTS :
			discountId == SALE_AMOUNT ? DiscountType.SALE_AMOUNT : DiscountType.NO_DISCOUNT;
	}

	public void insert () throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_CUSTOMER_SQL)) {
			statement.setInt(1, vat);
			statement.setString(2, designation);
			statement.setInt(3, phoneNumber);
			statement.setInt(4, discountId);
			statement.executeUpdate();
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next();
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
	}

	public static CustomerRowDataGateway findWithVATNumber (int vat) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_CUSTOMER_BY_VAT_NUMBER_SQL)) {
			statement.setInt(1, vat);
			try (ResultSet rs = statement.executeQuery()) {
				return loadCustomer(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting a customer by its VAT number", e);
		}
	}

	public static CustomerRowDataGateway find (int id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_CUSTOMER_BY_ID_SQL)) {
			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				return loadCustomer(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting a customer by its id", e);
		}
	}

	private static CustomerRowDataGateway loadCustomer(ResultSet rs) throws RecordNotFoundException {
		try {
			if (!rs.next()) {
                throw new RecordNotFoundException("Customer does not exist");
            }
			CustomerRowDataGateway newCustomer = new CustomerRowDataGateway(rs.getInt("vatnumber"),
					rs.getString("designation"), rs.getInt("phonenumber"),
					toDiscountType(rs.getInt("discount_id")));
			newCustomer.id = rs.getInt("id");
			return newCustomer;
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Customer does not exist", e);
		}
	}
}