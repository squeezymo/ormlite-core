package com.j256.ormlite.examples.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;

import javax.sql.DataSource;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.examples.common.Account;
import com.j256.ormlite.examples.common.AccountDao;
import com.j256.ormlite.examples.common.AccountJdbcDao;
import com.j256.ormlite.examples.common.Delivery;
import com.j256.ormlite.examples.common.DeliveryDao;
import com.j256.ormlite.examples.common.DeliveryJdbcDao;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.JdbcType;
import com.j256.ormlite.support.SimpleDataSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

/**
 * Main sample routine to show how to do basic operations with the package.
 */
public class FieldConfigMain {

	// we are using the in-memory H2 database
	private final static String DATABASE_URL = "jdbc:h2:mem:account";

	private AccountDao accountDao;
	private DeliveryDao deliveryDao;

	public static void main(String[] args) throws Exception {
		// turn our static method into an instance of Main
		new FieldConfigMain().doMain(args);
	}

	private void doMain(String[] args) throws Exception {
		SimpleDataSource dataSource = null;
		try {
			// create our data-source for the database
			dataSource = DatabaseTypeUtils.createSimpleDataSource(DATABASE_URL);
			// setup our database and DAOs
			setupDatabase(dataSource);
			// read and write some data
			readWriteData();
		} finally {
			// destroy the data source which should close underlying connections
			if (dataSource != null) {
				dataSource.destroy();
			}
		}
	}

	/**
	 * Setup our database and DAOs
	 */
	private void setupDatabase(DataSource dataSource) throws Exception {

		DatabaseType databaseType = DatabaseTypeUtils.createDatabaseType(dataSource);
		databaseType.loadDriver();

		AccountJdbcDao accountJdbcDao = new AccountJdbcDao();
		accountJdbcDao.setDatabaseType(databaseType);
		accountJdbcDao.setDataSource(dataSource);
		accountJdbcDao.initialize();
		accountDao = accountJdbcDao;

		DatabaseTableConfig<Delivery> tableConfig = buildTableConfig();
		DeliveryJdbcDao deliveryJdbcDao = new DeliveryJdbcDao();
		deliveryJdbcDao.setTableConfig(tableConfig);
		deliveryJdbcDao.setDatabaseType(databaseType);
		deliveryJdbcDao.setDataSource(dataSource);
		deliveryJdbcDao.initialize();
		deliveryDao = deliveryJdbcDao;

		// if you need to create the table
		TableUtils.createTable(databaseType, dataSource, Account.class);
		TableUtils.createTable(databaseType, dataSource, tableConfig);
	}

	private DatabaseTableConfig<Delivery> buildTableConfig() {
		ArrayList<DatabaseFieldConfig> fieldConfigs = new ArrayList<DatabaseFieldConfig>();
		fieldConfigs.add(new DatabaseFieldConfig("id", null, JdbcType.UNKNOWN, null, 0, false, false, true, null,
				false, null, false, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("when", null, JdbcType.UNKNOWN, null, 0, false, false, false, null,
				false, null, false, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("signedBy", null, JdbcType.UNKNOWN, null, 0, false, false, false,
				null, false, null, false, null, false));
		fieldConfigs.add(new DatabaseFieldConfig("account", null, JdbcType.UNKNOWN, null, 0, false, false, false, null,
				true, null, false, null, false));
		DatabaseTableConfig<Delivery> tableConfig = new DatabaseTableConfig<Delivery>(Delivery.class, fieldConfigs);
		return tableConfig;
	}

	/**
	 * Read and write some example data.
	 */
	private void readWriteData() throws Exception {
		// create an instance of Account
		String name = "Jim Coakley";
		Account account = new Account(name);
		// persist the account object to the database, it should return 1
		if (accountDao.create(account) != 1) {
			throw new Exception("Could not create Account in database");
		}

		Delivery delivery = new Delivery(new Date(), "Mr. Ed", account);
		// persist the account object to the database, it should return 1
		if (deliveryDao.create(delivery) != 1) {
			throw new Exception("Could not create Delivery in database");
		}

		Delivery delivery2 = deliveryDao.queryForId(delivery.getId());
		assertNotNull(delivery2);
		assertEquals(delivery.getId(), delivery2.getId());
		assertEquals(account.getId(), delivery2.getAccount().getId());
	}
}