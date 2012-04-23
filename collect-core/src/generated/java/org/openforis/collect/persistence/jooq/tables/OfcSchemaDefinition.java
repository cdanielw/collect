/**
 * This class is generated by jOOQ
 */
package org.openforis.collect.persistence.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = {"http://www.jooq.org", "2.0.1"},
                            comments = "This class is generated by jOOQ")
public class OfcSchemaDefinition extends org.jooq.impl.UpdatableTableImpl<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord> {

	private static final long serialVersionUID = -254898726;

	/**
	 * The singleton instance of ofc_schema_definition
	 */
	public static final org.openforis.collect.persistence.jooq.tables.OfcSchemaDefinition OFC_SCHEMA_DEFINITION = new org.openforis.collect.persistence.jooq.tables.OfcSchemaDefinition();

	/**
	 * The class holding records for this type
	 */
	private static final java.lang.Class<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord> __RECORD_TYPE = org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord.class;

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord> getRecordType() {
		return __RECORD_TYPE;
	}

	/**
	 * An uncommented item
	 * 
	 * PRIMARY KEY
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord, java.lang.Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * An uncommented item
	 * <p>
	 * <code><pre>
	 * FOREIGN KEY [collect.ofc_schema_definition.survey_id]
	 * REFERENCES ofc_survey [collect.ofc_survey.id]
	 * </pre></code>
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord, java.lang.Integer> SURVEY_ID = createField("survey_id", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * An uncommented item
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord, java.lang.String> PATH = createField("path", org.jooq.impl.SQLDataType.VARCHAR, this);

	/**
	 * No further instances allowed
	 */
	private OfcSchemaDefinition() {
		super("ofc_schema_definition", org.openforis.collect.persistence.jooq.Collect.COLLECT);
	}

	/**
	 * No further instances allowed
	 */
	private OfcSchemaDefinition(java.lang.String alias) {
		super(alias, org.openforis.collect.persistence.jooq.Collect.COLLECT, org.openforis.collect.persistence.jooq.tables.OfcSchemaDefinition.OFC_SCHEMA_DEFINITION);
	}

	@Override
	public org.jooq.UniqueKey<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord> getMainKey() {
		return org.openforis.collect.persistence.jooq.Keys.ofc_schema_definition_pkey;
	}

	@Override
	@SuppressWarnings("unchecked")
	public java.util.List<org.jooq.UniqueKey<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord>>asList(org.openforis.collect.persistence.jooq.Keys.ofc_schema_definition_pkey);
	}

	@Override
	@SuppressWarnings("unchecked")
	public java.util.List<org.jooq.ForeignKey<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.openforis.collect.persistence.jooq.tables.records.OfcSchemaDefinitionRecord, ?>>asList(org.openforis.collect.persistence.jooq.Keys.ofc_schema_definition__ofc_schema_definition_survey_fkey);
	}

	@Override
	public org.openforis.collect.persistence.jooq.tables.OfcSchemaDefinition as(java.lang.String alias) {
		return new org.openforis.collect.persistence.jooq.tables.OfcSchemaDefinition(alias);
	}
}
