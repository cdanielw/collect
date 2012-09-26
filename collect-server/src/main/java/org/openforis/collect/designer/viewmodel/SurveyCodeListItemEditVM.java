/**
 * 
 */
package org.openforis.collect.designer.viewmodel;

import org.openforis.collect.designer.form.CodeListItemFormObject;
import org.openforis.collect.designer.form.ItemFormObject;
import org.openforis.idm.metamodel.CodeListItem;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zkplus.databind.BindingListModelList;

/**
 * 
 * @author S. Ricci
 *
 */
public class SurveyCodeListItemEditVM extends SurveyItemEditVM<CodeListItem> {

	@Init
	public void init(@ExecutionArgParam("item") CodeListItem item) {
		setEditedItem(item);
	}
	
	@Override
	protected void addNewItemToSurvey() {
		//do nothing
	}
	
	@Override
	protected void deleteItemFromSurvey(CodeListItem item) {
		//do nothing
	}
	
	@Override
	public BindingListModelList<CodeListItem> getItems() {
		//do nothing
		return null;
	}
	
	@Override
	protected ItemFormObject<CodeListItem> createFormObject() {
		return new CodeListItemFormObject();
	}

	@Command
	public void close() {
		if ( currentFormValid ) {
			String command = SurveyCodeListsEditVM.CLOSE_CODE_LIST_ITEM_POP_UP_COMMAND;
			BindUtils.postGlobalCommand(null, null, command, null);
		} else {
			//TODO show alert message: invalid form 
		}
	}
	
}