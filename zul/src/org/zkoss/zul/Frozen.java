/* Frozen.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 2, 2009 9:54:29 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 * @author jumperchen
 * @since 5.0.0
 */
public class Frozen extends XulElement {
	static {
		addClientEvent(Frozen.class, "onScrollPos", CE_DUPLICATE_IGNORE | CE_IMPORTANT); // only used in [ZK EE]
	}
	private int _columns;
	private int _start;
	/** maintain the number of the visible item in Paging mold. Only in smooth mold [ZK EE] */
	private int _currentLeft = 0;

	/**
	 * Sets the start position of the scrollbar.
	 * <p> Default: 0
	 * @param start the column number
	 */
	public void setStart(int start) {
		if (start < 0)
			throw new WrongValueException("Positive only");
		if (_start != start) {
			_start = start;
			smartUpdate("start", _start);
		}
	}

	/**
	 * Returns the start position of the scrollbar.
	 * <p>Default: 0
	 */
	public int getStart() {
		return _start;
	}

	/**
	 * Sets the number of columns to freeze.(from left to right)
	 * @param columns positive only
	 */
	public void setColumns(int columns) {
		if (columns < 0)
			throw new WrongValueException("Positive only");
		if (_columns != columns) {
			_columns = columns;
			smartUpdate("columns", _columns);
		}
	}

	/**
	 * Returns the number of columns to freeze.
	 * <p>Default: 0
	 */
	public int getColumns() {
		return _columns;
	}

	/**
	 * Sets the number of rows to freeze.(from top to bottom)
	 * 
	 * <p>Note: this feature is reserved and not yet implemented.
	 * @param rows positive only
	 */
	public void setRows(int rows) {
		throw new UnsupportedOperationException("Unsupported yet!"); //B70-ZK-2879
	}

	/**
	 * Returns the number of rows to freeze.
	 * <p>Default: 0
	 */
	public int getRows() {
		return 0;
	}

	/**
	 * Returns frozen is smooth or not.
	 * <p>Default: true
	 * @since 8.5.0
	 */
	public boolean isSmooth() {
		return Utils.testAttribute(this, "org.zkoss.zul.frozen.smooth", true, true);
	}

	public String getZclass() {
		return _zclass == null ? "z-frozen" : _zclass;
	}

	protected void updateByClient(String name, Object value) {
		if ("start".equals(name))
			setStart(value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(Objects.toString(value)));
		else
			super.updateByClient(name, value);
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (_columns > 0)
			renderer.render("columns", _columns);
		if (_columns > 0 && _start > 0)
			renderer.render("start", _start);

		//F85-ZK-3525: frozen support smooth mode (ee only)
		if (!isSmooth()) {
			renderer.render("smooth", false);
		} else {
			renderer.render("currentLeft", _currentLeft);
		}
	}

	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (isSmooth() && cmd.equals("onScrollPos")) {
			final Map<String, Object> data = request.getData();
			_currentLeft = AuRequests.getInt(data, "left", 0);
		} else
			super.service(request, everError);
	}
}
