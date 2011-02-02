/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer. Copyright (C) 2005 - 2010 Jaspersoft Corporation. All
 * rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of iReport.
 * 
 * iReport is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * iReport is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with iReport. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.chart.model.command;

import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignChartDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.wizard.Wizard;

import com.jaspersoft.studio.chart.messages.Messages;
import com.jaspersoft.studio.chart.model.MChart;
import com.jaspersoft.studio.model.dataset.MDatasetRun;
import com.jaspersoft.studio.wizards.dataset.WizardDatasetPage;

public class ChartWizard extends Wizard {
	private ChartWizardPage page0;
	private WizardDatasetPage page1;
	private MChart chart;

	public ChartWizard() {
		super();
		setWindowTitle(Messages.common_chart_wizard);
	}

	@Override
	public void addPages() {
		page0 = new ChartWizardPage();
		addPage(page0);

		page1 = new WizardDatasetPage(jasperDesign);
		addPage(page1);
		page1.setDataSetRun(new MDatasetRun(new JRDesignDatasetRun(), jasperDesign));
	}

	public MChart getChart() {
		this.chart = new MChart();
		chart.setValue(MChart.createJRElement(jasperDesign, page0.getChartType()));
		JRDesignChart jrChart = (JRDesignChart) chart.getValue();
		JRDesignChartDataset jrDataSet = (JRDesignChartDataset) jrChart.getDataset();
		jrDataSet.setDatasetRun((JRDatasetRun) page1.getDataSetRun().getValue());
		return chart;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	private JasperDesign jasperDesign;

	public void init(JasperDesign jd) {
		this.jasperDesign = jd;
	}
}
