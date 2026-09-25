package SaleSys;

import presentation.CustomerService;
import presentation.SaleService;
import business.CustomerTransactionScripts;
import business.SaleTransactionScripts;
import business.exception.ApplicationException;
import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;

public class SaleSys {

	private CustomerService customerService;
	private SaleService saleService;

	public void run() throws ApplicationException {
		try {
			DataSource.INSTANCE.connect();
			customerService = new CustomerService(new CustomerTransactionScripts());
			saleService = new SaleService(new SaleTransactionScripts());
		} catch (PersistenceException e) {
			throw new ApplicationException("Error connecting database", e);
		}
	}

	public void stopRun() {
		DataSource.INSTANCE.close();
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public SaleService getSaleService() {
		return saleService;
	}
}