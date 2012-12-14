package com.jaspersoft.studio.model.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.model.field.MField;
import com.jaspersoft.studio.model.parameter.MParameter;
import com.jaspersoft.studio.model.variable.MVariable;

public class RenameDatasetObjectNameCommand extends Command {
	private String newvalue;
	private String oldvalue;
	private String type;

	private JasperDesign jd;
	private JasperReportsContext jContext;
	private JRDataset dataset;
	private Set<JRDesignExpression> cexpr = new HashSet<JRDesignExpression>();

	public RenameDatasetObjectNameCommand(MField mfield, String oldvalue) {
		super();
		jd = mfield.getJasperDesign();
		jContext = mfield.getJasperConfiguration();
		dataset = mfield.getJRDataset();
		type = "\\$F\\{";
		this.newvalue = mfield.getValue().getName();
		this.oldvalue = oldvalue;
	}

	public RenameDatasetObjectNameCommand(MVariable mvar, String oldvalue) {
		super();
		jd = mvar.getJasperDesign();
		jContext = mvar.getJasperConfiguration();
		dataset = mvar.getJRDataset();
		type = "\\$V\\{";
		this.newvalue = mvar.getValue().getName();
		this.oldvalue = oldvalue;
	}

	public RenameDatasetObjectNameCommand(MParameter mparam, String oldvalue) {
		super();
		jd = mparam.getJasperDesign();
		jContext = mparam.getJasperConfiguration();
		dataset = mparam.getJRDataset();
		type = "\\$P\\{";
		this.newvalue = mparam.getValue().getName();
		this.oldvalue = oldvalue;
	}

	@Override
	public void execute() {
		cexpr.clear();
		JRExpressionCollector reportCollector = JRExpressionCollector.collector(jContext, jd);
		JRExpressionCollector datasetCollector = reportCollector.getCollector(dataset);
		List<JRExpression> datasetExpressions = datasetCollector.getExpressions();
		// update expressions
		for (JRExpression expr : datasetExpressions) {
			String s = expr.getText();
			if (s != null && s.length() > 4 && s.contains("$F{" + oldvalue + "}")) {
				s = s.replaceAll(type + oldvalue + "}", type + newvalue + "}");

				JRDesignExpression dexpr = (JRDesignExpression) expr;
				dexpr.setText(s);
				cexpr.add((JRDesignExpression) expr);
			}
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		for (JRDesignExpression de : cexpr) {
			de.setText(de.getText().replaceAll(newvalue, oldvalue));
		}
	}
}
