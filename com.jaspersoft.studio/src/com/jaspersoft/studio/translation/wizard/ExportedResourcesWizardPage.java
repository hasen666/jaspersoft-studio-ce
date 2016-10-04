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
package com.jaspersoft.studio.translation.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.translation.ExtendedTranslationInformation;
import com.jaspersoft.studio.wizards.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSHelpWizardPage;

/**
 * Wizard page that allow to select the single plugins inside a translation project
 * that will be converted into usable fragments. By selecting a plugin it is also 
 * Possible to see informations about the plugin and configure the fragment
 * 
 * @author Orlandin Marco
 *
 */
public class ExportedResourcesWizardPage extends JSSHelpWizardPage {

	/**
	 * Table where all the plugins are listed
	 */
	private Table pluginTable;
	
	/**
	 * Name of the fragment for the resource actually selected
	 */
	private Text pluginName;
	
	/**
	 * Version of the fragment the resource actually selected
	 */
	private Text pluginVersion;
	
	/**
	 * Name of the host plugin for the fragment that will be generated by the resource actually selected
	 */
	private Text hostPluginName;
	
	/**
	 * Minimum version of the host plugin for the fragment that will be generated by the resource actually selected
	 */
	private Text hostPluginVersion;
	
	/**
	 * Name of the fragment producer
	 */
	private Text pluginProducer;
	
	/**
	 * List of the available resources that can be converted into a fragment
	 */
	private List<ExtendedTranslationInformation> resources;
	
	/**
	 * List of the resources selected by the user that will be converted into a fragment
	 */
	private List<ExtendedTranslationInformation> selectedResources = new ArrayList<ExtendedTranslationInformation>();
	
	/**
	 * Actually selected resource, from where the additional info are shown
	 */
	private ExtendedTranslationInformation selectedItem = null;
	
	/**
	 * Boolean guard to avoid unnecessary update when an element is selected
	 */
	private Boolean updating = false;
	
	/**
	 * Listener used on all the textual information field to update the information of the selected
	 * resource when the user type something new
	 */
	private ModifyListener modListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			//guard used to avoid infinite cycles of refresh, since the modify listener 
			//is called also when the content of an element is set programatically
			synchronized (updating) {
				if (!updating && selectedItem != null){
					updating = true;
					selectedItem.setBundleName(pluginName.getText());
					selectedItem.setBundleVersion(pluginVersion.getText());
					selectedItem.setHostPluginName(hostPluginName.getText());
					selectedItem.setHostPluginVersion(hostPluginVersion.getText());
					selectedItem.setBundleProducer(pluginProducer.getText());
					Text usedWidget = (Text)e.widget;
					int caretPosition = usedWidget.getCaretPosition();
					updateSelection();
					usedWidget.setSelection(caretPosition);
					updating = false;
				}
			}
		}
	};
	
	/**
	 * Create an instance of the class
	 * 
	 * @param resources list of the resources that the user can select to became a fragment
	 */
	public ExportedResourcesWizardPage(List<ExtendedTranslationInformation> resources) {
		super(Messages.ExportedResourcesWizardPage_dialogName);
		setTitle(Messages.ExportedResourcesWizardPage_pageTitle);
		setMessage(Messages.ExportedResourcesWizardPage_pageMessage);
		this.resources = resources;
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1,false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//Create the table with the checkbox to select the single resources
		pluginTable = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData tableData = new GridData(GridData.FILL_BOTH);
		tableData.heightHint = 500;
		pluginTable.setLayoutData(tableData);
		
		for(ExtendedTranslationInformation resource : resources){
			 TableItem item = new TableItem(pluginTable, SWT.NONE);
	     item.setText(resource.getPluginName());
	     item.setChecked(true);
	     item.setData(resource);
		}
		
		Group infoGroup = new Group(container, SWT.NONE);
		infoGroup.setText(Messages.ExportedResourcesWizardPage_selectedGroupTitle);
		GridData infoData = new GridData(GridData.FILL_BOTH);
		infoData.heightHint = 120;
		infoData.minimumHeight = 120;
		infoGroup.setLayoutData(infoData);
		infoGroup.setLayout(new GridLayout(4,false));
		
		Label pluginNameLabel = new Label(infoGroup, SWT.NONE);
		pluginNameLabel.setText(Messages.ExportedResourcesWizardPage_pluginNameLabel);
		pluginName = new Text(infoGroup, SWT.BORDER);
		pluginName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pluginName.addModifyListener(modListener);
		
		Label pluginVersionLabel = new Label(infoGroup, SWT.NONE);
		pluginVersionLabel.setText(Messages.ExportedResourcesWizardPage_pluginVersion);
		pluginVersion = new Text(infoGroup, SWT.BORDER);
		pluginVersion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pluginVersion.addModifyListener(modListener);
		
		Label hostPluginNameLabel = new Label(infoGroup, SWT.NONE);
		hostPluginNameLabel.setText(Messages.ExportedResourcesWizardPage_hostPlaginNameLabel);
		hostPluginName = new Text(infoGroup, SWT.BORDER);
		hostPluginName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostPluginName.addModifyListener(modListener);
		
		Label hostPluginVersionLabel = new Label(infoGroup, SWT.NONE);
		hostPluginVersionLabel.setText(Messages.ExportedResourcesWizardPage_hostPluginVersion);
		hostPluginVersion = new Text(infoGroup, SWT.BORDER);
		hostPluginVersion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostPluginVersion.addModifyListener(modListener);
		
		Label pluginProducerLabel = new Label(infoGroup, SWT.NONE);
		pluginProducerLabel.setText(Messages.ExportedResourcesWizardPage_pluginProducer);
		pluginProducer = new Text(infoGroup, SWT.BORDER);
		pluginProducer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pluginProducer.addModifyListener(modListener);
		
		//When an element of the table is selected update the information panel
		pluginTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean atLeastOneSelected = false;
				for (TableItem item : pluginTable.getItems()){
					if (item.getChecked()) {
						atLeastOneSelected = true;
						break;
					}
				}
				setPageComplete(atLeastOneSelected);
				if (atLeastOneSelected && pluginTable.getSelection().length>0){
					TableItem selectedEntry = pluginTable.getSelection()[0];
					selectedItem = (ExtendedTranslationInformation) selectedEntry.getData();
					synchronized (updating) {
						updating = true;
						updateSelection();
						updating = false;
					}
				}
			}
		});
		
		//Select the first element if there is at least one, or disable the input controls otherwise
		if (resources.size() == 0){
			pluginName.setEnabled(false);
			pluginVersion.setEnabled(false);
			hostPluginName.setEnabled(false);
			hostPluginVersion.setEnabled(false);
			pluginProducer.setEnabled(false);
			setPageComplete(false);
		} else {
			pluginTable.setSelection(0);
			TableItem selectedEntry = pluginTable.getSelection()[0];
			selectedItem = (ExtendedTranslationInformation) selectedEntry.getData();
			synchronized (updating) {
				updating = true;
				updateSelection();
				updating = false;
			}
		}
		
		setControl(container);
	}
	
	/**
	 * When the user advance to the next page the list of resources selected is populated, 
	 * by doing this we avoid to read from a disposed widget because on the finish the 
	 * wizard pages are already disposed. Other then this when advancing the following 
	 * page, if it is a LocalesTranslationWizard page, will be initialized to have 
	 * selected by default the locales of the resources in the project
	 */
	@Override
	public IWizardPage getNextPage() {
		selectedResources.clear();
		for (TableItem item : pluginTable.getItems()){
			if (item.getChecked()) {
				selectedResources.add((ExtendedTranslationInformation)item.getData());
			}
		}
		IWizardPage page = super.getNextPage();
		if (page instanceof LocalesTranslationWizardPage){
			((LocalesTranslationWizardPage)page).initializeSelectedLocales();
		}
		return page;
	}
	
	/**
	 * Return the list of resources that should be converted into fragment
	 * 
	 * @return a not null list of resources
	 */
	public List<ExtendedTranslationInformation> getSelectedResources(){
		return selectedResources;
	}
	
	/**
	 * Put in the information panel the information about the resource actually selected
	 */
	private void updateSelection(){
		if (selectedItem != null){
			pluginName.setText(selectedItem.getBundleName());
			pluginVersion.setText(selectedItem.getBundleVersion());
			hostPluginName.setText(selectedItem.getHostPluginName());
			hostPluginVersion.setText(selectedItem.getHostPluginVersion());
			pluginProducer.setText(selectedItem.getBundleProducer());
		}
	}

	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_INSTALL_TRANSLATION_STEP1;
	}

}
