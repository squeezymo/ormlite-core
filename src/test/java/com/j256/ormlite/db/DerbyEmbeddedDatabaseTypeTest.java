package com.j256.ormlite.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.j256.ormlite.TestUtils;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.table.TableInfo;

public class DerbyEmbeddedDatabaseTypeTest extends BaseDatabaseTest {

	@Override
	protected void setDatabaseParams() throws SQLException {
		System.setProperty("derby.stream.error.file", "target/derby.log");
		databaseUrl = "jdbc:derby:target/ormlitederby;create=true";
		dataSource = DatabaseTypeUtils.createSimpleDataSource(DEFAULT_DATABASE_URL);
	}

	@Override
	protected boolean isDriverClassExpected() {
		return false;
	}

	@Test
	public void testGetDriverClassName() {
		assertEquals("org.apache.derby.jdbc.EmbeddedDriver", databaseType.getDriverClassName());
	}

	@Override
	@Test
	public void testEscapedEntityName() throws Exception {
		String word = "word";
		assertEquals("\"" + word + "\"", TestUtils.appendEscapedEntityName(databaseType, word));
	}

	@Test
	public void testGeneratedId() throws Exception {
		TableInfo<GeneratedId> tableInfo = new TableInfo<GeneratedId>(databaseType, GeneratedId.class);
		StringBuilder sb = new StringBuilder();
		List<String> additionalArgs = new ArrayList<String>();
		List<String> statementsBefore = new ArrayList<String>();
		databaseType.appendColumnArg(sb, tableInfo.getFieldTypes()[0], additionalArgs, statementsBefore, null, null);
		assertTrue(sb + "should contain the stuff", sb.toString().contains(" INTEGER GENERATED BY DEFAULT AS IDENTITY"));
		assertEquals(0, statementsBefore.size());
		assertEquals(1, additionalArgs.size());
		assertTrue(additionalArgs.get(0).contains("PRIMARY KEY"));
	}

	@Test
	public void testBoolean() throws Exception {
		TableInfo<AllTypes> tableInfo = new TableInfo<AllTypes>(databaseType, AllTypes.class);
		assertEquals(9, tableInfo.getFieldTypes().length);
		FieldType booleanField = tableInfo.getFieldTypes()[1];
		assertEquals("booleanField", booleanField.getDbColumnName());
		StringBuilder sb = new StringBuilder();
		List<String> additionalArgs = new ArrayList<String>();
		List<String> statementsBefore = new ArrayList<String>();
		databaseType.appendColumnArg(sb, booleanField, additionalArgs, statementsBefore, null, null);
		assertTrue(sb.toString().contains("SMALLINT"));
	}

	@Test
	public void testByte() throws Exception {
		TableInfo<AllTypes> tableInfo = new TableInfo<AllTypes>(databaseType, AllTypes.class);
		assertEquals(9, tableInfo.getFieldTypes().length);
		FieldType byteField = tableInfo.getFieldTypes()[3];
		assertEquals("byteField", byteField.getDbColumnName());
		StringBuilder sb = new StringBuilder();
		List<String> additionalArgs = new ArrayList<String>();
		List<String> statementsBefore = new ArrayList<String>();
		databaseType.appendColumnArg(sb, byteField, additionalArgs, statementsBefore, null, null);
		assertTrue(sb.toString().contains("SMALLINT"));
	}

	@Override
	@Test
	public void testLimitSupport() throws Exception {
		assertFalse(databaseType.isLimitSupported());
	}
}