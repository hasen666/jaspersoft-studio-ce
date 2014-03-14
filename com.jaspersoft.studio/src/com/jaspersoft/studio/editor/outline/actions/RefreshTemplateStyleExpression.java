/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.editor.outline.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.ExternalStylesManager;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.style.MStyleTemplate;

/**
 * Action to reload an external template style, this will also re-evaluate its expression
 * 
 * @author Orlandin Marco
 *
 */
public class RefreshTemplateStyleExpression extends SelectionAction {

	/** The Constant ID. */
	public static final String ID = "refresh_template_style_expression"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public RefreshTemplateStyleExpression(IWorkbenchPart part) {
		super(part);
	}

	/**
	 * Initializes this action's text and images.
	 */
	@Override
	protected void init() {
		super.init();
		setText("Reload Style");
		setToolTipText("Evaluate the expression of a template style reload it");
		setId(RefreshTemplateStyleExpression.ID);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/refresh_style_action.png")); //$NON-NLS-1$
		setEnabled(false);
	}

	/**
	 * Enable only if there is at least one style that can be exported
	 */
	@Override
	protected boolean calculateEnabled() {
		return !getSelectedStyles().isEmpty();
	}

	@Override
	public void run() {
		List<MStyleTemplate> templates = getSelectedStyles();
		for(MStyleTemplate template : templates){
			ExternalStylesManager.refreshStyle(template);
		}
	}
	
	/**
	 * Return the list of all the selected Template styles. 
	 * 
	 * @return a not null list of MStyleTemplate
	 */
	private List<MStyleTemplate> getSelectedStyles(){
		List<?> objects = getSelectedObjects();
		if (objects == null || objects.isEmpty())
			return new ArrayList<MStyleTemplate>();
		List<MStyleTemplate> result = new ArrayList<MStyleTemplate>();
		for (Object obj : objects){
			if (obj instanceof EditPart) {
				ANode n = (ANode) ((EditPart) obj).getModel();
				if (n instanceof MStyleTemplate){
					result.add((MStyleTemplate)n);
				}
			}
		}
		return result;
	}
}
