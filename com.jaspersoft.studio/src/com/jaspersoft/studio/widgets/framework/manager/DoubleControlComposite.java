/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.widgets.framework.manager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.widgets.framework.ui.ItemPropertyDescription;

/**
 * Composite used to handle a couple of controls into a stack layout.
 * This can be used to easily handle the switch between expression mode
 * and editor mode of the {@link ItemPropertyDescription} widgets
 * 
 * @author Orlandin Marco
 *
 */
public class DoubleControlComposite extends Composite {

	/**
	 * The composite where to place one control
	 */
	private Composite firstControlContainer;
	
	/**
	 * The composite where to place the other control
	 */
	private Composite secondControlContainer;
	
	/**
	 * The layout of this control
	 */
	private StackLayout layout = new StackLayout();
	
	/**
	 * Create this composite with already a stack layout inside. The
	 * two subcomposite where the controls can be created are created
	 * here also, and with a grid layout with a single column
	 * 
	 * @param parent the parent of this composite
	 * @param style the style of this composite
	 */
	public DoubleControlComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(layout);
		firstControlContainer = new Composite(this, SWT.NONE);
		firstControlContainer.setLayout(WidgetFactory.getNoPadLayout(1));
		secondControlContainer = new Composite(this, SWT.NONE);
		secondControlContainer.setLayout(WidgetFactory.getNoPadLayout(1));
		layout.topControl = firstControlContainer;
	}

	/**
	 * Return the first composite where one control can be created
	 * 
	 * @return a not null composite with a grid layout of a single column
	 */
	public Composite getFirstContainer(){
		return firstControlContainer;
	}

	/**
	 * Return the second composite where one control can be created
	 * 
	 * @return a not null composite with a grid layout of a single column
	 */
	public Composite getSecondContainer(){
		return secondControlContainer;
	}
	
	/**
	 * Make visible in the stack layout the first composite and its content
	 * it also request a layout of this container and of its children
	 */
	public void switchToFirstContainer(){
		layout.topControl = firstControlContainer;
		layout(true, true);
	}
	
	/**
	 * Make visible in the stack layout the second composite and its content
	 * it also request a layout of this container and of its children
	 */
	public void switchToSecondContainer(){
		layout.topControl = secondControlContainer;
		layout(true, true);
	}
}