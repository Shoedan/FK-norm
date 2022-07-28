import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JToolBar;

public class MumoJamboDELETEAFTER {
	
	
	public static void main(String[] args) throws SQLException, IOException {
		File file = new File("D:/Fac/AN3/SGDB/SQLite/GUI/SQLiteStudio/SQLiteStudio.exe");
		Desktop.getDesktop().open(file);

	}
}
