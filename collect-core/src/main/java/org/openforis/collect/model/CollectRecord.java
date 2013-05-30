package org.openforis.collect.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openforis.collect.metamodel.ui.UIOptions;
import org.openforis.collect.metamodel.ui.UIOptions.Layout;
import org.openforis.idm.metamodel.AttributeDefault;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.ModelVersion;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.NumericAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.SurveyContext;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.metamodel.validation.ValidationResultFlag;
import org.openforis.idm.metamodel.validation.ValidationResults;
import org.openforis.idm.model.Attribute;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Field;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.NodePointer;
import org.openforis.idm.model.NodeVisitor;
import org.openforis.idm.model.NumberAttribute;
import org.openforis.idm.model.Record;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.Value;
import org.openforis.idm.model.expression.ExpressionFactory;
import org.openforis.idm.model.expression.InvalidExpressionException;
import org.openforis.idm.model.expression.ModelPathExpression;

/**
 * 
 * @author G. Miceli
 * @author M. Togna
 * @author S. Ricci
 * 
 */
public class CollectRecord extends Record {

	private static final int APPROVED_MISSING_POSITION = 0;
	private static final int CONFIRMED_ERROR_POSITION = 0;
	private static final int DEFAULT_APPLIED_POSITION = 1;
	
	public enum Step {
		ENTRY(1), CLEANSING(2), ANALYSIS(3);

		private int stepNumber;

		private Step(int stepNumber) {
			this.stepNumber = stepNumber;
		}

		public int getStepNumber() {
			return stepNumber;
		}

		public static Step valueOf(int stepNumber) {
			Step[] values = Step.values();
			for (Step step : values) {
				if (step.getStepNumber() == stepNumber) {
					return step;
				}
			}
			return null;
		}
		
		public Step getNext() {
			switch(this) {
				case ENTRY:
					return CLEANSING;
				case CLEANSING:
					return ANALYSIS;
				default:
					throw new IllegalArgumentException("This record cannot be promoted.");
			}
		}
		
		public Step getPrevious() {
			switch(this) {
				case CLEANSING:
					return Step.ENTRY;
				case ANALYSIS:
					return Step.CLEANSING;
				default:
					throw new IllegalArgumentException("This record cannot be promoted.");
			}
		}
		
	}

	public enum State {
		REJECTED("R");
		
		private String code;

		private State(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}
		
		public static State fromCode(String code) {
			State[] values = State.values();
			for (State state : values) {
				if (state.getCode().equals(code)) {
					return state;
				}
			}
			return null;
		}
	}
	
	private transient Step step;
	private transient State state;

	private transient Date creationDate;
	private transient User createdBy;
	private transient Date modifiedDate;
	private transient User modifiedBy;
	private transient Integer missing;
	private transient Integer missingErrors;
	private transient Integer missingWarnings;
	private transient Integer skipped;
	private transient Integer errors;
	private transient Integer warnings;
	
	private List<String> rootEntityKeyValues;
	private List<Integer> entityCounts;
	
	private RecordValidationCache validationCache;

	public CollectRecord(CollectSurvey survey, String versionName) {
		super(survey, versionName);
		this.step = Step.ENTRY;
		this.validationCache = new RecordValidationCache(this);
		// use List to preserve the order of the keys and counts
		this.rootEntityKeyValues = new ArrayList<String>();
		this.entityCounts = new ArrayList<Integer>();
		initErrorCountInfo();
	}

	protected void initErrorCountInfo() {
		skipped = null;
		missing = null;
		missingErrors = null;
		missingWarnings = null;
		errors = null;
		warnings = null;
	}

	public boolean isErrorConfirmed(Attribute<?,?> attribute){
		int fieldCount = attribute.getFieldCount();		
		for( int i=0; i <fieldCount; i++ ){
			Field<?> field = attribute.getField(i);
			if( !field.getState().get(CONFIRMED_ERROR_POSITION) ){
				return false;
			}
		}
		return true;
	}
	
	public boolean isMissingApproved(Entity parentEntity, String childName){
		org.openforis.idm.model.State childState = parentEntity.getChildState(childName);
		return childState.get(APPROVED_MISSING_POSITION);
	} 
	
	public boolean isDefaultValueApplied(Attribute<?, ?> attribute) {
		int fieldCount = attribute.getFieldCount();		
		for( int i=0; i <fieldCount; i++ ){
			Field<?> field = attribute.getField(i);
			if( !field.getState().get(DEFAULT_APPLIED_POSITION) ){
				return false;
			}
		}
		return true;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public User getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getSkipped() {
		if ( skipped == null ) {
			skipped = validationCache.getSkippedNodeIds().size();
		}
		return skipped;
	}

	public void setSkipped(Integer skipped) {
		this.skipped = skipped;
	}

	public Integer getMissing() {
		if (missing == null) {
			missing = getMissingErrors();
		}
		return missing;
	}
	
	public Integer getMissingErrors() {
		if ( missingErrors == null ) {
			missingErrors = validationCache.getTotalMissingMinCountErrors();
		}
		return missingErrors;
	}
	
	public Integer getMissingWarnings() {
		if ( missingWarnings == null ) {
			missingWarnings = validationCache.getTotalMissingMinCountWarnings();
		}
		return missingWarnings;
	}

	public void setMissing(Integer missing) {
		this.missing = missing;
	}

	public Integer getErrors() {
		if(errors == null) {
			errors = validationCache.getTotalAttributeErrors();
			errors += validationCache.getTotalMaxCountErrors();
		}
		return errors;
	}

	public void setErrors(Integer errors) {
		this.errors = errors;
	}

	public Integer getWarnings() {
		if(warnings == null) {
			warnings = validationCache.getTotalAttributeWarnings();
			warnings += validationCache.getTotalMinCountWarnings();
			warnings += validationCache.getTotalMaxCountWarnings();
		}
		return warnings;
	}

	public void setWarnings(Integer warnings) {
		this.warnings = warnings;
	}

	public List<String> getRootEntityKeyValues() {
		return rootEntityKeyValues;
	}

	public RecordValidationCache getValidationCache() {
		return validationCache;
	}
	
	public void updateRootEntityKeyValues(){
		Entity rootEntity = getRootEntity();
		if(rootEntity != null) {
			rootEntityKeyValues = new ArrayList<String>();
			EntityDefinition rootEntityDefn = rootEntity.getDefinition();
			List<AttributeDefinition> keyDefns = rootEntityDefn.getKeyAttributeDefinitions();
			for (AttributeDefinition keyDefn : keyDefns) {
				String keyValue = null;
				Node<?> keyNode = rootEntity.get(keyDefn.getName(), 0);
				if(keyNode instanceof CodeAttribute) {
					Code code = ((CodeAttribute) keyNode).getValue();
					if(code != null) {
						keyValue = code.getCode();
					}
				} else if(keyNode instanceof TextAttribute) {
					keyValue = ((TextAttribute) keyNode).getText();
				} else if(keyNode instanceof NumberAttribute<?,?>) {
					Number obj = ((NumberAttribute<?,?>) keyNode).getNumber();
					if(obj != null) {
						keyValue = obj.toString();
					}
				}
				if(StringUtils.isNotEmpty(keyValue)){
					rootEntityKeyValues.add(keyValue);
				} else {
					//todo throw error in this case?
					rootEntityKeyValues.add(null);
				}
			}
		}
	}

	public void updateEntityCounts() {
		Entity rootEntity = getRootEntity();
		List<EntityDefinition> countableDefns = getCountableEntitiesInList();
		
		//set counts
		List<Integer> counts = new ArrayList<Integer>();
		for (EntityDefinition defn : countableDefns) {
			String name = defn.getName();
			int count = rootEntity.getCount(name);
			counts.add(count);
		}
		entityCounts = counts;
	}
	
	/**
	 * Returns first level entity definitions that have the attribute countInSummaryList set to true
	 * 
	 * @param rootEntityDefinition
	 * @return 
	 */
	protected List<EntityDefinition> getCountableEntitiesInList() {
		List<EntityDefinition> result = new ArrayList<EntityDefinition>();
		EntityDefinition rootEntityDefinition = getRootEntity().getDefinition();
		List<NodeDefinition> childDefinitions = rootEntityDefinition .getChildDefinitions();
		for (NodeDefinition childDefinition : childDefinitions) {
			if(childDefinition instanceof EntityDefinition) {
				EntityDefinition entityDefinition = (EntityDefinition) childDefinition;
				String annotation = childDefinition.getAnnotation(UIOptions.Annotation.COUNT_IN_SUMMARY_LIST.getQName());
				if(annotation != null && Boolean.parseBoolean(annotation)) {
					result.add(entityDefinition);
				}
			}
		}
		return result;
	}
	
	public void setRootEntityKeyValues(List<String> keys) {
		this.rootEntityKeyValues = keys;
	}

	public List<Integer> getEntityCounts() {
		return entityCounts;
	}

	public void setEntityCounts(List<Integer> counts) {
		this.entityCounts = counts;
	}

	/**
	 * Clear all node states
	 */
	public void clearNodeStates() {
		Entity rootEntity = getRootEntity();
		rootEntity.traverse( new NodeVisitor() {
			@Override
			public void visit(Node<? extends NodeDefinition> node, int idx) {
				if ( node instanceof Attribute ) {
					Attribute<?,?> attribute = (Attribute<?, ?>) node;
//					if ( step == Step.ENTRY ) {
//						attribute.clearFieldSymbols();
//					}
					attribute.clearFieldStates();
					attribute.clearValidationResults();
				} else if( node instanceof Entity ) {
					Entity entity = (Entity) node;
					entity.clearChildStates();
					
					EntityDefinition definition = entity.getDefinition();
					List<NodeDefinition> childDefinitions = definition.getChildDefinitions();
					for (NodeDefinition childDefinition : childDefinitions) {
						String childName = childDefinition.getName();
						entity.clearRelevanceState(childName);
						entity.clearRequiredState(childName);
					}
				}
			} 
		} );
	}
	
	/**
	 * Updates all derived states of all nodes
	 * @return 
	 */
	public void updateDerivedStates() {
		initErrorCountInfo();
		Entity rootEntity = getRootEntity();
		rootEntity.traverse(new NodeVisitor() {
			@Override
			public void visit(Node<? extends NodeDefinition> node, int idx) {
				if ( node instanceof Attribute ) {
					Attribute<?,?> attribute = (Attribute<?, ?>) node;
					attribute.validateValue();
				} else if ( node instanceof Entity ) {
					Entity entity = (Entity) node;
					ModelVersion version = getVersion();
					EntityDefinition definition = entity.getDefinition();
					List<NodeDefinition> childDefinitions = definition.getChildDefinitions();
					for (NodeDefinition childDefinition : childDefinitions) {
						if ( version == null || version.isApplicable(childDefinition) ) {
							String childName = childDefinition.getName();
							entity.validateMaxCount(childName);
							entity.validateMinCount(childName);
						}
					}
				}
			}
		});
	}
	
	/**
	 * Deletes a node from the record.
	 * 
	 * @param node
	 * @return
	 */
	public NodeChangeSet deleteNode(
			Node<?> node) {
		Set<NodePointer> relevantDependencies = new HashSet<NodePointer>();
		Set<NodePointer> requiredDependencies = new HashSet<NodePointer>();
		HashSet<Attribute<?, ?>> checkDependencies = new HashSet<Attribute<?,?>>();
		NodeChangeMap changeMap = new NodeChangeMap();
		changeMap.prepareDeleteNodeChange(node);
		List<NodePointer> cardinalityNodePointers = createCardinalityNodePointers(node);
		Stack<Node<?>> depthFirstDescendants = getDepthFirstDescendants(node);
		while ( !depthFirstDescendants.isEmpty() ) {
			Node<?> n = depthFirstDescendants.pop();
			relevantDependencies.addAll(n.getRelevantDependencies());
			requiredDependencies.addAll(n.getRequiredDependencies());
			if ( n instanceof Attribute ) {
				checkDependencies.addAll(((Attribute<?, ?>) n).getCheckDependencies());
			}
			performNodeDeletion(n);
		}
		//clear dependencies
		clearRelevantDependencies(relevantDependencies);
		HashSet<NodePointer> relevanceRequiredDependencies = new HashSet<NodePointer>();
		relevanceRequiredDependencies.addAll(relevantDependencies);
		relevanceRequiredDependencies.addAll(requiredDependencies);
		clearRequiredDependencies(relevanceRequiredDependencies);
		clearValidationResults(checkDependencies);
		
		prepareChange(changeMap, relevanceRequiredDependencies, checkDependencies, cardinalityNodePointers);
		return new NodeChangeSet(changeMap.getChanges());
	}
	
	protected Node<?> performNodeDeletion(Node<?> node) {
		if(node.isDetached()) {
			throw new IllegalArgumentException("Unable to delete a node already detached");
		}
		Entity parentEntity = node.getParent();
		int index = node.getIndex();
		Node<?> deletedNode = parentEntity.remove(node.getName(), index);
		removeValidationCache(deletedNode.getInternalId());
		return deletedNode;
	}
	
	protected Stack<Node<?>> getDepthFirstDescendants(Node<?> node) {
		Stack<Node<?>> result = new Stack<Node<?>>();
		Stack<Node<?>> stack = new Stack<Node<?>>();
		stack.push(node);
		while(!stack.isEmpty()){
			Node<?> n = stack.pop();
			result.push(n);
			if(n instanceof Entity){
				Entity entity = (Entity) n;
				List<Node<? extends NodeDefinition>> children = entity.getChildren();
				for (Node<? extends NodeDefinition> child : children) {
					stack.push(child);
				}
			}
		}
		return result;
	}

	/**
	 * Applies the default value to an attribute, if any.
	 * The applied default value will be the first one having verified the "condition".
	 *  
	 * @param attribute
	 * @return
	 */
	public NodeChangeSet applyDefaultValue(
			Attribute<?, ?> attribute) {
		performDefaultValueApply(attribute);
		NodeChangeMap changeMap = new NodeChangeMap();
		AttributeChange change = changeMap.prepareAttributeChange(attribute);
		Map<Integer, Object> updatedFieldValues = createFieldValuesMap(attribute);
		change.setUpdatedFieldValues(updatedFieldValues);
		
		List<NodePointer> cardinalityNodePointers = createCardinalityNodePointers(attribute);
		Set<NodePointer> relevanceRequiredDependencies = clearRelevanceRequiredDependencies(attribute);
		Set<Attribute<?, ?>> checkDependencies = clearValidationResults(attribute);
		relevanceRequiredDependencies.add(new NodePointer(attribute.getParent(), attribute.getName()));
		checkDependencies.add(attribute);
		prepareChange(changeMap, relevanceRequiredDependencies, checkDependencies, cardinalityNodePointers);
		return new NodeChangeSet(changeMap.getChanges());
	}

	/**
	 * Applies the first default value (if any) that is applicable to the attribute.
	 * The condition of the corresponding DefaultValue will be verified.
	 *  
	 * @param attribute
	 */
	protected <V extends Value> void performDefaultValueApply(Attribute<?, V> attribute) {
		AttributeDefinition attributeDefn = (AttributeDefinition) attribute.getDefinition();
		List<AttributeDefault> defaults = attributeDefn.getAttributeDefaults();
		if ( defaults != null && defaults.size() > 0 ) {
			for (AttributeDefault attributeDefault : defaults) {
				try {
					V value = attributeDefault.evaluate(attribute);
					if ( value != null ) {
						attribute.setValue(value);
						setDefaultValueApplied(attribute, true);
						clearRelevanceRequiredDependencies(attribute);
						clearValidationResults(attribute);
						break;
					}
				} catch (InvalidExpressionException e) {
					throw new RuntimeException("Error applying default value for attribute " + attributeDefn.getPath());
				}
			}
		}
	}
	
	/**
	 * Updates an attribute with a new value
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public <V extends Value> NodeChangeSet updateAttribute(
			Attribute<? extends NodeDefinition, V> attribute,
			V value) {
		beforeAttributeUpdate(attribute);
		attribute.setValue(value);
		return afterAttributeUpdate(attribute);
	}
	
	/**
	 * Updates an attribute and sets the specified FieldSymbol on every field
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public NodeChangeSet updateAttribute(
			Attribute<? extends NodeDefinition, ?> attribute,
			FieldSymbol symbol) {
		beforeAttributeUpdate(attribute);
		setSymbolOnFields(attribute, symbol);
		return afterAttributeUpdate(attribute);
	}
	
	protected <V extends Value> void beforeAttributeUpdate(
			Attribute<? extends NodeDefinition, V> attribute) {
		Entity parentEntity = attribute.getParent();
		setErrorConfirmed(attribute, false);
		performMissingValueApproval(parentEntity, attribute.getName(), false);
		setDefaultValueApplied(attribute, false);
	}

	protected <V extends Value> NodeChangeSet afterAttributeUpdate(
			Attribute<? extends NodeDefinition, V> attribute) {
		NodeChangeMap changeMap = new NodeChangeMap();
		AttributeChange change = changeMap.prepareAttributeChange(attribute);
		Map<Integer, Object> updatedFieldValues = createFieldValuesMap(attribute);
		change.setUpdatedFieldValues(updatedFieldValues);
		return afterAttributeInsertOrUpdate(changeMap, attribute);
	}

	protected <V extends Value> NodeChangeSet afterAttributeInsertOrUpdate(
			NodeChangeMap changeMap,
			Attribute<? extends NodeDefinition, V> attribute) {
		Set<NodePointer> relevanceRequiredDependencies = clearRelevanceRequiredDependencies(attribute);
		relevanceRequiredDependencies.add(new NodePointer(attribute.getParent(), attribute.getName()));
		Set<Attribute<?, ?>> checkDependencies = clearValidationResults(attribute);
		checkDependencies.add(attribute);
		List<NodePointer> cardinalityDependencies = createCardinalityNodePointers(attribute);
		prepareChange(changeMap, relevanceRequiredDependencies, checkDependencies, cardinalityDependencies);
		return new NodeChangeSet(changeMap.getChanges());
	}

	/**
	 * Updates a field with a new value or symbol.
	 * The value will be parsed according to field data type.
	 * 
	 * @param field
	 * @param rawValue 
	 * @param symbol
	 * @param remarks
	 * @return
	 */
	public NodeChangeSet updateField(
			Field<?> field, String rawValue, FieldSymbol symbol, String remarks) {
		Attribute<?, ?> attribute = field.getAttribute();
		Entity parentEntity = attribute.getParent();

		setErrorConfirmed(attribute, false);
		performMissingValueApproval(parentEntity, attribute.getName(), false);
		setDefaultValueApplied(attribute, false);
		int fieldIndex = field.getIndex();
		Object value = parseFieldValue(parentEntity, attribute.getDefinition(), rawValue, fieldIndex);
		setFieldValue(field, value, remarks, symbol);
		
		NodeChangeMap changeMap = new NodeChangeMap();
		AttributeChange change = changeMap.prepareAttributeChange(attribute);

		Map<Integer, Object> updatedFieldValues = createFieldValuesMap(attribute);
		change.setUpdatedFieldValues(updatedFieldValues);
		
		Set<NodePointer> relevanceRequiredDependencies = clearRelevanceRequiredDependencies(attribute);
		Set<Attribute<?, ?>> checkDependencies = clearValidationResults(attribute);
		relevanceRequiredDependencies.add(new NodePointer(attribute.getParent(), attribute.getName()));
		checkDependencies.add(attribute);
		List<NodePointer> cardinalityDependencies = createCardinalityNodePointers(attribute);
		prepareChange(changeMap, relevanceRequiredDependencies, checkDependencies, cardinalityDependencies);
		return new NodeChangeSet(changeMap.getChanges());
	}
	
	/**
	 * Adds a new entity to a the record.
	 * 
	 * @param parentEntity
	 * @param nodeName
	 * @return Changes applied to the record 
	 */
	public NodeChangeSet addEntity(
			Entity parentEntity, String nodeName) {
		Entity createdNode = performEntityAdd(parentEntity, nodeName);
		
		performMissingValueApproval(parentEntity, nodeName, false);
		
		NodeChangeMap changeMap = new NodeChangeMap();
		changeMap.prepareAddEntityChange(createdNode);
		
		Set<NodePointer> relevanceRequiredDependencies = clearRelevanceRequiredDependencies(createdNode);
		Set<Attribute<?, ?>> checkDependencies = null;
		relevanceRequiredDependencies.add(new NodePointer(createdNode.getParent(), nodeName));
		List<NodePointer> cardinalityDependencies = createCardinalityNodePointers(createdNode);
		prepareChange(changeMap, relevanceRequiredDependencies, checkDependencies, cardinalityDependencies);
		return new NodeChangeSet(changeMap.getChanges());
	}

	/**
	 * Adds a new attribute to a record.
	 * This attribute can be immediately populated with a value or with a FieldSymbol, and remarks.
	 * You cannot specify both value and symbol.
	 * 
	 * @param parentEntity Parent entity of the attribute
	 * @param nodeName Node name of the attribute
	 * @param value Value to set on the attribute
	 * @param symbol FieldSymbol to set on each field of the attribute
	 * @param remarks Remarks to set on each field of the attribute
	 * @return Changes applied to the record 
	 */
	public NodeChangeSet addAttribute(
			Entity parentEntity, String nodeName, Value value, FieldSymbol symbol, 
			String remarks) {
		Attribute<?, ?> attribute = performAttributeAdd(parentEntity, nodeName, value, symbol, remarks);
		
		performMissingValueApproval(parentEntity, nodeName, false);
		
		NodeChangeMap changeMap = new NodeChangeMap();
		changeMap.prepareAddAttributeChange(attribute);
		
		return afterAttributeInsertOrUpdate(changeMap, attribute);
	}

	/**
	 * Updates the remarks of a Field
	 * 
	 * @param field
	 * @param remarks
	 * @return
	 */
	public NodeChangeSet updateRemarks(
			Field<?> field, String remarks) {
		field.setRemarks(remarks);
		NodeChangeMap changeMap = new NodeChangeMap();
		Attribute<?, ?> attribute = field.getAttribute();
		changeMap.prepareAttributeChange(attribute);
		return new NodeChangeSet(changeMap.getChanges());
	}

	public NodeChangeSet approveMissingValue(
			Entity parentEntity, String nodeName) {
		return approveMissingValue(parentEntity, nodeName, true);
	}

	public NodeChangeSet approveMissingValue(
			Entity parentEntity, String nodeName, boolean approved) {
		performMissingValueApproval(parentEntity, nodeName, approved);
		List<NodePointer> cardinalityNodePointers = createCardinalityNodePointers(parentEntity);
		cardinalityNodePointers.add(new NodePointer(parentEntity, nodeName));
		NodeChangeMap changeMap = new NodeChangeMap();
		validateAll(changeMap, cardinalityNodePointers, false);
		return new NodeChangeSet(changeMap.getChanges());
	}

	public NodeChangeSet confirmError(Attribute<?, ?> attribute) {
		return confirmError(attribute, true);
	}
	
	public NodeChangeSet confirmError(Attribute<?, ?> attribute, boolean confirmed) {
		Set<Attribute<?,?>> checkDependencies = new HashSet<Attribute<?,?>>();
		setErrorConfirmed(attribute, confirmed);
		attribute.clearValidationResults();
		checkDependencies.add(attribute);
		
		NodeChangeMap changeMap = new NodeChangeMap();
		changeMap.prepareAttributeChange(attribute);
		validateChecks(changeMap, checkDependencies);
		return new NodeChangeSet(changeMap.getChanges());
	}
	
	protected List<NodePointer> createCardinalityNodePointers(Node<?> node){
		List<NodePointer> nodePointers = new ArrayList<NodePointer>();
		
		Entity parent = node.getParent();
		String childName = node.getName();
		while(parent != null){
			NodePointer nodePointer = new NodePointer(parent, childName );
			nodePointers.add(nodePointer);
			
			childName = parent.getName();
			parent = parent.getParent();
		}
		return nodePointers;
	}

	protected void prepareChange(NodeChangeMap changeMap, Set<NodePointer> relevanceRequiredDependencies, 
			Collection<Attribute<?, ?>> checkDependencies, Collection<NodePointer> cardinalityDependencies) {
		validateAll(changeMap, cardinalityDependencies, false);
		validateAll(changeMap, relevanceRequiredDependencies, true);
		validateChecks(changeMap, checkDependencies);
	}

	protected void validateChecks(NodeChangeMap changeMap,
			Collection<Attribute<?, ?>> attributes) {
		if (attributes != null) {
			for (Attribute<?, ?> attr : attributes) {
				validateAttribute(changeMap, attr);
			}
		}
	}

	protected void validateAttribute(NodeChangeMap changeMap,
			Attribute<?, ?> attr) {
		if ( !attr.isDetached() ) {
			attr.clearValidationResults();
			ValidationResults results = attr.validateValue();
			AttributeChange change = changeMap.prepareAttributeChange(attr);
			change.setValidationResults(results);
		}
	}

	protected void validateChecks(NodeChangeMap changeMap,
			Entity entity, String childName) {
		List<Node<?>> children = entity.getAll(childName);
		for ( Node<?> node : children ) {
			if ( node instanceof Attribute ){
				validateAttribute(changeMap, (Attribute<?, ?>) node);
			}
		}
	}
	
	protected void validateAll(NodeChangeMap changeMap,
			Collection<NodePointer> nodePointers, boolean validateChecks) {
		if (nodePointers != null) {
			for (NodePointer nodePointer : nodePointers) {
				Entity parent = nodePointer.getEntity();
				if ( parent != null && ! parent.isDetached()) {
					validateCardinality(changeMap, nodePointer);
					validateRelevanceState(changeMap, nodePointer);
					validateRequirenessState(changeMap, nodePointer);
					if ( validateChecks ) {
						validateChecks(changeMap, parent, nodePointer.getChildName());
					}
				}
			}
		}
	}

	protected void validateCardinality(NodeChangeMap changeMap, NodePointer nodePointer) {
		Entity entity = nodePointer.getEntity();
		String childName = nodePointer.getChildName();
		EntityChange change = changeMap.prepareEntityChange(entity);
		change.setChildrenMinCountValidation(childName, entity.validateMinCount(childName));
		change.setChildrenMaxCountValidation(childName, entity.validateMaxCount(childName));
	}

	protected void validateRelevanceState(
			NodeChangeMap changeMap, NodePointer nodePointer) {
		Entity entity = nodePointer.getEntity();
		String childName = nodePointer.getChildName();
		EntityChange change = changeMap.prepareEntityChange(entity);
		change.setChildrenRelevance(childName, entity.isRelevant(childName));
	}

	protected void validateRequirenessState(
			NodeChangeMap changeMap, NodePointer nodePointer) {
		Entity entity = nodePointer.getEntity();
		String childName = nodePointer.getChildName();
		EntityChange change = changeMap.prepareEntityChange(entity);
		change.setChildrenRequireness(childName, entity.isRequired(childName));
	}

	protected Attribute<?, ?> performAttributeAdd(Entity parentEntity, String nodeName, Value value, FieldSymbol symbol, String remarks) {
		EntityDefinition parentEntityDefn = parentEntity.getDefinition();
		AttributeDefinition def = (AttributeDefinition) parentEntityDefn.getChildDefinition(nodeName);
		@SuppressWarnings("unchecked")
		Attribute<?, Value> attribute = (Attribute<?, Value>) def.createNode();
		parentEntity.add(attribute);
		if ( value != null ) {
			attribute.setValue(value);
		}
		if ( symbol != null ) {
			setSymbolOnFields(attribute, symbol);
		}
		if ( remarks != null ) {
			setRemarksOnFields(attribute, remarks);
		}
		return attribute;
	}
	
	protected Object parseFieldValue(Entity parentEntity, AttributeDefinition def, String value, Integer fieldIndex) {
		Object fieldValue = null;
		if(StringUtils.isBlank(value)) {
			return null;
		}
		if(def instanceof BooleanAttributeDefinition) {
			fieldValue = Boolean.parseBoolean(value);
		} else if(def instanceof CoordinateAttributeDefinition) {
			if(fieldIndex != null) {
				if(fieldIndex == 2) {
					fieldValue = value;
				} else {
					fieldValue = Double.valueOf(value);
				}
			}
		} else if(def instanceof DateAttributeDefinition) {
			Integer val = Integer.valueOf(value);
			fieldValue = val;
		} else if(def instanceof NumberAttributeDefinition) {
			NumericAttributeDefinition numberDef = (NumericAttributeDefinition) def;
			if(fieldIndex != null && fieldIndex == 2) {
				//unit id
				fieldValue = Integer.parseInt(value);
			} else {
				NumericAttributeDefinition.Type type = numberDef.getType();
				Number number = null;
				switch(type) {
					case INTEGER:
						number = Integer.valueOf(value);
						break;
					case REAL:
						number = Double.valueOf(value);
						break;
				}
				if(number != null) {
					fieldValue = number;
				}
			}
		} else if(def instanceof RangeAttributeDefinition) {
			if(fieldIndex != null && fieldIndex == 3) {
				//unit id
				fieldValue = Integer.parseInt(value);
			} else {
				RangeAttributeDefinition.Type type = ((RangeAttributeDefinition) def).getType();
				Number number = null;
				switch(type) {
					case INTEGER:
						number = Integer.valueOf(value);
						break;
					case REAL:
						number = Double.valueOf(value);
						break;
				}
				if(number != null) {
					fieldValue = number;
				}
			}
		} else if(def instanceof TimeAttributeDefinition) {
			fieldValue = Integer.valueOf(value);
		} else {
			fieldValue = value;
		}
		return fieldValue;
	}
	
	
	public Entity performEntityAdd(Entity parentEntity, String nodeName) {
		Entity entity = EntityBuilder.addEntity(parentEntity, nodeName);
		addEmptyNodes(entity);
		return entity;
	}

	public Entity performEntityAdd(Entity parentEntity, String nodeName, int idx) {
		Entity entity = EntityBuilder.addEntity(parentEntity, nodeName, idx);
		addEmptyNodes(entity);
		return entity;
	}
	
	public void addEmptyNodes(Entity entity) {
		ModelVersion version = getVersion();
		addEmptyEnumeratedEntities(entity);
		EntityDefinition entityDefn = entity.getDefinition();
		List<NodeDefinition> childDefinitions = entityDefn.getChildDefinitions();
		for (NodeDefinition childDefn : childDefinitions) {
			if(version == null || version.isApplicable(childDefn)) {
				String childName = childDefn.getName();
				if(entity.getCount(childName) == 0) {
					int toBeInserted = entity.getEffectiveMinCount(childName);
					if ( toBeInserted <= 0 && childDefn instanceof AttributeDefinition || ! childDefn.isMultiple() ) {
						//insert at least one node
						toBeInserted = 1;
					}
					addEmptyChildren(entity, childDefn, toBeInserted);
				} else {
					List<Node<?>> children = entity.getAll(childName);
					for (Node<?> child : children) {
						if(child instanceof Entity) {
							addEmptyNodes((Entity) child);
						}
					}
				}
			}
		}
	}

	protected void addEmptyEnumeratedEntities(Entity parentEntity) {
		CollectSurvey survey = (CollectSurvey) parentEntity.getSurvey();
		UIOptions uiOptions = survey.getUIOptions();
		ModelVersion version = getVersion();
		EntityDefinition parentEntityDefn = parentEntity.getDefinition();
		List<NodeDefinition> childDefinitions = parentEntityDefn.getChildDefinitions();
		for (NodeDefinition childDefn : childDefinitions) {
			if ( childDefn instanceof EntityDefinition && (version == null || version.isApplicable(childDefn)) ) {
				EntityDefinition childEntityDefn = (EntityDefinition) childDefn;
				boolean tableLayout = uiOptions == null || uiOptions.getLayout(childEntityDefn) == Layout.TABLE;
				if(childEntityDefn.isMultiple() && childEntityDefn.isEnumerable() && tableLayout) {
					addEmptyEnumeratedEntities(parentEntity, childEntityDefn);
				}
			}
		}
	}

	protected void addEmptyEnumeratedEntities(Entity parentEntity, EntityDefinition enumerableEntityDefn) {
		ModelVersion version = getVersion();
		CodeAttributeDefinition enumeratingCodeDefn = enumerableEntityDefn.getEnumeratingKeyCodeAttribute(version);
		if(enumeratingCodeDefn != null) {
			String enumeratedEntityName = enumerableEntityDefn.getName();
			CodeList list = enumeratingCodeDefn.getList();
			List<CodeListItem> items = list.getItems();
			for (int i = 0; i < items.size(); i++) {
				CodeListItem item = items.get(i);
				if(version == null || version.isApplicable(item)) {
					String code = item.getCode();
					Entity enumeratedEntity = getEnumeratedEntity(parentEntity, enumerableEntityDefn, enumeratingCodeDefn, code);
					if( enumeratedEntity == null ) {
						Entity addedEntity = performEntityAdd(parentEntity, enumeratedEntityName, i);
						//set the value of the key CodeAttribute
						CodeAttribute addedCode = (CodeAttribute) addedEntity.get(enumeratingCodeDefn.getName(), 0);
						addedCode.setValue(new Code(code));
					} else {
						parentEntity.move(enumeratedEntityName, enumeratedEntity.getIndex(), i);
					}
				}
			}
		}
	}

	protected Entity getEnumeratedEntity(Entity parentEntity, EntityDefinition childEntityDefn, 
			CodeAttributeDefinition enumeratingCodeAttributeDef, String value) {
		List<Node<?>> children = parentEntity.getAll(childEntityDefn.getName());
		for (Node<?> child : children) {
			Entity entity = (Entity) child;
			Code code = getCodeAttributeValue(entity, enumeratingCodeAttributeDef);
			if(code != null && value.equals(code.getCode())) {
				return entity;
			}
		}
		return null;
	}
	
	private Code getCodeAttributeValue(Entity entity, CodeAttributeDefinition def) {
		Node<?> node = entity.get(def.getName(), 0);
		if(node != null) {
			return ((CodeAttribute)node).getValue();
		} else {
			return null;
		}
	}
	
	protected int addEmptyChildren(Entity entity, NodeDefinition childDefn, int toBeInserted) {
		String childName = childDefn.getName();
		CollectSurvey survey = (CollectSurvey) entity.getSurvey();
		UIOptions uiOptions = survey.getUIOptions();
		int count = 0;
		boolean multipleEntityFormLayout = childDefn instanceof EntityDefinition && childDefn.isMultiple() && 
				uiOptions != null && uiOptions.getLayout((EntityDefinition) childDefn) == Layout.FORM;
		if ( ! multipleEntityFormLayout ) {
			while(count < toBeInserted) {
				if(childDefn instanceof AttributeDefinition) {
					Node<?> createNode = childDefn.createNode();
					entity.add(createNode);
				} else if(childDefn instanceof EntityDefinition ) {
					performEntityAdd(entity, childName);
				}
				count ++;
			}
		}
		return count;
	}
	
	/**
	 * Applies default values on each descendant attribute of a record in which empty nodes have already been added.
	 * Default values are applied only to "empty" attributes.
	 * 
	 * @throws InvalidExpressionException 
	 */
	public void applyDefaultValues() {
		Entity rootEntity = getRootEntity();
		applyDefaultValues(rootEntity);
	}

	/**
	 * Applies default values on each descendant attribute of an Entity in which empty nodes have already been added.
	 * Default values are applied only to "empty" attributes.
	 * 
	 * @param entity
	 * @throws InvalidExpressionException 
	 */
	protected void applyDefaultValues(Entity entity) {
		List<Node<?>> children = entity.getChildren();
		for (Node<?> child: children) {
			if ( child instanceof Attribute ) {
				Attribute<?, ?> attribute = (Attribute<?, ?>) child;
				if ( attribute.isEmpty() ) {
					performDefaultValueApply(attribute);
				}
			} else if ( child instanceof Entity ) {
				applyDefaultValues((Entity) child);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <V> void setFieldValue(Field<V> field, Object value, String remarks, FieldSymbol symbol){
		field.setValue((V)value);
		field.setRemarks(remarks);
		setFieldSymbol(field, symbol);
	}
	
	protected <V> void setFieldSymbol(Field<V> field, FieldSymbol symbol){
		Character symbolChar = null;
		if (symbol != null) {
			symbolChar = symbol.getCode();
		}
		field.setSymbol(symbolChar);
	}
	
	public Set<NodePointer> clearRelevanceRequiredDependencies(Node<?> node){
		Set<NodePointer> relevantDependencies = node.getRelevantDependencies();
		clearRelevantDependencies(relevantDependencies);
		Set<NodePointer> requiredDependencies = node.getRequiredDependencies();
		requiredDependencies.addAll(relevantDependencies);
		clearRequiredDependencies(requiredDependencies);
		return requiredDependencies;
	}
	
	public void clearRelevantDependencies(Set<NodePointer> nodePointers) {
		for (NodePointer nodePointer : nodePointers) {
			Entity entity = nodePointer.getEntity();
			entity.clearRelevanceState(nodePointer.getChildName());
		}
	}
	
	public void clearRequiredDependencies(Set<NodePointer> nodePointers) {
		for (NodePointer nodePointer : nodePointers) {
			Entity entity = nodePointer.getEntity();
			entity.clearRequiredState(nodePointer.getChildName());
		}
	}
	
	public Set<Attribute<?, ?>> clearValidationResults(Attribute<?,?> attribute){
		Set<Attribute<?,?>> checkDependencies = attribute.getCheckDependencies();
		clearValidationResults(checkDependencies);
		return checkDependencies;
	}

	public void clearValidationResults(Set<Attribute<?, ?>> checkDependencies) {
		for (Attribute<?, ?> attr : checkDependencies) {
			attr.clearValidationResults();
		}
	}
	
	protected Map<Integer, Object> createFieldValuesMap(
			Attribute<?, ?> attribute) {
		Map<Integer, Object> fieldValues = new HashMap<Integer, Object>();
		int fieldCount = attribute.getFieldCount();
		for (int idx = 0; idx < fieldCount; idx ++) {
			Field<?> field = attribute.getField(idx);
			fieldValues.put(idx, field.getValue());
		}
		return fieldValues;
	}

	protected <V extends Value> void setSymbolOnFields(
			Attribute<? extends NodeDefinition, V> attribute,
			FieldSymbol symbol) {
		for (Field<?> field : attribute.getFields()) {
			setFieldSymbol(field, symbol);
		}
	}
	
	protected <V extends Value> void setRemarksOnFields(
			Attribute<? extends NodeDefinition, V> attribute,
			String remarks) {
		for (Field<?> field : attribute.getFields()) {
			field.setRemarks(remarks);
		}
	}

	protected CodeAttribute getCodeParent(Entity context, CodeAttributeDefinition def) {
		try {
			Record record = context.getRecord();
			SurveyContext surveyContext = record.getSurveyContext();
			ExpressionFactory expressionFactory = surveyContext.getExpressionFactory();
			String parentExpr = def.getParentExpression();
			ModelPathExpression expression = expressionFactory.createModelPathExpression(parentExpr);
			Node<?> parentNode = expression.evaluate(context, null);
			if (parentNode != null && parentNode instanceof CodeAttribute) {
				return (CodeAttribute) parentNode;
			}
		} catch (Exception e) {
			// return null;
		}
		return null;
	}

	public CodeListItem findCodeListItem(List<CodeListItem> siblings, String code) {
		String adaptedCode = code.trim();
		adaptedCode = adaptedCode.toUpperCase();
		//remove initial zeros
		adaptedCode = adaptedCode.replaceFirst("^0+", "");
		adaptedCode = Pattern.quote(adaptedCode);

		for (CodeListItem item : siblings) {
			String itemCode = item.getCode();
			Pattern pattern = Pattern.compile("^[0]*" + adaptedCode + "$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(itemCode);
			if(matcher.find()) {
				return item;
			}
		}
		return null;
	}
	
	public List<CodeListItem> getAssignableCodeListItems(Entity parent, CodeAttributeDefinition def) {
		List<CodeListItem> items = null;
		if(StringUtils.isEmpty(def.getParentExpression())){
			items = def.getList().getItems();
		} else {
			CodeAttribute parentCodeAttribute = getCodeParent(parent, def);
			if(parentCodeAttribute!=null){
				CodeListItem parentCodeListItem = parentCodeAttribute.getCodeListItem();
				if(parentCodeListItem != null) {
					//TODO exception if parent not specified
					items = parentCodeListItem.getChildItems();
				}
			}
		}
		List<CodeListItem> result = new ArrayList<CodeListItem>();
		if(items != null) {
			ModelVersion version = getVersion();
			for (CodeListItem item : items) {
				if (version == null || version.isApplicable(item)) {
					result.add(item);
				}
			}
		}
		return result;
	}
	
	protected void setErrorConfirmed(Attribute<?,?> attribute, boolean confirmed){
		int fieldCount = attribute.getFieldCount();
		for( int i=0; i <fieldCount; i++ ){
			Field<?> field = attribute.getField(i);
			field.getState().set(CONFIRMED_ERROR_POSITION, confirmed);
		}
	}
	
	protected void performMissingValueApproval(Entity parentEntity, String childName, boolean approved) {
		org.openforis.idm.model.State childState = parentEntity.getChildState(childName);
		childState.set(APPROVED_MISSING_POSITION, approved);
	}
	
	protected void setDefaultValueApplied(Attribute<?, ?> attribute, boolean applied) {
		int fieldCount = attribute.getFieldCount();
		
		for( int i=0; i <fieldCount; i++ ){
			Field<?> field = attribute.getField(i);
			field.getState().set(DEFAULT_APPLIED_POSITION, applied);
		}
	}
	
	public void updateSkippedCount(Integer attributeId) {
		removeValidationCache(attributeId);
		validationCache.addSkippedNodeId(attributeId);
		skipped = null;
	}
	
	public void updateMinCountsValidationCache(Integer entityId, String childName, ValidationResultFlag flag) {
		validationCache.updateMinCountInfo(entityId, childName, flag);
		this.missing = null;
		this.missingErrors = null;
		this.missingWarnings = null;
		this.errors = null;
		this.warnings = null;
	}

	public void updateMaxCountsValidationCache(Integer entityId, String childName, ValidationResultFlag flag) {
		validationCache.updateMaxCountInfo(entityId, childName, flag);
		this.errors = null;
		this.warnings = null;
	}
	
	public void updateAttributeValidationCache(Integer attributeId, ValidationResults validationResults) {
		removeValidationCache(attributeId);
		
		int errorCounts = validationResults.getErrors().size();
		int warningCounts = validationResults.getWarnings().size();
		validationCache.setAttributeErrorCount(attributeId, errorCounts);
		validationCache.setAttributeWarningCount(attributeId, warningCounts);
		validationCache.setAttributeValidationResults(attributeId, validationResults);
		
		errors = null;
		warnings = null;
	}
	
	
	protected void removeValidationCache(Integer nodeId) {
		Node<?> node = this.getNodeByInternalId(nodeId);
		validationCache.remove(node);
		skipped = null;
		missing = null;
		missingErrors = null;
		missingWarnings = null;
		errors = null;
		warnings = null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((step == null) ? 0 : step.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollectRecord other = (CollectRecord) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (modifiedBy == null) {
			if (other.modifiedBy != null)
				return false;
		} else if (!modifiedBy.equals(other.modifiedBy))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (state != other.state)
			return false;
		if (step != other.step)
			return false;
		return true;
	}
	
}
