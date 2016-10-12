/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.preview.MultiPageContainer;
import com.jaspersoft.studio.editor.preview.view.APreview;
import com.jaspersoft.studio.editor.preview.view.control.ReportControler;
import com.jaspersoft.studio.editor.preview.view.control.VExporter;
import com.jaspersoft.studio.messages.MessagesByKeys;
import com.jaspersoft.studio.preferences.exporter.JRExporterPreferencePage;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

public class ViewExporterAction extends ASwitchAction implements IMenuCreator {
	public ViewExporterAction(MultiPageContainer container, IFile file) {
		super(container, ReportControler.FORM_EXPORTER, AS_DROP_DOWN_MENU);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/equalizer--arrow.png")); //$NON-NLS-1$
		setToolTipText(MessagesByKeys.getString(ReportControler.FORM_EXPORTER));
		this.file = file;
		setMenuCreator(this);
	}

	private IFile file;
	private Menu listMenu;

	public void dispose() {
		if (listMenu != null)
			listMenu.dispose();
	}

	@Override
	public Menu getMenu(Control parent) {
		if (listMenu != null)
			listMenu.dispose();
		listMenu = new Menu(parent);
		if (file != null) {
			MenuItem m1 = new MenuItem(listMenu, SWT.PUSH);
			m1.setText("Show in properties");
			m1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String pageID = JRExporterPreferencePage.PAGE_ID;
					APreview p = container.getViewer(viewKey);
					if (p instanceof VExporter) {
						PreferencePage pp = ((VExporter) p).getPreferencePage();
						if (pp instanceof FieldEditorOverlayPage)
							pageID = ((FieldEditorOverlayPage) pp).getPageId();
					}
					// open properties
					PreferenceDialog pref = PreferencesUtil.createPropertyDialogOn(UIUtils.getShell(), file, pageID, null, null);
					if (pref != null)
						pref.open();
					run();
				}
			});
		}
		return listMenu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}
}