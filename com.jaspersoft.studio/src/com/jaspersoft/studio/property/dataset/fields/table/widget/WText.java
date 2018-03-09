/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.property.dataset.fields.table.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import net.sf.jasperreports.eclipse.util.Misc;

public class WText extends AWControl {
	public WText(AWidget aw) {
		super(aw);
	}

	protected Text txt;
	protected boolean refresh = false;

	@Override
	protected void createControl(Composite parent) {
		txt = new Text(parent, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 200;
		txt.setLayoutData(gd);
		txt.addModifyListener(e -> {
			if (refresh)
				return;
			aw.setValue(txt.getText());
			txt.setToolTipText(aw.getToolTipText());
		});
	}

	@Override
	protected void fillValue() {
		String v = getText();
		try {
			refresh = true;
			txt.setText(Misc.nvl(v, ""));
		} finally {
			refresh = false;
		}
		txt.setToolTipText(aw.getToolTipText());
	}

	@Override
	public void addDisposeListener(DisposeListener dlistener) {
		txt.addDisposeListener(dlistener);
	}

	@Override
	public void setEnabled(boolean en) {
		txt.setEnabled(en);
	}

}
