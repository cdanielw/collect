/**
 * This class is generated by jOOQ
 */
package org.openforis.collect.persistence.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = {"http://www.jooq.org", "2.5.0"},
                            comments = "This class is generated by jOOQ")
public class OfcSurvey extends org.jooq.impl.UpdatableTableImpl<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord> {

	private static final long serialVersionUID = 82843106;

	/**
	 * The singleton instance of collect.ofc_survey
	 */
	public static final org.openforis.collect.persistence.jooq.tables.OfcSurvey OFC_SURVEY = new org.openforis.collect.persistence.jooq.tables.OfcSurvey();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord> getRecordType() {
		return org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord.class;
	}

	/**
	 * The table column <code>collect.ofc_survey.id</code>
	 * <p>
	 * This column is part of the table's PRIMARY KEY
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord, java.lang.Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The table column <code>collect.ofc_survey.name</code>
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR, this);

	/**
	 * The table column <code>collect.ofc_survey.uri</code>
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord, java.lang.String> URI = createField("uri", org.jooq.impl.SQLDataType.VARCHAR, this);

	/**
	 * The table column <code>collect.ofc_survey.idml</code>
	 */
	public final org.jooq.TableField<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord, java.lang.String> IDML = createField("idml", org.jooq.impl.SQLDataType.CLOB, this);

	public OfcSurvey() {
		super("ofc_survey", org.openforis.collect.persistence.jooq.Collect.COLLECT);
	}

	public OfcSurvey(java.lang.String alias) {
		super(alias, org.openforis.collect.persistence.jooq.Collect.COLLECT, org.openforis.collect.persistence.jooq.tables.OfcSurvey.OFC_SURVEY);
	}

	@Override
	public org.jooq.UniqueKey<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord> getMainKey() {
		return org.openforis.collect.persistence.jooq.Keys.OFC_SURVEY_PKEY;
	}

	@Override
	@SuppressWarnings("unchecked")
	public java.util.List<org.jooq.UniqueKey<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.openforis.collect.persistence.jooq.tables.records.OfcSurveyRecord>>asList(org.openforis.collect.persistence.jooq.Keys.OFC_SURVEY_PKEY, org.openforis.collect.persistence.jooq.Keys.OFC_SURVEY_NAME_KEY, org.openforis.collect.persistence.jooq.Keys.OFC_SURVEY_URI_KEY);
	}

	@Override
	public org.openforis.collect.persistence.jooq.tables.OfcSurvey as(java.lang.String alias) {
		return new org.openforis.collect.persistence.jooq.tables.OfcSurvey(alias);
	}
}
