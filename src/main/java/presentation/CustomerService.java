package presentation;

import business.CustomerTransactionScripts;
import business.DiscountType;
import business.exception.ApplicationException;

public class CustomerService {

	private CustomerTransactionScripts customerTS;

	public CustomerService(CustomerTransactionScripts customerTS) {
		this.customerTS = customerTS;
	}

	public void addCustomer(int vat, String denomination, int phoneNumber,
			DiscountType discountType) throws ApplicationException {
		customerTS.addCustomer(vat, denomination, phoneNumber, discountType);
	}
}