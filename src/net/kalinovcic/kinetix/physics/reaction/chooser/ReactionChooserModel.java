package net.kalinovcic.kinetix.physics.reaction.chooser;

import javax.swing.table.AbstractTableModel;

import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class ReactionChooserModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1438709488546412748L;
	
	public int enabledIndex = 0;

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
		return Reactions.getReactions().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0) return rowIndex == enabledIndex;
		return Reactions.getReactions().get(rowIndex).partToString(columnIndex);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex == 0)
		{
			enabledIndex = rowIndex;
			fireTableDataChanged();
		}
		
		if (!(aValue instanceof String)) return;
		String value = (String) aValue;
		try
		{
			double v = Double.parseDouble(value);
			if (columnIndex == 15) Reactions.getReactions().get(rowIndex).temperature = v;
			if (columnIndex == 23) Reactions.getReactions().get(rowIndex).concentration1 = v / Reaction.cFactors[Reaction.cUnit];
			if (columnIndex == 24) Reactions.getReactions().get(rowIndex).concentration2 = v / Reaction.cFactors[Reaction.cUnit];
			Reactions.getReactions().get(rowIndex).recalculate();
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
