package com.jaspersoft.studio.wizards.group;

import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.wizard.Wizard;

import com.jaspersoft.studio.model.group.MGroup;

public class BandGroupWizard extends Wizard {
	private MGroup group;

	public boolean isAddHeader() {
		return step2.isAddHeader();
	}

	public boolean isAddFooter() {
		return step2.isAddFooter();
	}

	private WizardBandGroupPage step1;
	private WizardBandGroupLayoutPage step2;

	public BandGroupWizard() {
		super();
		setWindowTitle("Group Band");
	}

	@Override
	public void addPages() {
		this.group = new MGroup();
		group.setValue(MGroup.createJRGroup(jasperDesign.getMainDesignDataset()));

		step1 = new WizardBandGroupPage(jasperDesign);
		addPage(step1);
		step1.setGroup(group);

		step2 = new WizardBandGroupLayoutPage();
		addPage(step2);
	}

	public MGroup getGroup() {
		return group;
	}

	@Override
	public boolean performFinish() {
		JRDesignGroup gr = (JRDesignGroup) group.getValue();
		if (jasperDesign.getMainDesignDataset().getGroupsMap().get(gr.getName()) != null)
			return false;
		return true;
	}

	private JasperDesign jasperDesign;

	public void init(JasperDesign jd) {
		this.jasperDesign = jd;
	}
}
