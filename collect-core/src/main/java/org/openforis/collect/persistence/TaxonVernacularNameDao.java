package org.openforis.collect.persistence;

import static org.openforis.collect.persistence.jooq.Sequences.OFC_TAXON_VERNACULAR_NAME_ID_SEQ;
import static org.openforis.collect.persistence.jooq.tables.OfcTaxon.OFC_TAXON;
import static org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.BatchBindStep;
import org.jooq.Field;
import org.jooq.Insert;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.StoreQuery;
import org.jooq.TableField;
import org.jooq.impl.Factory;
import org.openforis.collect.persistence.jooq.MappingJooqDaoSupport;
import org.openforis.collect.persistence.jooq.MappingJooqFactory;
import org.openforis.collect.persistence.jooq.tables.records.OfcTaxonVernacularNameRecord;
import org.openforis.idm.model.species.TaxonVernacularName;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author G. Miceli
 * @author S. Ricci
 * @author E. Wibowo
 */
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TaxonVernacularNameDao extends MappingJooqDaoSupport<TaxonVernacularName, TaxonVernacularNameDao.JooqFactory> {
	
	private static final TableField[] QUALIFIER_FIELDS = {OFC_TAXON_VERNACULAR_NAME.QUALIFIER1, OFC_TAXON_VERNACULAR_NAME.QUALIFIER2, OFC_TAXON_VERNACULAR_NAME.QUALIFIER3};

	protected static Field<?>[] TAXON_VERNACULAR_NAME_FIELDS;
	static {
		Field<?>[] fields = {
				OFC_TAXON_VERNACULAR_NAME.ID,
				OFC_TAXON_VERNACULAR_NAME.TAXON_ID,
				OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME,
				OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE,
				OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY,
				OFC_TAXON_VERNACULAR_NAME.STEP};
		fields = ArrayUtils.addAll(fields, QUALIFIER_FIELDS);
		TAXON_VERNACULAR_NAME_FIELDS = fields;
	}

	public TaxonVernacularNameDao() {
		super(TaxonVernacularNameDao.JooqFactory.class);
	}

	@Override
	public TaxonVernacularName loadById(int id) {
		return super.loadById(id);
	}

	@Override
	public void insert(TaxonVernacularName entity) {
		super.insert(entity);
	}

	@Override
	public void update(TaxonVernacularName entity) {
		super.update(entity);
	}

	@Override
	public void delete(int id) {
		super.delete(id);
	}
	
	public List<TaxonVernacularName> findByVernacularName(int taxonomyId, String searchString, int maxResults) {
		return findByVernacularName(taxonomyId, searchString, null, maxResults);
	}	
	
	public List<TaxonVernacularName> findByVernacularName(int taxonomyId, String searchString, String[] qualifierValues, int maxResults) {
		JooqFactory jf = getMappingJooqFactory();
		//find containing
		searchString = "%" + searchString.toUpperCase() + "%";
		
		SelectConditionStep selectConditionStep = jf.select(OFC_TAXON_VERNACULAR_NAME.getFields())
			.from(OFC_TAXON_VERNACULAR_NAME)
			.join(OFC_TAXON).on(OFC_TAXON.ID.equal(OFC_TAXON_VERNACULAR_NAME.TAXON_ID))
			.where(OFC_TAXON.TAXONOMY_ID.equal(taxonomyId)
				.and(JooqFactory.upper(OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME).like(searchString)));
		
		if ( qualifierValues != null ) {
			for (int i = 0; i < qualifierValues.length; i++) {
				String value = qualifierValues[i];
				if ( value != null ) {
					TableField field = QUALIFIER_FIELDS[i];
					selectConditionStep.and(field.equal(value));
				}
			}
		}
		selectConditionStep.limit(maxResults);
		Result<?> result = selectConditionStep.fetch();
		List<TaxonVernacularName> entities = jf.fromResult(result);
		return entities;
	}

	public List<TaxonVernacularName> findByTaxon(int taxonId) {
		JooqFactory jf = getMappingJooqFactory();
		
		SelectConditionStep selectConditionStep = jf.select(OFC_TAXON_VERNACULAR_NAME.getFields())
			.from(OFC_TAXON_VERNACULAR_NAME)
			.where(OFC_TAXON_VERNACULAR_NAME.TAXON_ID.equal(taxonId));
		
		Result<?> result = selectConditionStep.fetch();
		List<TaxonVernacularName> entities = jf.fromResult(result);
		return entities;
	}

	
	public void deleteByTaxonomy(int taxonomyId) {
		JooqFactory jf = getMappingJooqFactory();
		SelectConditionStep selectTaxonIds = jf.select(OFC_TAXON.ID).from(OFC_TAXON).where(OFC_TAXON.TAXONOMY_ID.equal(taxonomyId));
		jf.delete(OFC_TAXON_VERNACULAR_NAME).where(OFC_TAXON_VERNACULAR_NAME.TAXON_ID.in(selectTaxonIds)).execute();
	}
	
	public void insert(List<TaxonVernacularName> items) {
		if ( items != null && ! items.isEmpty() ) {
			JooqFactory jf = getMappingJooqFactory();
			int id = jf.nextId(OFC_TAXON_VERNACULAR_NAME.ID, OFC_TAXON_VERNACULAR_NAME_ID_SEQ);
			int maxId = id;
			Insert<OfcTaxonVernacularNameRecord> query = jf.createInsertStatement();
			BatchBindStep batch = jf.batch(query);
			for (TaxonVernacularName item : items) {
				if ( item.getId() == null ) {
					item.setId(id++);
				}
				Object[] values = jf.extractValues(item);
				batch.bind(values);
				maxId = Math.max(maxId, item.getId());
			}
			batch.execute();
			jf.restartSequence(OFC_TAXON_VERNACULAR_NAME_ID_SEQ, maxId + 1);
		}
	}

	public void duplicateVernacularNames(int oldTaxonomyId,
			int taxonIdGap) {
		JooqFactory jf = getMappingJooqFactory();
		int nextId = jf.nextId(OFC_TAXON_VERNACULAR_NAME.ID, OFC_TAXON_VERNACULAR_NAME_ID_SEQ);
		int minId = loadMinId(jf, oldTaxonomyId);
		int idGap = nextId - minId;
		Field<?>[] selectFields = {
				OFC_TAXON_VERNACULAR_NAME.ID.add(idGap),
				OFC_TAXON_VERNACULAR_NAME.TAXON_ID.add(taxonIdGap),
				OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME,
				OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE,
				OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY,
				OFC_TAXON_VERNACULAR_NAME.STEP};
		selectFields = ArrayUtils.addAll(selectFields, QUALIFIER_FIELDS);
		Select<?> select = jf.select(selectFields)
				.from(OFC_TAXON_VERNACULAR_NAME)
					.join(OFC_TAXON)
					.on(OFC_TAXON.ID.equal(OFC_TAXON_VERNACULAR_NAME.TAXON_ID))
				.where(OFC_TAXON.TAXONOMY_ID.equal(oldTaxonomyId))
				.orderBy(OFC_TAXON_VERNACULAR_NAME.TAXON_ID, OFC_TAXON_VERNACULAR_NAME.ID);
		Insert<?> insert = jf.insertInto(OFC_TAXON_VERNACULAR_NAME, TAXON_VERNACULAR_NAME_FIELDS).select(select);
		int insertedCount = insert.execute();
		nextId = nextId + insertedCount;
		jf.restartSequence(OFC_TAXON_VERNACULAR_NAME_ID_SEQ, nextId);
	}
	
	protected int loadMinId(JooqFactory jf, int taxonomyId) {
		Integer minId = jf.select(Factory.min(OFC_TAXON_VERNACULAR_NAME.ID))
				.from(OFC_TAXON_VERNACULAR_NAME)
					.join(OFC_TAXON)
					.on(OFC_TAXON.ID.equal(OFC_TAXON_VERNACULAR_NAME.TAXON_ID))
				.where(OFC_TAXON.TAXONOMY_ID.equal(taxonomyId))
				.fetchOne(0, Integer.class);
		return minId == null ? 0: minId.intValue();
	}

	public int nextId() {
		JooqFactory jf = getMappingJooqFactory();
		return jf.nextId(OFC_TAXON_VERNACULAR_NAME.ID, OFC_TAXON_VERNACULAR_NAME_ID_SEQ);
	}

	protected static class JooqFactory extends MappingJooqFactory<TaxonVernacularName> {

		private static final long serialVersionUID = 1L;

		public JooqFactory(Connection connection) {
			super(connection, OFC_TAXON_VERNACULAR_NAME.ID, OFC_TAXON_VERNACULAR_NAME_ID_SEQ, TaxonVernacularName.class);
		}

		@Override
		public void fromRecord(Record r, TaxonVernacularName t) {
			t.setId(r.getValue(OFC_TAXON_VERNACULAR_NAME.ID));
			t.setVernacularName(r.getValue(OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME));
			t.setLanguageCode(r.getValue(OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE));
			t.setLanguageVariety(r.getValue(OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY));
			t.setTaxonSystemId(r.getValue(OFC_TAXON_VERNACULAR_NAME.TAXON_ID));
			t.setStep(r.getValue(OFC_TAXON_VERNACULAR_NAME.STEP));
			List<String> q = new ArrayList<String>();
			for ( TableField field : QUALIFIER_FIELDS ) {
				q.add((String) r.getValue(field));
			}
			t.setQualifiers(q);
		}

		@Override
		public void fromObject(TaxonVernacularName t, StoreQuery<?> q) {
			q.addValue(OFC_TAXON_VERNACULAR_NAME.ID, t.getId());
			q.addValue(OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME,
					t.getVernacularName());
			q.addValue(OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE,
					t.getLanguageCode());
			q.addValue(OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY,
					t.getLanguageVariety());
			q.addValue(OFC_TAXON_VERNACULAR_NAME.TAXON_ID, t.getTaxonSystemId());
			q.addValue(OFC_TAXON_VERNACULAR_NAME.STEP, t.getStep());
			
			List<String> qualifiers = t.getQualifiers();
			for (int i = 0; i < qualifiers.size(); i++) {
				q.addValue(QUALIFIER_FIELDS[i], qualifiers.get(i));
			}
		}
		
		protected Insert<OfcTaxonVernacularNameRecord> createInsertStatement() {
			Object[] valuesPlaceholders = new String[TAXON_VERNACULAR_NAME_FIELDS.length];
			Arrays.fill(valuesPlaceholders, "?");
			return insertInto(OFC_TAXON_VERNACULAR_NAME, TAXON_VERNACULAR_NAME_FIELDS).values(valuesPlaceholders);
		}
		
		protected Object[] extractValues(TaxonVernacularName item) {
			Object[] values = {
					item.getId(), 
					item.getTaxonSystemId(),
					item.getVernacularName(),
					item.getLanguageCode(),
					item.getLanguageVariety(),
					item.getStep()
					};
			Object[] qualifierValues = extractQualifierValues(item);
			values = ArrayUtils.addAll(values, qualifierValues);
			return values;
		}

		protected String[] extractQualifierValues(TaxonVernacularName item) {
			String[] result = new String[QUALIFIER_FIELDS.length];
			List<String> itemQualifiers = item.getQualifiers();
			for (int i = 0; i < QUALIFIER_FIELDS.length; i++) {
				String value = i < itemQualifiers.size() ? itemQualifiers.get(i): null;
				result[i] = value;
			}
			return result;
		}

		@Override
		protected void setId(TaxonVernacularName t, int id) {
			t.setId(id);
		}

		@Override
		protected Integer getId(TaxonVernacularName t) {
			return t.getId();
		}
	}

}
