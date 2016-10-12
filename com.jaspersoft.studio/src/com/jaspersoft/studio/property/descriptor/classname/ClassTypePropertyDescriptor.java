/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.property.descriptor.classname;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;

public class ClassTypePropertyDescriptor extends RWComboBoxPropertyDescriptor {
	public void setClasses(List<Class<?>> classes) {
		this.classes = classes;
	}

	protected List<Class<?>> classes;

	public ClassTypePropertyDescriptor(Object id, String displayName) {
		super(id, displayName, ClassTypeComboCellEditor.DEFAULT_ITEMS, NullEnum.NULL, false);
	}

	public ClassTypePropertyDescriptor(Object id, String displayName, String[] items) {
		super(id, displayName, items, NullEnum.NULL, false);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		ClassTypeCellEditor editor = new ClassTypeCellEditor(parent);
		editor.setClasses(classes);
		editor.setValidator(ClassTypeCellEditorValidator.instance());
		setValidator(ClassTypeCellEditorValidator.instance());
		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		if (isLabelProviderSet()) {
			return super.getLabelProvider();
		}
		return new LabelProvider();
	}
}