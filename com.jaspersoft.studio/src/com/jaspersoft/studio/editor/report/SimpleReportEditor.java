/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.editor.report;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.gef.parts.JSSGraphicalViewerKeyHandler;
import com.jaspersoft.studio.editor.gef.parts.JasperDesignEditPartFactory;
import com.jaspersoft.studio.editor.gef.parts.MainDesignerRootEditPart;
import com.jaspersoft.studio.editor.gef.rulers.ReportRuler;
import com.jaspersoft.studio.editor.gef.rulers.ReportRulerProvider;
import com.jaspersoft.studio.editor.gef.ui.actions.RZoomComboContributionItem;
import com.jaspersoft.studio.editor.gef.ui.actions.ViewSettingsDropDownAction;
import com.jaspersoft.studio.editor.java2d.JSSScrollingGraphicalViewer;
import com.jaspersoft.studio.editor.outline.JDReportOutlineView;
import com.jaspersoft.studio.editor.outline.actions.CreateBandAction;
import com.jaspersoft.studio.editor.outline.actions.CreateFieldAction;
import com.jaspersoft.studio.editor.outline.actions.CreateParameterAction;
import com.jaspersoft.studio.editor.outline.actions.CreateParameterSetAction;
import com.jaspersoft.studio.editor.outline.actions.CreateSortFieldAction;
import com.jaspersoft.studio.editor.outline.actions.CreateVariableAction;
import com.jaspersoft.studio.editor.outline.actions.DeleteGroupReportAction;
import com.jaspersoft.studio.editor.palette.JDPaletteFactory;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.IContainer;
import com.jaspersoft.studio.plugin.ExtensionManager;
import com.jaspersoft.studio.preferences.RulersGridPreferencePage;
import com.jaspersoft.studio.property.section.report.action.PageFormatAction;
import com.jaspersoft.studio.property.section.report.action.PageRemoveMarginsAction;
import com.jaspersoft.studio.utils.AContributorAction;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/**
 * A reduced version of the report editor that can be used to edit tool. 
 * This editor dosen't provide the preview and the compilation and the actions
 * related to it. Some other actions were removed, like the create dataset action,
 * since their not needed for a tool
 * 
 * @author Orlandin Marco
 *
 */
public class SimpleReportEditor extends ReportEditor {

	/** 
	 * The Constant ID. 
	 */
	public static final String ID = SimpleReportEditor.class.getName();

	/**
	 * Instantiates a new report editor.
	 */
	public SimpleReportEditor(JasperReportsConfiguration jrContext) {
		super(jrContext);
		setPartName(Messages.common_main_report);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().getControl().setBackground(ColorConstants.button);

		GraphicalViewer graphicalViewer = getGraphicalViewer();
		MainDesignerRootEditPart rootEditPart = new MainDesignerRootEditPart();

		graphicalViewer.setRootEditPart(rootEditPart);
		// set EditPartFactory
		graphicalViewer.setEditPartFactory(new JasperDesignEditPartFactory());

		// set rulers providers
		RulerProvider provider = new ReportRulerProvider(new ReportRuler(true, RulerProvider.UNIT_PIXELS));
		graphicalViewer.setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, provider);

		provider = new ReportRulerProvider(new ReportRuler(false, RulerProvider.UNIT_PIXELS));
		graphicalViewer.setProperty(RulerProvider.PROPERTY_VERTICAL_RULER, provider);

		Boolean isRulerVisible = jrContext.getPropertyBoolean(RulersGridPreferencePage.P_PAGE_RULERGRID_SHOWRULER);

		graphicalViewer.setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, isRulerVisible);

		createAdditionalActions();
		graphicalViewer.setKeyHandler(new JSSGraphicalViewerKeyHandler(graphicalViewer));
		if (graphicalViewer instanceof JSSScrollingGraphicalViewer){
			((JSSScrollingGraphicalViewer)graphicalViewer).setSelectionOverrider(new ParentSelectionOverrider(IContainer.class, true));
		}
	}
	
	/**
	 * This subeditor show only a subset of the elements in the palette, excluding those that
	 * need a dataset
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		return JDPaletteFactory.createSimplePalette(getIgnorePalleteElements(), jrContext);
	}

	//Methods to create the subset of actions 
	
	protected JDReportOutlineView getOutlineView() {
		//Rebuild the outline only if it's closed or disposed
		if (outlinePage == null || outlinePage.isDisposed()) {
			TreeViewer viewer = new TreeViewer();
			outlinePage = new JDReportOutlineView(this, viewer) {
				protected void initActions(ActionRegistry registry, IActionBars bars) {
					String id = DeleteGroupReportAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));
	
					id = CreateFieldAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));
	
					id = CreateSortFieldAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));
	
					id = CreateVariableAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));
	
					id = CreateParameterAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));
					
					id = CreateParameterSetAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));

					id = CreateBandAction.ID;
					bars.setGlobalActionHandler(id, registry.getAction(id));
				}
			};
		}
		return outlinePage;
	}

	@SuppressWarnings("unchecked")
	protected void createEditorActions(ActionRegistry registry) {
		List<String> selectionActions = getSelectionActions();

		IAction action = new CreateBandAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateBandAction.ID);

		ExtensionManager m = JaspersoftStudioPlugin.getExtensionManager();
		List<Action> lst = m.getActions(this);
		for (Action act : lst) {
			action = act;
			registry.registerAction(action);
			selectionActions.add(act.getId());
		}

		action = new PageFormatAction(this);
		registry.registerAction(action);
		selectionActions.add(action.getId());

		action = new PageRemoveMarginsAction(this);
		registry.registerAction(action);
		selectionActions.add(action.getId());
	}

	@Override
	public void contributeItemsToEditorTopToolbar(IToolBarManager toolbarManager) {
		toolbarManager.add(new Separator());
		toolbarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		toolbarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
		RZoomComboContributionItem zoomItem = new RZoomComboContributionItem(getEditorSite().getPage());
		GraphicalViewer graphicalViewer = getGraphicalViewer();
		ZoomManager property = (ZoomManager) graphicalViewer.getProperty(ZoomManager.class.toString());
		if (property != null){
			zoomItem.setZoomManager(property);
		}
		zoomItem.setEnabled(true);
		toolbarManager.add(zoomItem);
		toolbarManager.add(new Separator());
		// Contributed actions
		List<AContributorAction> contributedActions = JaspersoftStudioPlugin.getExtensionManager().getActions();
		for (AContributorAction a : contributedActions) {
			a.setJrConfig((JasperReportsConfiguration) getGraphicalViewer().getProperty("JRCONTEXT"));
			toolbarManager.add(a);
		}
		// Global "View" menu items
		toolbarManager.add(new ViewSettingsDropDownAction(getActionRegistry()));
	}
}