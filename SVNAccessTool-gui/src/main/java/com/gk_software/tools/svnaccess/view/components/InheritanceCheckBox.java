package com.gk_software.tools.svnaccess.view.components;

import gk.ee_common.i18n.I18NResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.components.GroupsTree.GroupBean;
import com.vaadin.ui.CheckBox;

/**
 * Represents the checkbox for changing if the inheritance is disabled.
 */
public class InheritanceCheckBox extends CheckBox {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger
			.getLogger(InheritanceCheckBox.class);

	/** The checkbox text. */
	public static final I18NResource INHERITANCE_CHECKBOX_TEXT = new I18NResource(
			InheritanceCheckBox.class, "INHERITANCE_CHECKBOX_TEXT",
			"disable inheritance");

	/** The control class. */
	private IControl control;

	/** The instance of this class. */
	private InheritanceCheckBox thisBox;

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public InheritanceCheckBox(final IControl control) {
		this.control = control;
		thisBox = this;
		this.setImmediate(true);
		this.setCaption(I18NSupport.getMessage(INHERITANCE_CHECKBOX_TEXT));
		this.addStyleName("boldCaption");
		this.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				Node lastChosenNode = control.getApplication().getDirectories()
						.getLastChosenItemId();
				final String fullpath = lastChosenNode.getFullPath();
				String svn = control.getApplication().getRepositories()
						.getChosenRepo();

				Changes change = control.getChanges().get(svn + ":" + fullpath);
				if (change == null) {
					change = control.createChange(svn, fullpath);
				}
				change.reverseInheritance();

				if (change.isInheritance()) {
					thisBox.removeStyleName("boldCaption");
					thisBox.addStyleName("boldCaptionRed");
				} else {
					thisBox.removeStyleName("boldCaptionRed");
					thisBox.addStyleName("boldCaption");
				}
				if (thisBox.booleanValue()) {

					for (String s : Arrays.asList(((String) (Constants
							.getProperties().get("inheritance_free")))
							.split(";"))) {
						if (s.trim().length() != 0)
							if (!control.getApplication().getGroupsTable()
									.containsId(s)){
								control.addGroup(s,
										control.getRightsInheritanceFree(s,svn, fullpath));
								}
					}

				} else {
					for (String s : Arrays.asList(((String) (Constants
							.getProperties().get("inheritance_free")))
							.split(";"))) {
						if (s.trim().length() != 0)
							if (change.getGroup(s) != null)
								change.removeGroup(s,"");
					}
					control.getApplication().getGroupsTable().update();
				}
				control.cleanChanges(svn + ":" + fullpath);
				control.getApplication().getDirectories().refreshRowCache();

			}
		});
	}

	/**
	 * Updates the value of the checkbox according to the selected node.
	 */
	public void update() {
		logger.info("Updating inheritance checkbox");

		Node lastChosenNode = control.getApplication().getDirectories()
				.getLastChosenItemId();
		String fullpath;
		if (lastChosenNode == null)
			fullpath = "/";
		else
			fullpath = lastChosenNode.getFullPath();
		String svn = control.getApplication().getRepositories().getChosenRepo();

		// Node fromTree=control.getNode(svn,fullpath);

		// System.out.println("inheritance in node from tree: "+fromTree.isInheritance());

		Changes changes = control.getChanges().get(svn + ":" + fullpath);

		if (changes != null) {

			if (changes.isInheritance()) {
				this.setValue(!lastChosenNode.isInheritance());
				this.addStyleName("boldCaptionRed");
			} else {
				this.setValue(lastChosenNode.isInheritance());
				this.removeStyleName("boldCaptionRed");
				this.addStyleName("boldCaption");
			}
		} else {
			this.setValue(lastChosenNode.isInheritance());
			this.removeStyleName("boldCaptionRed");
			this.addStyleName("boldCaption");
		}
		// System.out.println("Set to " +
		// this.getValue()+" for: "+svn+":"+fullpath);
	}
}