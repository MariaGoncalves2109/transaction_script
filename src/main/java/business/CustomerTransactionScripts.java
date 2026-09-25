package business;

import business.exception.ApplicationException;
import dataaccess.exception.PersistenceException;
import dataaccess.rdgw.CustomerRowDataGateway;

public class CustomerTransactionScripts {

	public void addCustomer (int vat, String denomination, int phoneNumber, DiscountType discountType)
			throws ApplicationException {
		if (!isValidVAT (vat))
			throw new ApplicationException ("Invalid VAT number: " + vat);

	    if (!isFilled (denomination) || phoneNumber == 0)
	      throw new ApplicationException(
	             "Both denomination and phoneNumber must be filled");

		try {
			CustomerRowDataGateway newCustomer = new CustomerRowDataGateway (vat, denomination, phoneNumber, discountType);
			newCustomer.insert();
		} catch (PersistenceException e) {
				throw new ApplicationException ("Error inserting the customer into the database", e);
		}
	}

	private boolean isValidVAT(int vat) {
		//TODO complete me
		return false;
	}

	private boolean isFilled(String value) {
		//TODO complete me
		return false;}
}