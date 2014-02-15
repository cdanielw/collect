/**
 * 
 */
package org.openforis.collect.designer.viewmodel;

import java.util.HashMap;
import java.util.Map;

import org.openforis.collect.designer.component.SchemaTreeModel;
import org.openforis.collect.designer.component.SchemaTreeModel.Predicate;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.ModelVersion;
import org.openforis.idm.metamodel.SurveyObject;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

/**
 * @author S. Ricci
 *
 */
public class SchemaTreePopUpVM extends SurveyBaseVM {

	public static final String SCHEMA_TREE_NODE_SELECTED_COMMAND = "schemaTreeNodeSelected";
	
	private SchemaTreeModel treeModel;
	private SurveyObject selectedNode;

	private Predicate<SurveyObject> selectPredicate;
	
	@Init(superclass=false)
	public void init(@ExecutionArgParam("rootEntity") EntityDefinition rootEntity, 
			@ExecutionArgParam("version") ModelVersion version,
			@ExecutionArgParam("selection") SurveyObject selection,
			@ExecutionArgParam("includePredicate") SchemaTreeModel.Predicate<SurveyObject> includePredicate,
			@ExecutionArgParam("selectPredicate") SchemaTreeModel.Predicate<SurveyObject> selectPredicate) {
		super.init();
		this.treeModel = SchemaTreeModel.createInstance(rootEntity, version, includePredicate, false, currentLanguageCode);
		this.treeModel.openAllItems();
		this.selectPredicate = selectPredicate; 
		if ( selection != null ) {
			this.selectedNode = selection;
			this.treeModel.select(selection);
			this.treeModel.showSelectedNode();
		}
	}
	
	@Command
	public void onOk(@BindingParam("selectedSurveyObject") SurveyObject selectedSurveyObject) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("node", selectedSurveyObject);
		BindUtils.postGlobalCommand(null, null, SCHEMA_TREE_NODE_SELECTED_COMMAND, args);
	}
	
	@Command
	public void nodeSelected(@BindingParam("node") SurveyObject surveyObject) {
		if ( selectPredicate != null && ! selectPredicate.evaluate(surveyObject) ) {
			treeModel.select(selectedNode);
		} else if ( selectedNode == surveyObject ) {
			treeModel.clearSelection();
			selectedNode = null;
		} else {
			selectedNode = surveyObject;
		}
	}
	
	public SchemaTreeModel getTreeModel() {
		return treeModel;
	}
	
}