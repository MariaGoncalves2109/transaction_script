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
public int newSale(int vat) throws ApplicationException {
		try {
			CustomerRowDataGateway c = CustomerRowDataGateway.findWithVATNumber(vat);
			if(c==null){
			 	throw new ApplicationException("Não existe cliente com este VAT.");

			}
			SaleRowDataGateway novaVenda = new SaleRowDataGateway(vat, new Date(System.currentTimeMillis()));
			novaVenda.insert();
			return novaVenda.getId();
		} catch (PersistenceException e) {
			throw new ApplicationException ("Não foi possível criar a venda", e);
		}

}

	public void addProductToSale(int saleId, int productCode, double qty) throws ApplicationException {
		try {
			//Verificar se a venda existe
			SaleRowDataGateway venda = getSale(saleId);
			//Verificr se o produto existe 
			ProductRowDataGateway product = getProduct(productCode);
			if(venda!=null && product!=null){
				try{
					DataSource.INSTANCE.beginTransaction();
					decreaseStockValue(product, qty);
					SaleProductRowDataGateway saleproduct = new SaleProductRowDataGateway(venda.getId(), product.getProductId(), qty);
					saleproduct.insert();
					DataSource.INSTANCE.commit();
				}catch(PersistenceException e){
					DataSource.INSTANCE.rollback();
					throw new PersistenceException("Erro: Não foi possível concluir a transação.");
				}
			}
			else{
				throw new ApplicationException("Produto ou Venda não existem.");
			}
		} catch (PersistenceException e) {
				throw new ApplicationException ("Não foi possível adicionar o produto à venda.", e);
		}
	}

	private void decreaseStockValue(ProductRowDataGateway product, double qty)
			throws PersistenceException {
		try{
			if(qty>0 && qty<=product.getQty()){
			product.setQty(product.getQty() - qty);
			product.updateStockValue();
		}
		}catch(PersistenceException e){
			throw new PersistenceException("O produto não existe.");
		}

	}


	private SaleRowDataGateway getSale(int saleId) throws ApplicationException {
		try{
			return SaleRowDataGateway.find(saleId);

		}catch(PersistenceException e){
			throw new ApplicationException("Não existe venda com o id fornecido.");
		}
	}

	private ProductRowDataGateway getProduct(int productCode) throws ApplicationException {
		try{
			return ProductRowDataGateway.findWithProdCod(productCode);
		}catch(PersistenceException e){
			throw new ApplicationException("Não existe produto com o productCode fornecido");
		}
	}

	public double getSaleDiscount (int saleId) throws ApplicationException {
		try{
			SaleRowDataGateway sale = SaleRowDataGateway.find(saleId);
			return sale.getDiscount();
		}catch(PersistenceException e){
			throw new ApplicationException("Não existe sale com este id");
		}
	}

	private double computeDiscount(CustomerRowDataGateway customer,
			Iterable<SaleProductRowDataGateway> saleProducts) throws PersistenceException {
			double discount = 0.0;
			discount += discountOnSaleAmount(saleProducts);
			discount += discountOnEligibleProducts(saleProducts);
			return discount;
	}

	private double discountOnSaleAmount(Iterable<SaleProductRowDataGateway> saleProducts)
			throws PersistenceException {

			double total = computeSaleTotal(saleProducts, p -> true);

			if (total >= 100) {
				return total * 0.10;
			}

			return 0.0;
	}

	private double discountOnEligibleProducts(Iterable<SaleProductRowDataGateway> saleProducts)
		throws PersistenceException {
		double eligibleTotal = computeSaleTotal(saleProducts,p -> p.isEligibleForDiscount());
		return eligibleTotal * 0.05;
	}

	private double computeSaleTotal(Iterable<SaleProductRowDataGateway> saleProducts,
			Predicate<ProductRowDataGateway> filter)
			throws PersistenceException {
			double total = 0.0;

				for (SaleProductRowDataGateway sp : saleProducts) {
					ProductRowDataGateway product =
							ProductRowDataGateway.find(sp.getProductId());

					if (filter.test(product)) {
						total += product.getFaceValue() * sp.getQty();
					}
				}

				return total;
	}
}

