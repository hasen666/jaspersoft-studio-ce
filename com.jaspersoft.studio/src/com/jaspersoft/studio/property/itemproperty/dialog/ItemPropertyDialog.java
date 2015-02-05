/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.property.itemproperty.dialog;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.sf.jasperreports.components.map.ItemProperty;
import net.sf.jasperreports.components.map.StandardItemProperty;
import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.engine.design.JRDesignExpression;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.editor.expression.ExpressionContext.Visibility;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.infoList.ElementDescription;
import com.jaspersoft.studio.property.infoList.SelectableComposite;
import com.jaspersoft.studio.property.itemproperty.desc.ADescriptor;
import com.jaspersoft.studio.property.itemproperty.desc.ItemPropertyDescriptor;
import com.jaspersoft.studio.swt.events.ExpressionModifiedEvent;
import com.jaspersoft.studio.swt.events.ExpressionModifiedListener;
import com.jaspersoft.studio.swt.widgets.WTextExpression;
import com.jaspersoft.studio.utils.Misc;

/**
 * Dialog that allows editing the information associated to a {@link ItemProperty} element.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class ItemPropertyDialog extends ATitledDialog implements IExpressionContextSetter {

	private Composite dialogArea;
	private Text propertyName;
	private Button useExpressionCheckbox;
	private Text propertyValue;
	private WTextExpression propertyValueExpression;
	private ExpressionContext expContext;
	private StandardItemProperty itemProperty;
	private SelectableComposite infoPanel;
	private ADescriptor descriptor;

	public ItemPropertyDialog(Shell parentShell, ItemProperty itemProperty, ADescriptor descriptor) {
		super(parentShell);
		setTitle(Messages.ItemPropertyDialog_EditItemProperty);
		setDefaultSize(450, 400);
		this.itemProperty = (StandardItemProperty) itemProperty;
		this.descriptor = descriptor;
	}

	@Override
	public boolean close() {
		descriptor.setOldItemProperty(null);
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		dialogArea.setLayout(layout);

		Label lblPropertyName = new Label(dialogArea, SWT.NONE);
		lblPropertyName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblPropertyName.setText(Messages.ItemPropertyDialog_PropertyName);
		propertyName = new Text(dialogArea, SWT.BORDER);
		propertyName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		useExpressionCheckbox = new Button(dialogArea, SWT.CHECK);
		useExpressionCheckbox.setText(Messages.ItemPropertyDialog_UseExpression);
		useExpressionCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label lblPropertyValue = new Label(dialogArea, SWT.NONE);
		lblPropertyValue.setText(Messages.ItemPropertyDialog_PropertyValue);
		lblPropertyValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		propertyValue = new Text(dialogArea, SWT.BORDER);
		GridData gd_propertyValue = new GridData(SWT.FILL, SWT.FILL, true, false);
		propertyValue.setLayoutData(gd_propertyValue);

		propertyValueExpression = new WTextExpression(dialogArea, SWT.NONE);
		GridData gd_propertyValueExpression = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_propertyValueExpression.heightHint = 50;
		propertyValueExpression.setLayoutData(gd_propertyValueExpression);
		propertyValueExpression.setExpressionContext(this.expContext);
		if (descriptor.getItemData() != null && descriptor.getItemData().getDataset() == null)
			expContext.setVisibilities(EnumSet.noneOf(Visibility.class));

		infoPanel = new SelectableComposite(dialogArea);
		infoPanel.setItems(getPropertiesInformation());
		GridData infoGD = new GridData(SWT.FILL, SWT.FILL, true, true);
		infoGD.heightHint = 200;
		infoGD.verticalIndent = 5;
		infoPanel.setLayoutData(infoGD);
		infoPanel.SetDoubleClickListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propertyName.setText(infoPanel.getSelectedElement().getName());
			}
		});

		initWidgets();
		addListeners();

		return dialogArea;
	}

	private List<ElementDescription> getPropertiesInformation() {
		List<ElementDescription> descriptions = new ArrayList<ElementDescription>();
		for (ItemPropertyDescriptor<?> ipd : descriptor.getItemPropertyDescriptors())
			descriptions.add(new ElementDescription(ipd.getName(), ipd.getDescription(), false));
		return descriptions;
	}

	private void initWidgets() {
		if (this.itemProperty == null) {
			this.itemProperty = new StandardItemProperty("", "", null); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (this.itemProperty.getValue() != null) {
			useExpressionCheckbox.setSelection(false);
			propertyName.setText(Misc.nvl(itemProperty.getName()));
			propertyValue.setText(Misc.nvl(itemProperty.getValue()));
			propertyValueExpression.setVisible(false);
			propertyValueExpression.setEnabled(false);
			propertyValueExpression.setExpression(null);
			((GridData) propertyValueExpression.getLayoutData()).exclude = true;
		} else {
			useExpressionCheckbox.setSelection(true);
			propertyName.setText(Misc.nvl(itemProperty.getName()));
			propertyValueExpression.setExpression((JRDesignExpression) itemProperty.getValueExpression());
			propertyValue.setVisible(false);
			propertyValue.setEnabled(false);
			((GridData) propertyValue.getLayoutData()).exclude = true;
		}
		for (ItemPropertyDescriptor<?> ipd : descriptor.getItemPropertyDescriptors()) {
			if (ipd.getName().equals(itemProperty.getName()) && ipd.isMandatory()) {
				propertyName.setEnabled(false);
				break;
			}
		}
	}

	private void addListeners() {
		propertyName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				itemProperty.setName(propertyName.getText());
				validateDialog();
			}
		});
		propertyValue.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				itemProperty.setValue(propertyValue.getText());
				validateDialog();
			}
		});
		propertyValueExpression.addModifyListener(new ExpressionModifiedListener() {
			@Override
			public void expressionModified(ExpressionModifiedEvent event) {
				itemProperty.setValueExpression(event.modifiedExpression);
				validateDialog();
			}
		});
		useExpressionCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (useExpressionCheckbox.getSelection()) {
					// hide normal textbox
					propertyValue.setText(""); //$NON-NLS-1$
					itemProperty.setValue(null);
					propertyValue.setVisible(false);
					propertyValue.setEnabled(false);
					((GridData) propertyValue.getLayoutData()).exclude = true;

					// and show expression widget
					propertyValueExpression.setVisible(true);
					propertyValueExpression.setEnabled(true);
					((GridData) propertyValueExpression.getLayoutData()).exclude = false;
				} else {
					// hide the expression widget
					propertyValueExpression.setVisible(false);
					propertyValueExpression.setEnabled(false);
					propertyValueExpression.setExpression(null);
					((GridData) propertyValueExpression.getLayoutData()).exclude = true;

					// and show the normal textbox
					propertyValue.setText(""); //$NON-NLS-1$
					propertyValue.setVisible(true);
					propertyValue.setEnabled(true);
					((GridData) propertyValue.getLayoutData()).exclude = false;
				}
				validateDialog();
				dialogArea.layout();
			}
		});
	}

	private void validateDialog() {
		Button ok = getButton(IDialogConstants.OK_ID);
		if (!ok.isEnabled())
			ok.setEnabled(true);
		String str = null;
		try {
			descriptor.validateItem(itemProperty);
		} catch (Exception e) {
			str = e.getMessage();
			ok.setEnabled(false);
		}
		setError(str);
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.PRIMARY_MODAL);
		setBlockOnOpen(true);
	}

	@Override
	public void setExpressionContext(ExpressionContext expContext) {
		this.expContext = expContext;
	}

	public StandardItemProperty getItemProperty() {
		return this.itemProperty;
	}

}