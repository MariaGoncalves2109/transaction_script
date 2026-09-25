package dataaccess.rdgw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;

public class ConfigurationRowDataGateway {

	private static ConfigurationRowDataGateway configurationRow;

	private final double totalAmountPercentage;
	private final double amountThreshold;
	private final double eligiblePercentage;

	private ConfigurationRowDataGateway(double totalAmountPercentage, double amountThreshold,
			double eligiblePercentage) {
		this.totalAmountPercentage = totalAmountPercentage;
		this.amountThreshold = amountThreshold;
		this.eligiblePercentage = eligiblePercentage;
	}

	public double getAmountThresholdPercentage() {
		return totalAmountPercentage;
	}

	public double getAmountThreshold() {
		return amountThreshold;
	}

	public double getEligiblePercentage() {
		return eligiblePercentage;
	}

	private static final String GET_APP_CONFIG_SQL =
			"select id, totalAmountPercentage, amountThreshold, eligiblePercentage " +
				"from appconfig ";

	public static ConfigurationRowDataGateway getConfiguration() throws PersistenceException {
		if (configurationRow == null)
			try (PreparedStatement comando = DataSource.INSTANCE.prepare(GET_APP_CONFIG_SQL)) {
				try (ResultSet rs = comando.executeQuery()) {
					rs.next();
					configurationRow = new ConfigurationRowDataGateway(rs.getDouble("totalAmountPercentage"),
							rs.getDouble("amountThreshold"), rs.getDouble("eligiblePercentage"));
				}
			} catch (SQLException e) {
				throw new PersistenceException("Internal error obtaning app configuration", e);
			}
		return configurationRow;
	}
}