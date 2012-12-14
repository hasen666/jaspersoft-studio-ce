/*******************************************************************************
 * Copyright (C) 2010 - 2012 Jaspersoft Corporation. All rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.property.dataset.dialog;

import java.util.List;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.IPropertySource;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.IDataPreviewInfoProvider;
import com.jaspersoft.studio.data.IFieldSetter;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.MQuery;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.model.field.command.CreateFieldCommand;
import com.jaspersoft.studio.model.field.command.DeleteFieldCommand;
import com.jaspersoft.studio.model.parameter.command.CreateParameterCommand;
import com.jaspersoft.studio.model.parameter.command.DeleteParameterCommand;
import com.jaspersoft.studio.model.sortfield.command.CreateSortFieldCommand;
import com.jaspersoft.studio.model.sortfield.command.DeleteSortFieldCommand;
import com.jaspersoft.studio.property.SetValueCommand;
import com.jaspersoft.studio.swt.widgets.CSashForm;
import com.jaspersoft.studio.swt.widgets.WTextExpression;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class DatasetDialog extends FormDialog implements IFieldSetter, IDataPreviewInfoProvider {
	private MDataset mdataset;
	// private MReport mreport;
	private JasperReportsConfiguration jConfig;

	public DatasetDialog(Shell shell, MDataset mdataset, JasperReportsConfiguration jConfig) {
		super(shell);
		this.mdataset = mdataset;
		this.jConfig = jConfig;
		newdataset = (JRDesignDataset) ((JRDesignDataset) mdataset.getValue()).clone();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.DatasetDialog_title);
		setShellStyle(getShellStyle() | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	@Override
	public boolean close() {
		createCommand();
		dataquery.dispose();
		return super.close();
	}

	@Override
	protected void createFormContent(final IManagedForm mform) {
		FormToolkit toolkit = mform.getToolkit();
		Composite body = mform.getForm().getBody();
		body.setLayout(new GridLayout(1, true));
		background = body.getBackground();
		body.setBackground(body.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		dataquery = new DataQueryAdapters(mform.getForm().getBody(), jConfig, newdataset, background) {

			@Override
			public void setFields(List<JRDesignField> fields) {
				DatasetDialog.this.setFields(fields);
			}

			@Override
			public List<JRDesignField> getCurrentFields() {
				return DatasetDialog.this.getCurrentFields();
			}

			@Override
			public void setParameters(List<JRDesignParameter> params) {

			}

			@Override
			public int getContainerType() {
				return CONTAINER_WITH_INFO_TABLES;
			}

		};

		dataquery.createToolbar(body);

		SashForm sf = new CSashForm(body, SWT.VERTICAL);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 600;
		gd.minimumHeight = 400;
		gd.widthHint = 800;
		sf.setLayoutData(gd);
		sf.setLayout(new GridLayout());

		dataquery.createTop(sf, this);

		// int tabHeight = c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		// tabHeight = Math.max(tabHeight, ctf.getTabHeight());
		// ctf.setTabHeight(tabHeight);
		//
		// ctf.setTopRight(c);

		createBottom(sf, toolkit);
		sf.setWeights(new int[] { 450, 250 });

		JasperDesign jd = mdataset.getJasperDesign();
		if (jd == null) {
			jd = mdataset.getMreport().getJasperDesign();
		}
		setDataset(jd, newdataset);
	}

	public void setFields(List<JRDesignField> fields) {
		ftable.setFields(fields);
	}

	public List<JRDesignField> getCurrentFields() {
		return ftable.getFields();
	}

	private void createBottom(Composite parent, FormToolkit toolkit) {
		CTabFolder tabFolder = new CTabFolder(parent, SWT.BOTTOM | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 250;
		tabFolder.setLayoutData(gd);

		createFields(toolkit, tabFolder);
		createParameters(toolkit, tabFolder);
		createSortFields(toolkit, tabFolder);
		createFilterExpression(toolkit, tabFolder);
		createDataPreview(toolkit, tabFolder);

		tabFolder.setSelection(0);
	}

	private void createDataPreview(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem dataPreviewtab = new CTabItem(tabFolder, SWT.NONE);
		dataPreviewtab.setText(Messages.DatasetDialog_DataPreviewTab);

		dataPreviewTable = new DataPreviewTable(tabFolder, this, background);

		dataPreviewtab.setControl(dataPreviewTable.getControl());
	}

	private void createParameters(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_ParametersTab);

		ptable = new ParametersTable(tabFolder, newdataset, background);

		bptab.setControl(ptable.getControl());
	}

	private void createFields(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_fieldstab);

		ftable = new FieldsTable(tabFolder, newdataset, background);

		bptab.setControl(ftable.getControl());
	}

	private void createSortFields(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_sortingtab);

		sftable = new SortFieldsTable(tabFolder, newdataset, background);

		bptab.setControl(sftable.getControl());
	}

	private void createFilterExpression(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_filterexpression);

		Composite sectionClient = toolkit.createComposite(tabFolder);
		FillLayout fLayout = new FillLayout();
		fLayout.marginHeight = 5;
		fLayout.marginWidth = 5;
		sectionClient.setLayout(fLayout);

		filterExpression = new WTextExpression(sectionClient, SWT.NONE);
		filterExpression.setBackground(sectionClient.getBackground());
		JRDesignDataset designDataset = mdataset.getValue();
		if (designDataset != null) {
			filterExpression.setExpressionContext(new ExpressionContext(designDataset, mdataset.getJasperConfiguration()));
		} else {
			filterExpression.setExpressionContext(ExpressionEditorSupportUtil.getReportExpressionContext());
		}
		filterExpression.setExpression((JRDesignExpression) newdataset.getFilterExpression());

		bptab.setControl(sectionClient);
	}

	private CompoundCommand command;
	private Color background;

	public CompoundCommand getCommand() {
		return command;
	}

	private JRDesignDataset newdataset;
	private FieldsTable ftable;
	private ParametersTable ptable;
	private SortFieldsTable sftable;
	private DataQueryAdapters dataquery;
	private WTextExpression filterExpression;
	private DataPreviewTable dataPreviewTable;

	public void setDataset(JasperDesign jDesign, JRDesignDataset ds) {
		dataquery.setDataset(jDesign, ds);
	}

	public void createCommand() {
		JRDesignDataset ds = (JRDesignDataset) (mdataset.getParent() == null ? mdataset.getJasperConfiguration()
				.getJasperDesign().getMainDesignDataset() : mdataset.getValue());
		command = new CompoundCommand();

		String lang = newdataset.getQuery().getLanguage();
		String qtext = newdataset.getQuery().getText();
		if (ds.getQuery() == null) {
			JRDesignQuery jrQuery = new JRDesignQuery();
			jrQuery.setLanguage(lang);
			jrQuery.setText(qtext);
			command.add(setValueCommand(JRDesignDataset.PROPERTY_QUERY, new MQuery(jrQuery, mdataset), mdataset));
		} else {
			IPropertySource mquery = (IPropertySource) mdataset.getPropertyValue(JRDesignDataset.PROPERTY_QUERY);
			if (ds.getQuery().getLanguage() == null || !ds.getQuery().getLanguage().equals(lang))
				command.add(setValueCommand(JRDesignQuery.PROPERTY_LANGUAGE, lang, mquery));
			if (!ds.getQuery().getText().equals(qtext))
				command.add(setValueCommand(JRDesignQuery.PROPERTY_TEXT, qtext, mquery));
		}
		String fexprtext = filterExpression.getText();
		if (fexprtext.trim().equals("")) //$NON-NLS-1$
			fexprtext = null;
		command.add(setValueCommand(JRDesignDataset.PROPERTY_FILTER_EXPRESSION, fexprtext, mdataset));
		command.add(setValueCommand(MDataset.PROPERTY_MAP, newdataset.getPropertiesMap(), mdataset));

		List<JRField> dsfields = ds.getFieldsList();
		List<JRDesignField> fields = ftable.getFields();
		for (JRField f : dsfields)
			command.add(new DeleteFieldCommand(jConfig, ds, (JRDesignField) f));
		for (JRDesignField newf : fields)
			command.add(new CreateFieldCommand(ds, newf, -1));

		List<JRSortField> dssfields = ds.getSortFieldsList();
		List<JRDesignSortField> sfields = sftable.getFields();
		for (JRSortField f : dssfields)
			command.add(new DeleteSortFieldCommand(ds, f));
		for (JRDesignSortField newf : sfields)
			command.add(new CreateSortFieldCommand(ds, newf, -1));

		List<JRParameter> dssparameters = ds.getParametersList();
		List<JRParameter> sparams = newdataset.getParametersList();
		for (JRParameter f : dssparameters)
			command.add(new DeleteParameterCommand(jConfig, ds, f));
		for (JRParameter newf : sparams)
			command.add(new CreateParameterCommand(ds, newf, -1));
	}

	private Command setValueCommand(String property, Object value, IPropertySource target) {
		SetValueCommand cmd = new SetValueCommand();
		cmd.setTarget(target);
		cmd.setPropertyId(property);
		cmd.setPropertyValue(value);
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IDataPreviewInfoProvider#getJasperReportsConfig()
	 */
	public JasperReportsConfiguration getJasperReportsConfig() {
		return this.jConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IDataPreviewInfoProvider#getDataAdapterDescriptor()
	 */
	public DataAdapterDescriptor getDataAdapterDescriptor() {
		return this.dataquery.getDataAdapter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IDataPreviewInfoProvider#getDesignDataset()
	 */
	public JRDesignDataset getDesignDataset() {
		return this.newdataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IDataPreviewInfoProvider#getFieldsForPreview()
	 */
	public List<JRDesignField> getFieldsForPreview() {
		return this.getCurrentFields();
	}
}
