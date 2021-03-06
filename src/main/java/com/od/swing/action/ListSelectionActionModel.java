/**
 * Copyright (C) 2012 (nick @ objectdefinitions.com)
 *
 * This file is part of Object Definitions od-swing.
 *
 * od-swing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * od-swing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with od-swing.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.swing.action;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-May-2009
 * Time: 10:31:46
 */
public class ListSelectionActionModel<E> extends AbstractActionModel {

    private LinkedHashSet<E> selectedSeries = new LinkedHashSet<E>();
    private List<E> selectedAsList;

    public void setSelected(E... series) {
        setSelected(Arrays.asList(series));
    }

    public void setSelected(List<? extends E> series) {
        selectedAsList = Collections.unmodifiableList(series);
        selectedSeries.clear();
        selectedSeries.addAll(series);
        setValidityAndFireStateChange();
    }

    public void addSelected(E series) {
        selectedAsList = null;
        selectedSeries.add(series);
        setValidityAndFireStateChange();
    }

    public void removeSelected(E series) {
        selectedAsList = null;
        selectedSeries.remove(series);
        setValidityAndFireStateChange();
    }

    public void setSelected(E series){
        selectedAsList = null;
        selectedSeries.clear();
        selectedSeries.add(series);
        setValidityAndFireStateChange();
    }

    private void setValidityAndFireStateChange() {
        setValidity();
        fireActionStateUpdated();
    }

    public List<E> getSelected() {
        return getSelectedAsList();
    }

    private List<E> getSelectedAsList() {
        if ( selectedAsList == null) {
            selectedAsList = Collections.unmodifiableList(new ArrayList<E>(selectedSeries));
        }
        return selectedAsList;
    }

    private void setValidity() {
        setModelValid(selectedSeries.size() > 0);
    }

    protected void doClearActionModelState() {
        selectedAsList = null;
        selectedSeries.clear();
    }

    public int getSelectionCount() {
        return selectedSeries.size();
    }
}
