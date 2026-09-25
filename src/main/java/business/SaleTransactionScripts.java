package business;

import java.sql.Date;
import java.util.function.Predicate;

import business.exception.ApplicationException;
import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;
import dataaccess.rdgw.ConfigurationRowDataGateway;
import dataaccess.rdgw.CustomerRowDataGateway;
import dataaccess.rdgw.ProductRowDataGateway;
import dataaccess.rdgw.SaleProductRowDataGateway;
import dataaccess.rdgw.SaleRowDataGateway;

public class SaleTransactionScripts {

	public int newSale (int vat) throws ApplicationException {
		return 0;
		// TODO complete me
	}

	public void addProductToSale(int saleId, int productCode, double qty) throws ApplicationException {
		// TODO complete me
	}

	private void decreaseStockValue(ProductRowDataGateway product, double qty)
			throws PersistenceException {
		// TODO complete me
	}

	private void addProductToSale(ProductRowDataGateway product, SaleRowDataGateway sale, double qty) throws PersistenceException {
		// TODO complete me
	}

	private SaleRowDataGateway getSale(int saleId) throws ApplicationException {
		return null;
		// TODO complete me
	}

	private ProductRowDataGateway getProduct(int productCode) throws ApplicationException {
		return null;
		// TODO complete me
	}

	public double getSaleDiscount (int saleId) throws ApplicationException {
		return 0.0;
		// TODO complete me
	}

	private double computeDiscount(CustomerRowDataGateway customer,
			Iterable<SaleProductRowDataGateway> saleProducts) throws PersistenceException {
		return 0.0;
		// TODO complete me
	}

	private double discountOnSaleAmount(Iterable<SaleProductRowDataGateway> saleProducts)
			throws PersistenceException {
		return 0.0;
		// TODO complete me
	}

	private double discountOnEligibleProducts(Iterable<SaleProductRowDataGateway> saleProducts)
			throws PersistenceException {
		return 0.0;
		// TODO complete me
	}

	private double computeSaleTotal(Iterable<SaleProductRowDataGateway> saleProducts,
			Predicate<ProductRowDataGateway> filter)
			throws PersistenceException {
		return 0.0;
		// TODO complete me
	}
}