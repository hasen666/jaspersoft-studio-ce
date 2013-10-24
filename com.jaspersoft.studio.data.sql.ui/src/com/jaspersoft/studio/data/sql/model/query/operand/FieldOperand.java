/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.operand;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.studio.data.sql.model.metadata.MSQLColumn;
import com.jaspersoft.studio.data.sql.model.query.expression.AMExpression;
import com.jaspersoft.studio.data.sql.model.query.from.MFromTable;

public class FieldOperand extends AOperand {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private MSQLColumn column;
	private MFromTable fromTable;

	public FieldOperand(MSQLColumn value, MFromTable fromTable, AMExpression<?> mexpr) {
		super(mexpr);
		setValue(value, fromTable);
	}

	public MFromTable getFromTable() {
		return fromTable;
	}

	public void setFromTable(MFromTable fromTable) {
		this.fromTable = fromTable;
	}

	public MSQLColumn getMColumn() {
		return column;
	}

	public void setColumn(MSQLColumn column) {
		this.column = column;
	}

	public void setValue(MSQLColumn value, MFromTable fromTable) {
		this.column = value;
		this.fromTable = fromTable;
	}

	@Override
	public String toSQLString() {
		if (column == null)
			return "___";
		StringBuffer ss = new StringBuffer();
		if (fromTable.getAlias() != null && !fromTable.getAlias().trim().isEmpty())
			ss.append(fromTable.getAlias());
		else
			ss.append(fromTable.getValue().toSQLString());
		ss.append(".\"" + column.getDisplayText() + "\"");
		return ss.toString();
	}

	@Override
	public String toXString() {
		return toSQLString();
	}

}
