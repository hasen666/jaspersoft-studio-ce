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
package com.jaspersoft.studio.server.action.resource;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.jaspersoft.studio.server.action.server.EditServerAction;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.wizard.permission.PermissionDialog;
import com.jaspersoft.studio.server.wizard.permission.PermissionWizard;

public class ShowPermissionsAction extends Action {
	private TreeViewer treeViewer;

	public ShowPermissionsAction(TreeViewer treeViewer) {
		super();
		setId("showpermission"); //$NON-NLS-1$
		setText(Messages.ShowPermissionsAction_1);
		setToolTipText(Messages.ShowPermissionsAction_2);
		this.treeViewer = treeViewer;
	}

	@Override
	public boolean isEnabled() {
		final TreeSelection s = (TreeSelection) treeViewer.getSelection();
		TreePath[] p = s.getPaths();
		for (int i = 0; i < p.length; i++) {
			final Object obj = p[i].getLastSegment();
			if (obj instanceof AMResource && AddResourceAction.isSpecialFolder((AMResource) obj))
				return false;
		}
		return super.isEnabled();
	}

	@Override
	public void run() {
		final TreeSelection s = (TreeSelection) treeViewer.getSelection();
		TreePath[] p = s.getPaths();
		for (int i = 0; i < p.length; i++) {
			final Object obj = p[i].getLastSegment();
			if (obj instanceof AMResource) {
				PermissionWizard wizard = new PermissionWizard((AMResource) obj);
				PermissionDialog dialog = new PermissionDialog(UIUtils.getShell(), wizard);
				dialog.addApplyListener(wizard);
				dialog.open();
			} else if (obj instanceof MServerProfile) {
				EditServerAction.fillServerProfile((MServerProfile) obj, treeViewer);
			}
		}
	}
}