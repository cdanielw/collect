/**
 * 
 */
package org.openforis.collect.designer.viewmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openforis.collect.designer.session.SessionStatus;
import org.openforis.collect.designer.util.ComponentUtil;
import org.openforis.collect.designer.util.MessageUtil;
import org.openforis.collect.designer.util.PageUtil;
import org.openforis.collect.designer.util.Resources;
import org.openforis.collect.designer.util.Resources.Page;
import org.openforis.collect.manager.SurveyManager;
import org.openforis.collect.manager.process.SimpleProcess;
import org.openforis.collect.manager.validation.SurveyValidator;
import org.openforis.collect.manager.validation.SurveyValidator.SurveyValidationResult;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.SurveySummary;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.SurveyImportException;
import org.openforis.collect.utils.ExecutorServiceUtil;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Window;

/**
 * 
 * @author S. Ricci
 * 
 */
public class SurveySelectVM extends BaseVM {

	private static Log log = Log.lookup(SurveySelectVM.class);

	private static final String TEXT_XML = "text/xml";

	public static final String CLOSE_SURVEY_IMPORT_POP_UP_GLOBAL_COMMNAD = "closeSurveyImportPopUp";

	public static final String UPDATE_SURVEY_LIST_COMMAND = "updateSurveyList";

	@WireVariable
	private SurveyManager surveyManager;

	@WireVariable
	private SurveyValidator surveyValidator;

	private SurveySummary selectedSurvey;

	private Window surveyImportPopUp;

	private Window validationResultsPopUp;

	private List<SurveySummary> summaries;

	private SurveyExportProcess surveyExportProcess;

	private Window processStatusPopUp;

	@Init()
	public void init() {
		PageUtil.clearConfirmClose();
		loadSurveySummaries();
	}

	@Command
	public void editSelectedSurvey() throws IOException {
		CollectSurvey surveyWork = loadSelectedSurveyForEdit();
		SessionStatus sessionStatus = getSessionStatus();
		Integer publishedSurveyId = null;
		if (selectedSurvey.isPublished()) {
			if (selectedSurvey.isWork()) {
				publishedSurveyId = selectedSurvey.getPublishedId();
			} else {
				publishedSurveyId = selectedSurvey.getId();
			}
		}
		sessionStatus.setPublishedSurveyId(publishedSurveyId);
		sessionStatus.setSurvey(surveyWork);
		sessionStatus.setCurrentLanguageCode(null);
		Executions.sendRedirect(Page.SURVEY_EDIT.getLocation());
	}

	@Command
	public void newSurvey() throws IOException {
		CollectSurvey survey = surveyManager.createSurveyWork();
		SessionStatus sessionStatus = getSessionStatus();
		sessionStatus.setSurvey(survey);
		sessionStatus.setCurrentLanguageCode(null);
		Executions.sendRedirect(Page.SURVEY_EDIT.getLocation());
	}

	@Command
	public void exportSelectedSurvey() throws IOException {
		CollectSurvey survey = loadSelectedSurvey();
		surveyExportProcess = new SurveyExportProcess(surveyManager, survey);
		surveyExportProcess.init();
		ExecutorServiceUtil.executeInCachedPool(surveyExportProcess);
		openSurveyExportStatusPopUp(survey);
	}

	protected void openSurveyExportStatusPopUp(CollectSurvey survey) {
		processStatusPopUp = ProcessStatusPopUpVM.openPopUp(Labels.getLabel(
				"survey.export_survey.process_status_popup.message",
				new String[] { survey.getName() }), surveyExportProcess, true);
	}

	protected void closeProcessStatusPopUp() {
		closePopUp(processStatusPopUp);
		processStatusPopUp = null;
	}

	@GlobalCommand
	public void processComplete() {
		closeProcessStatusPopUp();
		
		if (surveyExportProcess != null) {
			File file = surveyExportProcess.getOutputFile();
			CollectSurvey survey = surveyExportProcess.getSurvey();
			String fileName = survey.getName() + ".xml";
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				Filedownload.save(reader, TEXT_XML, fileName);
			} catch (FileNotFoundException e) {
				log.error(e);
				MessageUtil.showError("survey.export_survey.error", new String[]{e.getMessage()});
			} finally {
				surveyExportProcess = null;
			}
		}
	}

	@GlobalCommand
	public void processError(@BindingParam("errorMessage") String errorMessage) {
		closeProcessStatusPopUp();
		surveyExportProcess = null;
	}
	
	@GlobalCommand
	public void processCancelled() {
		closeProcessStatusPopUp();
		surveyExportProcess = null;
	}

	@Command
	public void publishSelectedSurvey() throws IOException {
		final CollectSurvey survey = loadSelectedSurvey();
		final CollectSurvey publishedSurvey = selectedSurvey.isPublished() ? surveyManager
				.getByUri(survey.getUri()) : null;
		if (validateSurvey(survey, publishedSurvey)) {
			MessageUtil.showConfirm(new MessageUtil.ConfirmHandler() {
				@Override
				public void onOk() {
					performSurveyPublishing(survey);
				}
			}, "survey.publish.confirm");
		}
	}

	@Command
	public void deleteSelectedSurvey() {
		String messageKey;
		if (selectedSurvey.isWork()) {
			if (selectedSurvey.isPublished()) {
				messageKey = "survey.delete.published_work.confirm";
			} else {
				messageKey = "survey.delete.work.confirm";
			}
		} else {
			messageKey = "survey.delete.confirm";
		}
		MessageUtil.showConfirm(new MessageUtil.ConfirmHandler() {
			@Override
			public void onOk() {
				performSelectedSurveyDeletion();
			}
		}, messageKey, new String[] { selectedSurvey.getName() });
	}

	protected void performSelectedSurveyDeletion() {
		if (selectedSurvey.isWork()) {
			surveyManager.deleteSurveyWork(selectedSurvey.getId());
		} else {
			surveyManager.deleteSurvey(selectedSurvey.getId());
		}
		loadSurveySummaries();
		notifyChange("surveySummaries");
	}

	protected boolean validateSurvey(CollectSurvey survey,
			CollectSurvey oldPublishedSurvey) {
		List<SurveyValidationResult> validationResults = surveyValidator
				.validateCompatibility(oldPublishedSurvey, survey);
		if (validationResults.isEmpty()) {
			return true;
		} else {
			openValidationResultsPopUp(validationResults);
			return false;
		}
	}

	@GlobalCommand
	public void openValidationResultsPopUp(
			@BindingParam("validationResults") List<SurveyValidationResult> validationResults) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("validationResults", validationResults);
		validationResultsPopUp = openPopUp(
				Resources.Component.SURVEY_VALIDATION_RESULTS_POPUP
						.getLocation(),
				true, args);
	}

	@GlobalCommand
	public void closeValidationResultsPopUp() {
		closePopUp(validationResultsPopUp);
		validationResultsPopUp = null;
	}

	protected void performSurveyPublishing(CollectSurvey survey) {
		try {
			surveyManager.publish(survey);
			loadSurveySummaries();
			notifyChange("surveySummaries");
			Object[] args = new String[] { survey.getName() };
			MessageUtil.showInfo("survey.successfully_published", args);
			User user = getLoggedUser();
			surveyManager.validateRecords(survey.getId(), user);
		} catch (SurveyImportException e) {
			throw new RuntimeException(e);
		}
	}

	@Command
	public void goToIndex() {
		Executions.sendRedirect(Page.INDEX.getLocation());
	}

	@Command
	public void openSurveyImportPopUp() {
		surveyImportPopUp = openPopUp(
				Resources.Component.SURVEY_IMPORT_POPUP.getLocation(), true);
	}

	@GlobalCommand
	public void closeSurveyImportPopUp(
			@BindingParam("successfullyImported") Boolean successfullyImported) {
		if (surveyImportPopUp != null) {
			Binder binder = ComponentUtil.getBinder(surveyImportPopUp);
			SurveyImportVM vm = (SurveyImportVM) binder.getViewModel();
			vm.reset();
		}
		closePopUp(surveyImportPopUp);
		surveyImportPopUp = null;
		if (successfullyImported != null && successfullyImported.booleanValue()) {
			loadSurveySummaries();
			notifyChange("surveySummaries");
		}
	}

	@Command
	public void validateAllRecords() {
		User user = getLoggedUser();
		Integer publishedSurveyId = getSelectedPublishedSurveyId();
		surveyManager.validateRecords(publishedSurveyId, user);
		updateSurveyList();
	}

	private Integer getSelectedPublishedSurveyId() {
		return !selectedSurvey.isPublished() ? null
				: selectedSurvey.isWork() ? selectedSurvey.getPublishedId()
						: selectedSurvey.getId();
	}

	@Command
	public void cancelRecordValidation() {
		Integer selectedPublishedSurveyId = getSelectedPublishedSurveyId();
		surveyManager.cancelRecordValidation(selectedPublishedSurveyId);
		updateSurveyList();
	}

	@GlobalCommand
	public void updateSurveyList() {
		if ( surveyImportPopUp != null || processStatusPopUp != null ) {
			//skip survey list update
			return;
		}
		List<SurveySummary> newSummaries = surveyManager.loadSummaries();
		if (summaries == null) {
			summaries = newSummaries;
		} else {
			for (SurveySummary newSummary : newSummaries) {
				SurveySummary oldSummary = findSummary(summaries,
						newSummary.getId(), newSummary.isPublished(),
						newSummary.isWork());
				if (oldSummary == null) {
					// TODO handle this??
				} else {
					oldSummary.setRecordValidationProcessStatus(newSummary
							.getRecordValidationProcessStatus());
					BindUtils.postNotifyChange(null, null, oldSummary,
							"recordValidationProgressStatus");
					BindUtils.postNotifyChange(null, null, oldSummary,
							"recordValidationInProgress");
					BindUtils.postNotifyChange(null, null, oldSummary,
							"recordValidationProgressPercent");
				}
			}
		}
	}

	private void loadSurveySummaries() {
		summaries = surveyManager.loadSummaries();
	}

	private SurveySummary findSummary(List<SurveySummary> summaries2,
			Integer id, boolean published, boolean work) {
		for (SurveySummary summary : summaries) {
			if (summary.getId().equals(id)
					&& summary.isPublished() == published
					&& summary.isWork() == work) {
				return summary;
			}
		}
		return null;
	}

	protected CollectSurvey loadSelectedSurveyForEdit() {
		String uri = selectedSurvey.getUri();
		CollectSurvey surveyWork;
		if (selectedSurvey.isWork()) {
			surveyWork = surveyManager.loadSurveyWork(selectedSurvey.getId());
		} else if (selectedSurvey.isPublished()) {
			surveyWork = surveyManager.duplicatePublishedSurveyForEdit(uri);
		} else {
			throw new IllegalStateException(
					"Trying to load an invalid survey: " + uri);
		}
		return surveyWork;
	}

	protected CollectSurvey loadSelectedSurvey() {
		String uri = selectedSurvey.getUri();
		CollectSurvey survey;
		if (selectedSurvey.isWork()) {
			survey = surveyManager.loadSurveyWork(selectedSurvey.getId());
		} else {
			survey = surveyManager.getByUri(uri);
		}
		return survey;
	}

	public ListModel<SurveySummary> getSurveySummaries() {
		return new BindingListModelList<SurveySummary>(summaries, false);
	}

	public SurveySummary getSelectedSurvey() {
		return selectedSurvey;
	}

	public void setSelectedSurvey(SurveySummary selectedSurvey) {
		this.selectedSurvey = selectedSurvey;
	}

	@DependsOn("selectedSurvey")
	public boolean isSurveySelected() {
		return this.selectedSurvey != null;
	}

	@DependsOn("selectedSurvey")
	public boolean isEditingDisabled() {
		return this.selectedSurvey == null;
	}

	@DependsOn("selectedSurvey")
	public boolean isExportDisabled() {
		return this.selectedSurvey == null;
	}

	@DependsOn("selectedSurvey")
	public boolean isPublishDisabled() {
		return this.selectedSurvey == null || !this.selectedSurvey.isWork();
	}

	static class SurveyExportProcess extends SimpleProcess {

		private CollectSurvey survey;
		private SurveyManager surveyManager;
		private File outputFile;

		public SurveyExportProcess(SurveyManager surveyManager,
				CollectSurvey survey) {
			this.surveyManager = surveyManager;
			this.survey = survey;
		}

		@Override
		public void startProcessing() throws Exception {
			super.startProcessing();
			outputFile = File.createTempFile("openforis-collect-survey-export",
					survey.getName());
			OutputStream os = null;
			try {
				os = new FileOutputStream(outputFile);
				surveyManager.marshalSurvey(survey, os, true, true, false);
			} finally {
				IOUtils.closeQuietly(os);
			}
		}

		public CollectSurvey getSurvey() {
			return survey;
		}

		public File getOutputFile() {
			return outputFile;
		}
	}
}
