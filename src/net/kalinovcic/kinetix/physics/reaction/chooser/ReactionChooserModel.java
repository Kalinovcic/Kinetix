package net.kalinovcic.kinetix.physics.reaction.chooser;

import java.util.HashSet;

import javax.swing.table.AbstractTableModel;

import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class ReactionChooserModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1438709488546412748L;
	
	public HashSet<Integer> enabledIndicies = new HashSet<Integer>();

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if (columnIndex == 0) return Boolean.class;
		return String.class;
	}
	
	@Override
	public int getColumnCount()
	{
		return 28;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		if (columnIndex == 0) return "Enabled";
		return Reaction.partName(columnIndex);
	}

	@Override
	public int getRowCount()
	{
		return Reactions.reactions.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0) return enabledIndicies.contains(rowIndex);
		return Reactions.reactions.get(rowIndex).partToString(columnIndex);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex == 0)
		{
		    if (enabledIndicies.contains(rowIndex))
		        enabledIndicies.remove(rowIndex);
		    else
		        enabledIndicies.add(rowIndex);
			fireTableDataChanged();
		}
		
		if (!(aValue instanceof String)) return;
		String value = (String) aValue;
		try
		{
			double v = Double.parseDouble(value);
			if (columnIndex == 15) Reactions.reactions.get(rowIndex).temperature = v;
			if (columnIndex == 24) Reactions.reactions.get(rowIndex).concentration1 = v / Reaction.cFactors[Reaction.cUnit];
			if (columnIndex == 25) Reactions.reactions.get(rowIndex).concentration2 = v / Reaction.cFactors[Reaction.cUnit];
			Reactions.reactions.get(rowIndex).recalculate();
			fireTableDataChanged();
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 0 || columnIndex == 16 || columnIndex == 24 || columnIndex == 25;
	}
}
