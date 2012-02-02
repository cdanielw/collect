package org.openforis.collect.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openforis.collect.model.CollectAttributeMetadata;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.RecordSummary;
import org.openforis.collect.persistence.xml.CollectIdmlBindingContext;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.xml.InvalidIdmlException;
import org.openforis.idm.metamodel.xml.SurveyUnmarshaller;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:test-context.xml"} )
@TransactionConfiguration(defaultRollback=false)
@Transactional
public class ModelDAOIntegrationTest {
	private final Log log = LogFactory.getLog(ModelDAOIntegrationTest.class);
	
	@Autowired
	protected SurveyDAO surveyDao;
	
	@Autowired
	protected RecordDAO recordDao;

	@Test
	public void testCRUD() throws Exception  {
//		try {
		// LOAD MODEL
		Survey survey = surveyDao.load("archenland1");

		if ( survey == null ) {
			// IMPORT MODEL
			survey = importModel();
		}
		
		// SAVE NEW
		CollectRecord record = createRecord(survey);
		recordDao.saveOrUpdate(record);
		
		String saved = record.toString();
		log.debug("Saving record:\n"+saved);
		
		// RELOAD
		record = recordDao.load(survey, record.getId());
		String reloaded = record.toString();
		log.debug("Reloaded as:\n"+reloaded);
		
		assertEquals(saved, reloaded);
		
		// UPDATE
//		updateRecord(record);
		
//		assertEquals(1, cluster.getCount("time_study"));

//		recordDao.saveOrUpdate(record);
//		} catch (DataAccessException ex){
//			ex.getCause().getCause().getCause().printStackTrace();
//		}
	}
	
	@Test
	public void testSurveyNotFoundById() {		
		Survey survey = surveyDao.load(-100);
		assertNull(survey);
	}
	
	@Test
	public void testSurveyNotFoundByName() {		
		Survey survey = surveyDao.load("!!!!!!");
		assertNull(survey);
	}

	private Survey importModel() throws IOException, SurveyImportException, InvalidIdmlException {
		URL idm = ClassLoader.getSystemResource("test.idm.xml");
		InputStream is = idm.openStream();
		CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext();
		SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();
		Survey survey = surveyUnmarshaller.unmarshal(is);
		surveyDao.importModel(survey);
		return survey;
	}

	private CollectRecord createRecord(Survey survey) {
		CollectRecord record = new CollectRecord(survey, "cluster", "2.0");
		record.setCreationDate(new GregorianCalendar(2011, 12, 31, 23, 59).getTime());
		//record.setCreatedBy("ModelDAOIntegrationTest");
		record.setStep(Step.ENTRY);
		Entity cluster = record.getRootEntity();
		String id = "123_456";
		cluster.addValue("id", new Code(id));
		cluster.addValue("gps_realtime", Boolean.TRUE);
		cluster.addValue("region", new Code("001"));
		cluster.addValue("district", new Code("002"));
		cluster.addValue("crew_no", 10);
		cluster.addValue("map_sheet", "value 1");
		cluster.addValue("map_sheet", "value 2");
		cluster.addValue("vehicle_location", new Coordinate(432423423l, 4324324l,"srs"));
		cluster.addValue("gps_model", "TomTom 1.232");
		{
			Entity ts = cluster.addEntity("time_study");
			ts.addValue("date", new Date(2011,2,14));
			ts.addValue("start_time", new Time(8,15));
			ts.addValue("end_time", new Time(15,29));
		}
		{
			Entity ts = cluster.addEntity("time_study");
			ts.addValue("date", new Date(2011,2,15));
			ts.addValue("start_time", new Time(8,32));
			ts.addValue("end_time", new Time(11,20));
		}
		{
			Entity plot = cluster.addEntity("plot");
			plot.addValue("no", new Code("1"));
			Entity tree1 = plot.addEntity("tree");
			tree1.addValue("tree_no", 1);
			tree1.addValue("dbh", 54.2);
			tree1.addValue("total_height", 2.0);
			tree1.addValue("bole_height", (Double) null).setMetadata(new CollectAttributeMetadata('*',null,"No value specified"));
			Entity tree2 = plot.addEntity("tree");
			tree2.addValue("tree_no", 2);
			tree2.addValue("dbh", 82.8);
			tree2.addValue("total_height", 3.0);
		}
		{
			Entity plot = cluster.addEntity("plot");
			plot.addValue("no", new Code("2"));
			Entity tree1 = plot.addEntity("tree");
			tree1.addValue("tree_no", 1);
			tree1.addValue("dbh", 34.2);
			tree1.addValue("total_height", 2.0);
			Entity tree2 = plot.addEntity("tree");
			tree2.addValue("tree_no", 2);
			tree2.addValue("dbh", 85.8);
			tree2.addValue("total_height", 4.0);
		}
		//set counts
		EntityDefinition plotDef = (EntityDefinition) cluster.getDefinition().getChildDefinition("plot");
		record.getCounts().put(plotDef.getPath(), 2);
		//set keys
		NodeDefinition idDef = cluster.getDefinition().getChildDefinition("id");
		record.getKeys().put(idDef.getPath(), id);
		//System.err.println(record);
		return record;
	}

	@Test
	public void testLoadAllSurvey(){
		List<Survey> list = this.surveyDao.loadAll();
		assertNotNull(list);
		assertEquals(1, list.size());
		
	}
	
	@Test
	public void testLoadRecordSummaries() {
		Survey survey = surveyDao.load("archenland1");
		//get the first root entity
		EntityDefinition rootEntity = survey.getSchema().getRootEntityDefinitions().get(0);
		String rootEntityName = rootEntity.getName();
		int offset = 0;
		int maxNumberOfRecords = 1;
		String orderByFieldName = "key_id";
		String filter = null;
		EntityDefinition plotDefn = (EntityDefinition) rootEntity.getChildDefinition("plot");
		List<RecordSummary> list = this.recordDao.loadSummaries(survey, rootEntityName, Arrays.asList(plotDefn), offset, maxNumberOfRecords, orderByFieldName, filter);
		assertNotNull(list);
		assertEquals(1, list.size());
		
		RecordSummary summary = list.get(0);
		assertEquals(1, summary.getStep());
	}
}