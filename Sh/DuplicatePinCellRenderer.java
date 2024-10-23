import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Set;

class RowColorRenderer extends DefaultTableCellRenderer {
    private final Set<String> duplicatePins;

    public RowColorRenderer(Set<String> duplicatePins) {
        this.duplicatePins = duplicatePins;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (duplicatePins.contains(table.getValueAt(row, 1))) { // assuming card pin is in the 2nd column (index 1)
            cellComponent.setBackground(Color.RED);
        } else {
            cellComponent.setBackground(Color.WHITE);
        }
        return cellComponent;
    }
}