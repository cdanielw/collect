package org.openforis.collect.util {
	
	import org.granite.collections.BasicMap;
	import org.openforis.collect.manager.dataexport.proxy.DataExportStatusProxy;
	import org.openforis.collect.manager.process.ProcessStatus;
	import org.openforis.collect.manager.process.ProcessStatus$Step;
	import org.openforis.collect.metamodel.NodeDefinitionSummary;
	import org.openforis.collect.metamodel.proxy.AttributeDefaultProxy;
	import org.openforis.collect.metamodel.proxy.AttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.BooleanAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.CodeAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.CodeListItemProxy;
	import org.openforis.collect.metamodel.proxy.CodeListLabelProxy;
	import org.openforis.collect.metamodel.proxy.CodeListLabelProxy$Type;
	import org.openforis.collect.metamodel.proxy.CodeListLevelProxy;
	import org.openforis.collect.metamodel.proxy.CodeListProxy;
	import org.openforis.collect.metamodel.proxy.CodeListProxy$CodeScope;
	import org.openforis.collect.metamodel.proxy.CodeListProxy$CodeType;
	import org.openforis.collect.metamodel.proxy.CoordinateAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.DateAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.EntityDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.FileAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.LanguageSpecificTextProxy;
	import org.openforis.collect.metamodel.proxy.ModelVersionProxy;
	import org.openforis.collect.metamodel.proxy.NodeLabelProxy;
	import org.openforis.collect.metamodel.proxy.NodeLabelProxy$Type;
	import org.openforis.collect.metamodel.proxy.PrecisionProxy;
	import org.openforis.collect.metamodel.proxy.PromptProxy;
	import org.openforis.collect.metamodel.proxy.PromptProxy$Type;
	import org.openforis.collect.metamodel.proxy.RangeAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.SchemaProxy;
	import org.openforis.collect.metamodel.proxy.SpatialReferenceSystemProxy;
	import org.openforis.collect.metamodel.proxy.SurveyProxy;
	import org.openforis.collect.metamodel.proxy.TaxonAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.TaxonSummariesProxy;
	import org.openforis.collect.metamodel.proxy.TaxonSummaryProxy;
	import org.openforis.collect.metamodel.proxy.TextAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.TextAttributeDefinitionProxy$Type;
	import org.openforis.collect.metamodel.proxy.TimeAttributeDefinitionProxy;
	import org.openforis.collect.metamodel.proxy.UIOptionsProxy;
	import org.openforis.collect.metamodel.proxy.UITabProxy;
	import org.openforis.collect.metamodel.proxy.UITabSetProxy;
	import org.openforis.collect.metamodel.proxy.UnitProxy;
	import org.openforis.collect.model.SurveySummary;
	import org.openforis.collect.model.proxy.AttributeAddChangeProxy;
	import org.openforis.collect.model.proxy.AttributeChangeProxy;
	import org.openforis.collect.model.proxy.AttributeProxy;
	import org.openforis.collect.model.proxy.CodeAttributeProxy;
	import org.openforis.collect.model.proxy.CodeProxy;
	import org.openforis.collect.model.proxy.CoordinateProxy;
	import org.openforis.collect.model.proxy.DateProxy;
	import org.openforis.collect.model.proxy.EntityAddChangeProxy;
	import org.openforis.collect.model.proxy.EntityChangeProxy;
	import org.openforis.collect.model.proxy.EntityProxy;
	import org.openforis.collect.model.proxy.FieldProxy;
	import org.openforis.collect.model.proxy.FileProxy;
	import org.openforis.collect.model.proxy.IntegerRangeProxy;
	import org.openforis.collect.model.proxy.NodeAddChangeProxy;
	import org.openforis.collect.model.proxy.NodeChangeProxy;
	import org.openforis.collect.model.proxy.NodeChangeSetProxy;
	import org.openforis.collect.model.proxy.NodeDeleteChangeProxy;
	import org.openforis.collect.model.proxy.NodeProxy;
	import org.openforis.collect.model.proxy.NodeUpdateRequestProxy;
	import org.openforis.collect.model.proxy.NodeUpdateRequestSetProxy;
	import org.openforis.collect.model.proxy.RealRangeProxy;
	import org.openforis.collect.model.proxy.RecordProxy;
	import org.openforis.collect.model.proxy.SamplingDesignItemProxy;
	import org.openforis.collect.model.proxy.SamplingDesignSummariesProxy;
	import org.openforis.collect.model.proxy.TaxonOccurrenceProxy;
	import org.openforis.collect.model.proxy.TaxonProxy;
	import org.openforis.collect.model.proxy.TaxonVernacularNameProxy;
	import org.openforis.collect.model.proxy.TaxonomyProxy;
	import org.openforis.collect.model.proxy.TimeProxy;
	import org.openforis.collect.model.proxy.ValidationResultProxy;
	import org.openforis.collect.model.proxy.ValidationResultsProxy;
	import org.openforis.collect.remoting.service.codelistimport.proxy.CodeListImportStatusProxy;
	import org.openforis.collect.remoting.service.dataimport.DataImportStateProxy;
	import org.openforis.collect.remoting.service.dataimport.DataImportSummaryItemProxy;
	import org.openforis.collect.remoting.service.dataimport.DataImportSummaryProxy;
	import org.openforis.collect.remoting.service.dataimport.FileUnmarshallingErrorProxy;
	import org.openforis.collect.remoting.service.dataimport.NodeUnmarshallingErrorProxy;
	import org.openforis.collect.remoting.service.samplingdesignimport.proxy.SamplingDesignImportStatusProxy;
	import org.openforis.collect.manager.dataimport.species.proxy.SpeciesImportStatusProxy;
	import org.openforis.idm.metamodel.validation.ValidationResultFlag;

	/**
	 * 
	 * @author M. Togna
	 * @author S. Ricci
	 * 
	 */
	public class ModelClassInitializer {

		public static function init():void {
			var array:Array = [
				AttributeAddChangeProxy,
				AttributeChangeProxy,
				AttributeDefaultProxy,
				AttributeDefinitionProxy,
				AttributeProxy,
				org.openforis.collect.model.proxy.AttributeChangeProxy,
				BasicMap, 
				BooleanAttributeDefinitionProxy,
				CodeAttributeDefinitionProxy,
				CodeAttributeProxy,
				CodeListImportStatusProxy,
				CodeListItemProxy,
				CodeListLabelProxy,
				CodeListLabelProxy$Type,
				CodeListLevelProxy,
				CodeListProxy,
				CodeListProxy$CodeScope,
				CodeListProxy$CodeType,
				CodeProxy,
				CoordinateAttributeDefinitionProxy,
				CoordinateProxy,
				DataExportStatusProxy,
				DataImportStateProxy,
				DataImportSummaryProxy,
				DataImportSummaryItemProxy,
				DateAttributeDefinitionProxy,
				DateProxy,
				EntityAddChangeProxy,
				EntityDefinitionProxy,
				EntityChangeProxy,
				EntityProxy,
				FieldProxy,
				FileAttributeDefinitionProxy,
				FileUnmarshallingErrorProxy,
				FileProxy,
				IntegerRangeProxy,
				LanguageSpecificTextProxy,
				ModelVersionProxy,
				NodeDefinitionSummary,
				NodeDeleteChangeProxy,
				NodeUnmarshallingErrorProxy,
				NodeLabelProxy,
				NodeLabelProxy$Type,
				NodeProxy,
				ProcessStatus,
				ProcessStatus$Step,
				PrecisionProxy,
				PromptProxy,
				PromptProxy$Type,
				RangeAttributeDefinitionProxy,
				RealRangeProxy,
				RecordProxy,
				NodeAddChangeProxy,
				NodeChangeProxy,
				NodeChangeSetProxy,
				NodeUpdateRequestProxy,
				NodeUpdateRequestSetProxy,
				NodeChangeSetProxy,
				SamplingDesignImportStatusProxy,
				SamplingDesignItemProxy,
				SamplingDesignSummariesProxy,
				SchemaProxy,
				SpatialReferenceSystemProxy,
				SpeciesImportStatusProxy,
				SurveyProxy,
				SurveySummary,
				TaxonAttributeDefinitionProxy,
				TaxonOccurrenceProxy,
				TaxonomyProxy,
				TaxonProxy,
				TaxonSummariesProxy,
				TaxonSummaryProxy,
				TaxonVernacularNameProxy,
				TextAttributeDefinitionProxy,
				TextAttributeDefinitionProxy$Type,
				TimeAttributeDefinitionProxy,
				TimeProxy,
				UIOptionsProxy,
				UITabSetProxy,
				UITabProxy,
				UnitProxy,
				ValidationResultsProxy,
				ValidationResultProxy,
				ValidationResultFlag
			];
		}
		
	}
}