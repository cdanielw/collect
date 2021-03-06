/**
 * 
 */
package org.openforis.collect.model;

import org.openforis.collect.metamodel.ui.UIOptions;
import org.openforis.collect.metamodel.ui.UIOptionsConstants;
import org.openforis.idm.metamodel.ApplicationOptions;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.SurveyContext;

/**
 * @author M. Togna
 * @author S. Ricci
 * 
 */
public class CollectSurvey extends Survey {

	private static final long serialVersionUID = 1L;
	
	private boolean work;
	
	protected CollectSurvey(SurveyContext surveyContext) {
		super(surveyContext);
		this.work = false;
	}

	public UIOptions createUIOptions() {
		return new UIOptions(this);
	}
	
	public UIOptions getUIOptions() {
		ApplicationOptions applicationOptions = getApplicationOptions(UIOptionsConstants.UI_TYPE);
		return (UIOptions) applicationOptions;
	}
	
	@Override
	public void addApplicationOptions(ApplicationOptions options) {
		super.addApplicationOptions(options);
		if ( options instanceof UIOptions ) {
			((UIOptions) options).setSurvey(this);
		}
	}
	
	public boolean isWork() {
		return work;
	}

	public void setWork(boolean work) {
		this.work = work;
	}

}
