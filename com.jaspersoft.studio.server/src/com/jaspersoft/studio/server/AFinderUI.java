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
package com.jaspersoft.studio.server;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.studio.server.model.server.MServerProfile;

public abstract class AFinderUI {
	private MServerProfile serverProfile;
	private String text;
	private List<String> types = new ArrayList<String>();
	private List<String> fileTypes = new ArrayList<String>();
	private boolean showHidden = false;

	public AFinderUI(MServerProfile sp) {
		this.serverProfile = sp;
	}

	public boolean isShowHidden() {
		return showHidden;
	}

	public List<String> getFileTypes() {
		return fileTypes;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public List<String> getTypes() {
		return types;
	}

	public MServerProfile getServerProfile() {
		return serverProfile;
	}

	public abstract void showResults(List<ClientResourceLookup> res);
}