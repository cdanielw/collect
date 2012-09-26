/**
 * 
 */
package org.openforis.collect.designer.viewmodel;

import java.util.List;

import org.openforis.collect.designer.session.SessionStatus;
import org.openforis.collect.model.LanguageConfiguration;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Window;

/**
 * @author S. Ricci
 *
 */
public class SurveyEditVM extends SurveyEditBaseVM {

	private static final String SURVEY_SELECT_LANGUAGE_POP_UP_URL = "survey_edit/select_language_popup.zul";
	private static final String SRS_MANAGER_POP_UP_URL = "survey_edit/srs_popup.zul";
	
	private Window selectLanguagePopUp;
	private Window srsPopUp;
	
	@Override
	@Init(superclass=false)
	public void init() {
		super.init();
		if ( currentLanguageCode == null ) {
			currentLanguageCode = "eng";
			LanguageConfiguration languageConfiguration = new LanguageConfiguration();
			languageConfiguration.addLanguageCode(currentLanguageCode);
			//openLanguageManagerPopUp();
		}
	}
	
	@Command
	public void openLanguageManagerPopUp() {
		selectLanguagePopUp = (Window) Executions.createComponents(
				SURVEY_SELECT_LANGUAGE_POP_UP_URL, null, null);
		selectLanguagePopUp.doModal();
	}
	
	@GlobalCommand
	public void openSRSManagerPopUp() {
		srsPopUp = (Window) Executions.createComponents(
				SRS_MANAGER_POP_UP_URL, null, null);
		srsPopUp.doModal();
	}
	
	
	@GlobalCommand
	public void closeSRSManagerPopUp() {
		closePopUp(srsPopUp);
	}
	
	public void languageCodeSelected(@BindingParam("code") String selectedLanguageCode) {
		SessionStatus sessionStatus = getSessionStatus();
		sessionStatus.setCurrentLanguageCode(selectedLanguageCode);
		BindUtils.postGlobalCommand(null, null, SurveySelectLanguageVM.CURRENT_LANGUAGE_CHANGED_COMMAND, null);
	}
	
	@GlobalCommand
	@NotifyChange({"availableLanguages"})
	public void surveyLanguagesChanged() {
		closeSelectLanguagePopUp();
	}

	private void closeSelectLanguagePopUp() {
		closePopUp(selectLanguagePopUp);
	}
	
	public List<String> getAvailableLanguages() {
		LanguageConfiguration langConf = survey.getLanguageConfiguration();
		if ( langConf != null ) {
	 		List<String> langCodes = langConf.getLanguageCodes();
			return new BindingListModelList<String>(langCodes, false);
		} else {
			return null;
		}
	}
	
}