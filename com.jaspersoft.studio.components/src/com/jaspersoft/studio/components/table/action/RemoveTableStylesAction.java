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
package com.jaspersoft.studio.components.table.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.components.Activator;
import com.jaspersoft.studio.components.table.messages.Messages;
import com.jaspersoft.studio.components.table.model.MTable;
import com.jaspersoft.studio.editor.gef.parts.FigureEditPart;
import com.jaspersoft.studio.model.style.command.DeleteStyleCommand;

/**
 * Action to delete all the styles from a table element
 * 
 * @author Orlandin Marco
 *
 */
public class RemoveTableStylesAction extends SelectionAction {
	
	/**
	 * The id of the action
	 */
	public static final String ID = "com.jaspersoft.studio.components.table.action.RemoveTableStyles";  //$NON-NLS-1$
	
	/**
	 * boolean flag to specify if the style element should be deleted or only be removed from the table
	 */
	private boolean deleteStyles = false;
	
	/**
	 * hashmap used internally to keep trace of the deleted styles
	 */
	private HashSet<String> deletedStyles;
	
	/**
	 * Jasperdesign of the actually handled table
	 */
	private JasperDesign design;
	
	public RemoveTableStylesAction(IWorkbenchPart part) {
		super(part);
		setText(Messages.RemoveStylesAction_actionTitle);
		setId(RemoveTableStylesAction.ID);
		setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/table-style-remove-16.png")); //$NON-NLS-1$
	}

	/**
	 * The action is enable only if enabled if and only if one of the edit part of the selection 
	 * has as model type an MTable
	 */
	@Override
	protected boolean calculateEnabled() {
		List<?> selectedObjects = getSelectedObjects();
		if (getSelectedObjects().size() >=0){
			for(Object selectedObject : selectedObjects){
				if (selectedObject instanceof EditPart){
					EditPart editPart = (EditPart)selectedObject;
					if (editPart != null){
						if (editPart.getModel() instanceof MTable) return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Return a list of the selected edit parts that has a model of type MTable
	 * 
	 * @return a not null list of edit part with an MTable as model
	 */
	private List<EditPart> getSelectedTables(){
		List<EditPart> result = new ArrayList<EditPart>();
		for(Object selectedObject : getSelectedObjects()){
			if (selectedObject instanceof EditPart){
				EditPart editPart = (EditPart)selectedObject;
				if (editPart.getModel() != null && editPart.getModel() instanceof MTable){
					result.add(editPart);
				}
			}
		}
		return result;
	}

	/**
	 * Execute the action
	 */
	@Override
	public void run() {
		deleteStyles = false;
		MessageDialog dialog = new MessageDialog(null, Messages.RemoveStylesAction_messageTitle, null, Messages.RemoveStylesAction_messageText, MessageDialog.QUESTION, 
												 new String[] {Messages.RemoveStylesAction_option1, Messages.RemoveStylesAction_option2, Messages.RemoveStylesAction_option3  }, 2);
		int selection = dialog.open();
		if (selection != 2){
			deleteStyles = selection == 0;
			List<EditPart> parts = getSelectedTables();
			execute(changeStyleCommand(parts));
			for(EditPart part : parts){
				if (part instanceof FigureEditPart) ((FigureEditPart)part).refreshVisuals();
			}
		}
	}

	/**
	 * Create the command to remove the style from a single cell and to delete the style 
	 * itself if the deleteStyle flag is enabled and if the command to delete the style
	 * was not already generated
	 * 
	 * @param cell the cell from where the style must be removed 
	 * @param container compound command where the new commands will be stored
	 */
	protected void createCommand(Cell cell, CompoundCommand container){
		if (cell != null && cell instanceof DesignCell){
			container.add(new RemoveStyleCommand((DesignCell)cell));
			if (deleteStyles && cell.getStyle() != null){
				JRStyle style = cell.getStyle();
				if (!deletedStyles.contains(style.getName())){
					deletedStyles.add(style.getName());
					container.add(new DeleteStyleCommand(design, (JRDesignStyle)style));
				}
			}
		}
	}

	/**
	 * Create the commands to remove the style from a list of columns, one by one
	 * 
	 * @param columns not null list of columns
	 * @param container compound command where the new commands will be stored
	 */
	protected void createCommandForColumns(List<BaseColumn> columns, CompoundCommand command){
		for (BaseColumn col : columns){
			createCommand(col.getColumnFooter(),command);
			createCommand(col.getColumnHeader(),command);
			createCommand(col.getTableFooter(),command);
			createCommand(col.getTableHeader(),command);
			
			if (col instanceof StandardColumn){
				StandardColumn baseCol = (StandardColumn)col;
				createCommand(baseCol.getDetailCell(),command);
			}
			
			if (col instanceof StandardColumnGroup){
				StandardColumnGroup colGroup = (StandardColumnGroup)col;
				createCommandForColumns(colGroup.getColumns(), command);
			}
		}
	}
	
	/**
	 * 
	 * Generate the command to remove all the styles from the table, it's essentially a compound command
	 * composed of many commands
	 * 
	 * @param editParts the edit parts containing an MCrosstab as model
	 * 
	 * @return the command to remove all the styles
	 */
	protected Command changeStyleCommand(List<EditPart> parts) {
		CompoundCommand command = new CompoundCommand();
		deletedStyles = new HashSet<String>();
		for(EditPart part : parts){
			MTable table = (MTable)part.getModel();
			design = table.getJasperDesign();
			StandardTable jrTable = (StandardTable)((JRDesignComponentElement)table.getValue()).getComponent();
			createCommandForColumns(jrTable.getColumns(), command);
		}
		return command;
	}
}
