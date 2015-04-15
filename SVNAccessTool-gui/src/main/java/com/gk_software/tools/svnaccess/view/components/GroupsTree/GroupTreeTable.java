package com.gk_software.tools.svnaccess.view.components.GroupsTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.data.ldap.LdapProvider;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;

/**
 * Represents the group tree in the GUI when adding a group. Allows filtering.
 */
public class GroupTreeTable extends TreeTable {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(GroupTreeTable.class);

	/** The container for this class. */
	private GroupTreeTableContainer container;

	/** The instance of this class. */
	private GroupTreeTable thisDirectories = this;

	/** The name of the name property. */
	protected static final String NAME_PROPERTY = "name";

	/** The name of the parent group. */
	private String parentName;

	/**
	 * Creates a new instance and initializes all the variables.
	 */
	public GroupTreeTable() {
		logger.info("Group tree table component initialization");
		this.setSelectable(true);
		this.setNullSelectionAllowed(false);
		this.setImmediate(true);
		this.setSizeFull();
		this.addContainerProperty(NAME_PROPERTY, String.class, "");
		this.addListener(new Tree.ExpandListener() {

			public void nodeExpand(ExpandEvent event) {
				GroupBean node = (GroupBean) event.getItemId();
				if (node != null) {
					while (node.getParent() != null) {
						node = node.getParent();
						thisDirectories.setCollapsed(node, false);
					}
				}
			}
		});

		this.setCellStyleGenerator(new TreeTable.CellStyleGenerator() {
			@Override
			public String getStyle(Object itemId, Object propertyId) {
				if (propertyId == null) {
					// Styling for row
					BeanItem<GroupBean> item = (BeanItem<GroupBean>) thisDirectories
							.getItem(itemId);
					boolean selected = Boolean.parseBoolean(item
							.getItemProperty("selected").toString());
					if (selected) {
						return "highlight-green-group";
					}
				}
				return null;
			}
		});

		logger.info("Creating new container");
	}

	/**
	 * Initializes the components and creates the parent group with the given name.
	 *
	 * @param parentName the name of the parent group.
	 *
	 */
	public void init(String parentName) {
		GroupBean parent = new GroupBean(parentName);
		this.parentName = parentName;
		container = new GroupTreeTableContainer();
		addChildren(parent, true, container); // add root elements
		buildTrees((List<GroupBean>) container.rootItemIds()); // build a tree

		this.setMultiSelect(false);
		this.setContainerDataSource(container);
		this.setVisibleColumns(new String[] { NAME_PROPERTY });
		this.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);
	}

	/**
	 * Initializes the components.
	 */
	public void init() {
		// GroupBean parent = new GroupBean("@DEPT_Organisation");
		// GroupBean parent2 = new GroupBean("@DEPT_O-StS");
		container = new GroupTreeTableContainer();

		List<String> l = LdapProvider.getInstance().findRoots();
		for (String g : l) {
			GroupBean parent = new GroupBean("@" + g);
			container.addBean(parent);
			addChildren(parent, false, container); // add root elements
		}

		buildTrees((List<GroupBean>) container.rootItemIds()); // build a tree

		this.setMultiSelect(true);
		this.setContainerDataSource(container);
		this.setVisibleColumns(new String[] { NAME_PROPERTY });
		this.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);

	}

	/**
	 * Adds the children of the given parent to his group tree.
	 *
	 * @param parent the parent whose children are added
	 * @param isRoot the indicator if the parent is root
	 * @param cont the container to which are the children added
	 *
	 */
	public void addChildren(GroupBean parent, boolean isRoot,
			GroupTreeTableContainer cont) {
		String parentName = "";
		if (parent.getName().startsWith("@"))
			parentName = parent.getName().substring(1);

		Set<String> childrenH = LdapProvider.getInstance().getGroupsUsers()
				.get(parentName);
		if (childrenH == null)
			childrenH = new HashSet<String>();
		List<String> children = new ArrayList<String>(childrenH);
		Collections.sort(children);

		parent.setChildren(new ArrayList<GroupBean>());

		for (String g : children) {
			GroupBean gg = new GroupBean(g);
			cont.addBean(gg);
			if (!isRoot) {
				gg.setParent(parent);
				parent.addChild(gg);
			}
		}
	}

	/**
	 * Builds the group trees of all the given root groups.
	 *
	 * @param roots the root groups of the tree
	 *
	 */
	public void buildTrees(List<GroupBean> roots) {
		Stack<GroupBean> stack = new Stack<GroupBean>();
		stack.addAll(roots);
		while (!stack.isEmpty()) {
			GroupBean ch = stack.pop();
			addChildren(ch, false, container);
			stack.addAll(ch.getChildren());
		}
	}

	/**
	 * Filters the groups in the tree by the given text
	 *
	 * @param text the searched text
	 *
	 */
	public void filter(String text) {
		logger.info("Collecting all nodes containing: " + text);
		text = text.toLowerCase();
		unselectAll(container);
		if (text.length() == 0) {
			this.setContainerDataSource(container);
			this.setVisibleColumns(new String[] { NAME_PROPERTY });
			this.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);
			return;
		}
		List<GroupBean> roots = (List<GroupBean>) container.rootItemIds();
		List<String> foundAll = new ArrayList<String>();
		// find all paths that contains text
		for (GroupBean g : roots) {
			foundAll.addAll(search(g, text));
		}

		GroupTreeTableContainer container2 = new GroupTreeTableContainer();
		GroupBean parent = new GroupBean(parentName);
		buildFilteredTree(foundAll, parent, container2);
	}

	/**
	 * Finds all paths that contain the given text from the root group.
	 *
	 * @param root the root group of the tree structure
	 * @param text the searched text
	 *
	 * @return the list of all the paths
	 *
	 */
	public List<String> search(GroupBean root, String text) {
		Stack<GroupBean> stack = new Stack<GroupBean>();
		stack.add(root);
		List<String> found = new ArrayList<String>();
		while (!stack.isEmpty()) {
			GroupBean ch = stack.pop();
			if (ch.getName().toLowerCase().contains(text)) {
				ch.setSelected(true);
				found.add(ch.getFullGroupPath());
				logger.info("Path contains text: " + text + " founded in: "
						+ ch.getFullGroupPath());
			}
			stack.addAll(ch.getChildren());
		}
		return found;
	}

	/**
	 * Builds the filtered tree.
	 *
	 * @param foundPaths the list of the filtered paths
	 * @param parent the root group
	 * @param cont2 the newly filled container
	 *
	 */
	private void buildFilteredTree(List<String> foundPaths, GroupBean parent,
			GroupTreeTableContainer cont2) {
		List<GroupBean> found2 = new ArrayList<GroupBean>();
		for (String g : foundPaths) {
			String[] path = g.split("/");
			GroupBean last = null;
			for (int i = 0; i < path.length; i++) {
				GroupBean p = new GroupBean(path[i]);
				if (i == (path.length - 1)) {
					p.setSelected(true);
				}
				if (i == 0) {
					if (!parent.containsChildren(p)) {
						cont2.addBean(p);
						parent.addChild(p);
					}
					last = parent.getCh(p);
				} else {

					if (!last.containsChildren(p)) {
						cont2.addBean(p);
						p.setParent(last);
						last.addChild(p);

						if (i == (path.length - 1)) {
							found2.add(p);
						}
					}
					last = last.getCh(p);
				}
			}
		}
		logger.info("Setting filtrated container to the component");
		this.setContainerDataSource(cont2);
		this.setVisibleColumns(new String[] { NAME_PROPERTY });
		this.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);

		for (GroupBean g : found2) {
			this.setCollapsed(g.getParent(), false);
		}
	}

	/**
	 * Unselects and collapses all the items that were selected.
	 */
	public void unselectAll(GroupTreeTableContainer cont) {
		Stack<GroupBean> stack = new Stack<GroupBean>();
		stack.addAll(cont.rootItemIds());
		while (!stack.isEmpty()) {
			GroupBean ch = stack.pop();
			ch.setSelected(false);
			this.setCollapsed(ch, true);
			stack.addAll(ch.getChildren());
		}
		thisDirectories.refreshRowCache();
		thisDirectories.validate();
	}
}
