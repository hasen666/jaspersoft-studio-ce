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
package com.jaspersoft.studio.preferences.exporter;

import net.sf.jasperreports.engine.JRExporterParameter;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.StudioPreferencePage;
import com.jaspersoft.studio.preferences.editor.CEncodingFieldEditor;
import com.jaspersoft.studio.preferences.editor.JSSComboFieldEditor;
import com.jaspersoft.studio.preferences.editor.pages.PagesFieldEditor;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;
import com.jaspersoft.studio.preferences.util.PropertiesHelper;
import com.jaspersoft.studio.utils.Misc;

/*
 * 
 */
public class JRExporterPreferencePage extends FieldEditorOverlayPage {

	public static final String NSF_EXPORT_LEGACY_BORDER_OFFSET = "net.sf.jasperreports.export.legacy.border.offset"; //$NON-NLS-1$
	public static final String EXPPARAM_OFFSET_X = "expparam.offset.x"; //$NON-NLS-1$
	public static final String EXPPARAM_OFFSET_Y = "expparam.offset.y"; //$NON-NLS-1$
	public static final String EXPPARAM_INDEX_PAGE = "expparam.index.page"; //$NON-NLS-1$

	public JRExporterPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
		setDescription(Messages.JRExporterPreferencePage_6);
	}

	@Override
	public void createFieldEditors() {
		CEncodingFieldEditor cefe = new CEncodingFieldEditor(JRExporterParameter.PROPERTY_CHARACTER_ENCODING,
				Messages.JRExporterPreferencePage_7, Messages.JRExporterPreferencePage_8, getFieldEditorParent());
		addField(cefe);
		for (Control c : cefe.getControls())
			HelpSystem.setHelp(c, StudioPreferencePage.REFERENCE_PREFIX + cefe.getPreferenceName());

		JSSComboFieldEditor cfe = new JSSComboFieldEditor(NSF_EXPORT_LEGACY_BORDER_OFFSET,
				Messages.JRExporterPreferencePage_9, new String[][] {
						{ Messages.JRExporterPreferencePage_10, Messages.JRExporterPreferencePage_11 },
						{ Messages.JRExporterPreferencePage_12, Messages.JRExporterPreferencePage_13 } }, getFieldEditorParent());
		addField(cfe);
		HelpSystem.setHelp(cfe.getComboBoxControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + cfe.getPreferenceName());

		BooleanFieldEditor bf = new BooleanFieldEditor(
				JRExporterParameter.PROPERTY_EXPORT_PARAMETERS_OVERRIDE_REPORT_HINTS, Messages.JRExporterPreferencePage_14,
				getFieldEditorParent());
		addField(bf);
		HelpSystem.setHelp(bf.getDescriptionControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + bf.getPreferenceName());

		bf = new BooleanFieldEditor(JRExporterParameter.PROPERTY_IGNORE_PAGE_MARGINS, Messages.JRExporterPreferencePage_15,
				getFieldEditorParent());
		addField(bf);
		HelpSystem.setHelp(bf.getDescriptionControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + bf.getPreferenceName());

		addField(new IntegerFieldEditor(EXPPARAM_OFFSET_X, Messages.JRExporterPreferencePage_16, getFieldEditorParent()));
		addField(new IntegerFieldEditor(EXPPARAM_OFFSET_Y, Messages.JRExporterPreferencePage_17, getFieldEditorParent()));

		addField(new PagesFieldEditor(EXPPARAM_INDEX_PAGE, Messages.JRExporterPreferencePage_18, getFieldEditorParent()));
	}

	public static void getDefaults(IPreferenceStore store) {
		store.setDefault(JRExporterParameter.PROPERTY_CHARACTER_ENCODING,
				Misc.nvl(PropertiesHelper.DPROP.getProperty(JRExporterParameter.PROPERTY_CHARACTER_ENCODING), "UTF-8")); //$NON-NLS-1$
		store.setDefault(JRExporterParameter.PROPERTY_IGNORE_PAGE_MARGINS,
				PropertiesHelper.DPROP.getBooleanProperty(JRExporterParameter.PROPERTY_IGNORE_PAGE_MARGINS));
		store
				.setDefault(JRExporterParameter.PROPERTY_EXPORT_PARAMETERS_OVERRIDE_REPORT_HINTS, PropertiesHelper.DPROP
						.getBooleanProperty(JRExporterParameter.PROPERTY_EXPORT_PARAMETERS_OVERRIDE_REPORT_HINTS));
		store.setDefault(NSF_EXPORT_LEGACY_BORDER_OFFSET,
				Misc.nvl(PropertiesHelper.DPROP.getProperty(NSF_EXPORT_LEGACY_BORDER_OFFSET), "DEFAULT")); //$NON-NLS-1$

		store.setDefault(EXPPARAM_OFFSET_X, 0);
		store.setDefault(EXPPARAM_OFFSET_Y, 0);

		store.setDefault(EXPPARAM_INDEX_PAGE, "all"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	protected String getPageId() {
		return "com.jaspersoft.studio.preferences.exporter.JRExporterPreferencePage.property"; //$NON-NLS-1$
	}

}
