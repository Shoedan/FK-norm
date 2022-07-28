import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.sqlite.SQLiteException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.*;

public class GUI{
	FileDialog dbsqlite;
	static Connection conn = null;

	
	
	public GUI() {
		JFrame f = new JFrame();
		dbsqlite = new FileDialog(f,"Alegeti",FileDialog.LOAD);
		dbsqlite.setDirectory("D:\\Fac\\Anul 2\\Java Codes\\Normalization of Foreign Keys\\Resources");
		
		f.setVisible(true);
		f.setSize(450,450);
		f.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Normalizare chei straine");
		lblNewLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(28, 46, 360, 23);
		f.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("SGBD : SQLite");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(53, 80, 102, 14);
		f.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Tech : Java");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(267, 80, 102, 14);
		f.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Stancu Nicolae-Tiberiu");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(102, 106, 212, 14);
		f.getContentPane().add(lblNewLabel_3);
		
		JButton btnNewButton = new JButton("Alegeti Baza de Date");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbsqlite.setVisible(true);
				String aux = dbsqlite.getFile();
				if (connect(aux))
				f.setVisible(false);
				
			}
		});
		btnNewButton.setBounds(102, 180, 212, 23);
		f.getContentPane().add(btnNewButton);
		
	}
	
	
	
	public static void options() throws SQLException {
		JFrame f = new JFrame();
		f.setVisible(true);
		f.setSize(450,250);
		f.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Optiuni pentru verificarea problemelor bazei de date selectate.");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 12));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(25, 26, 380, 27);
		f.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Verifica ca toate cheile straine sa fie numerice.");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					first_option();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				f.setVisible(false);			
			}
		});
		btnNewButton.setBounds(40, 93, 351, 23);
		f.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Vizualizeaza baza de date");
		btnNewButton_1.setBounds(40, 127, 351, 23);
		f.getContentPane().add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					view_table();
				} catch (SQLException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	
		JButton btnNewButton_2 = new JButton("Verifica sa nu existe dangling points");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				third_option();
				f.setVisible(false);			
			}
		});
		btnNewButton_2.setBounds(40, 161, 351, 23);
		f.getContentPane().add(btnNewButton_2);
		
	}
	
	
	
	public static void first_option() throws SQLException {
		boolean made_changes = false;
		String sql = "SELECT \r\n"
				+ "    name\r\n"
				+ "FROM \r\n"
				+ "    sqlite_schema\r\n"
				+ "WHERE \r\n"
				+ "    type ='table' AND \r\n"
				+ "    name NOT LIKE 'sqlite_%'";
		String []table_names = new String[20];							/// Need to find a workaround
		Statement stmt , stmt2 , stmt3 , stmt4;
		int increment = 0;
		try {
			stmt = conn.createStatement();																	// SEARCH ALL FOREIGN KEYS IN TABLE
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				table_names[increment] = rs.getString(1);
				increment++;}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		for (int i=0;i<increment;i++) {																		// ADD AUX INTEGER COLUMN
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list("+table_names[i]+");");
			while (rs.next()) {
				stmt2 = conn.createStatement();
				boolean duplicates = verif_duplicates(rs.getString(3));
				ResultSet rs2 = stmt2.executeQuery("PRAGMA table_info("+rs.getString(3)+");");			// RSGETSTRING3 IS REF TABLE
				while (rs2.next()) {
					if (rs2.getString(2).equals(rs.getString(5)) && !rs2.getString(3).equals("INTEGER")) {	// RS2GETSTRING3 IS TYPE OF THE REF TABLE P KEY RS2GETSTRING2 IS COLUMN NAME
						if (duplicates == false) {
						sql = "ALTER TABLE " + rs.getString(3) + " ADD COLUMN AUXFOREIGNID INTEGER";
						made_changes = true;
						stmt3 = conn.createStatement();
						stmt3.executeUpdate(sql);
						stmt4 = conn.createStatement();
						ResultSet rs3 = stmt4.executeQuery("SELECT * FROM " + rs.getString(3) + ";");
						populate_aux_columns(rs3,rs.getString(3));
						add_foreign_aux_and_populate(table_names[i],rs.getString(4),rs.getString(3),rs2.getString(2));
						// FIND WAY TO CHECK FOR DUPLICATE COLUMN
					}
						else {
							add_foreign_aux_and_populate(table_names[i],rs.getString(4),rs.getString(3),rs2.getString(2));
							made_changes = true;
						}
					}	
											
				}
				}
			}
		Message_opt_1(made_changes);
		}
	
	
	
	public static boolean verif_duplicates(String table_name) throws SQLException {
		String sql = "SELECT name FROM pragma_table_info('" + table_name + "');";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next())
			if (rs.getString(1).equals("AUXFOREIGNID"))
				return true;
		return false;
		
	}
	
	
	
	public static void add_foreign_aux_and_populate(String table_name , String column_name , String ref_key , String column_name_ref) throws SQLException {
		String sql = "ALTER TABLE " + table_name + " ADD COLUMN " + column_name + "FIX INTEGER REFERENCES " + ref_key + "(AUXFOREIGNID);";
		System.out.println(table_name);
		Statement stmt;
		ResultSet rs;
		stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		ArrayList<String> postoid = new ArrayList<String>();
		sql = "SELECT " + column_name_ref + " FROM " + ref_key + " ORDER BY ROWID ASC"; 
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
			postoid.add(rs.getString(1));}
		PreparedStatement pst = conn.prepareStatement("UPDATE " + table_name + " SET " + column_name + "FIX = ? WHERE " + column_name + " = ?");
		for (int i=1;i<=postoid.size();i++) {
			pst.setInt(1,i);
			pst.setString(2,postoid.get(i-1));
			pst.executeUpdate();
		}
		sql = "ALTER TABLE " + table_name + " DROP COLUMN " + column_name;
		stmt.executeUpdate(sql);
		}
	
	
	
	public static void populate_aux_columns(ResultSet rs , String table_name) throws SQLException {
		int count = 1;
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + table_name + " SET AUXFOREIGNID = ? WHERE ROWID = ?");
		while (rs.next()) {
			stmt.setInt(1,count);
			stmt.setInt(2,count);
			stmt.executeUpdate();
			count++;
		}
	}
	
	
	
	public static void Message_opt_1(boolean check) {
		JFrame f = new JFrame();
		f.setVisible(true);
		f.setSize(450,250);
		f.getContentPane().setLayout(null);
		JLabel lblNewLabel ;
		if (check)
			lblNewLabel = new JLabel("Au fost gasite chei straine pe atribute non umerice!");
		else
			lblNewLabel = new JLabel("Nu au fost gasite chei straine pe atribute non umerice!");
		
		f.getContentPane().setLayout(null);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(20, 22, 386, 22);
		f.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Back to options");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					options();
					f.setVisible(false);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(136, 120, 150, 23);
		f.getContentPane().add(btnNewButton);
	}
	
	
	
	public static void third_option() {
		JFrame f = new JFrame();
		f.setVisible(true);
		f.setSize(450,250);
		f.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Alegeti tabelul si coloana pe care doriti sa o verificati");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(28, 21, 375, 14);
		f.getContentPane().add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(28, 62, 115, 22);
		f.getContentPane().add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(28, 118, 115, 22);
		f.getContentPane().add(comboBox_1);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(250, 62, 115, 22);
		f.getContentPane().add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setBounds(250, 118, 115, 22);
		f.getContentPane().add(comboBox_3);
		
		JLabel lblNewLabel_1 = new JLabel("Foreign Key Table");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(38, 46, 93, 14);
		f.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Foreign Key Column");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(28, 95, 115, 14);
		f.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Referenced Table");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(250, 46, 115, 14);
		f.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Referenced Column");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(250, 95, 115, 14);
		f.getContentPane().add(lblNewLabel_4);
		
		
		String sql = "SELECT \r\n"
				+ "    name\r\n"
				+ "FROM \r\n"
				+ "    sqlite_schema\r\n"
				+ "WHERE \r\n"
				+ "    type ='table' AND \r\n"
				+ "    name NOT LIKE 'sqlite_%'";
		String []table_names = new String[20];							/// Need to find a workaround
		Statement stmt ;
		int increment = 0;
		try {
			stmt = conn.createStatement();																	// SEARCH ALL FOREIGN KEYS IN TABLE
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				table_names[increment] = rs.getString(1);
				increment++;}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		comboBox.setModel(new DefaultComboBoxModel(table_names));
		comboBox_2.setModel(new DefaultComboBoxModel(table_names));
		comboBox_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sqlaux = "PRAGMA table_info(" + comboBox.getSelectedItem() + ");";
				String []column_names = new String[20];
				Statement stmt2;
				int incaux = 0;
				try {
					stmt2 = conn.createStatement();
					ResultSet rsaux = stmt2.executeQuery(sqlaux);
					while (rsaux.next()) {
						column_names[incaux] = rsaux.getString(2);
						incaux++;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				comboBox_1.setModel(new DefaultComboBoxModel(column_names));
			}
		});
		comboBox_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sqlaux = "PRAGMA table_info(" + comboBox_2.getSelectedItem() + ");";
				String []column_names = new String[20];
				Statement stmt2;
				int incaux = 0;
				try {
					stmt2 = conn.createStatement();
					ResultSet rsaux = stmt2.executeQuery(sqlaux);
					while (rsaux.next()) {
						column_names[incaux] = rsaux.getString(2);
						incaux++;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				comboBox_3.setModel(new DefaultComboBoxModel(column_names));
			}
		});
		
		JButton btnNewButton = new JButton("Verifica");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					verif_dangling_points(comboBox.getSelectedItem().toString(), comboBox_1.getSelectedItem().toString() , comboBox_2.getSelectedItem().toString() , comboBox_3.getSelectedItem().toString());
					f.setVisible(false);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(158, 177, 89, 23);
		f.getContentPane().add(btnNewButton);
		
	}
	
	
	
	public static void verif_dangling_points(String table_name_1 , String column_name_1 , String table_name_2 , String column_name_2) throws SQLException {
		ArrayList<Integer> main = new ArrayList<Integer>();
		ArrayList<Integer> secondary = new ArrayList<Integer>();
		String sql = "SELECT " + column_name_1 + " FROM " + table_name_1;
		String sql2 = "SELECT " + column_name_2 + " FROM " + table_name_2;
		Statement stmt1 , stmt2;
		stmt1 = conn.createStatement();
		stmt2 = conn.createStatement();
		ResultSet rs = stmt1.executeQuery(sql);
		ResultSet rs2 = stmt2.executeQuery(sql2);
		while(rs.next()) 
			main.add(rs.getInt(1));
		while (rs2.next()) 
			secondary.add(rs2.getInt(1));
		if (secondary.containsAll(main)) 
			JOptionPane.showMessageDialog(null,"Nu exista dangling points");
			else
				JOptionPane.showMessageDialog(null,"Exista dangling points");
		options();
				
		
	}
	
	
	
	public static boolean connect(String fileName) throws HeadlessException {
		
		try {
		String url = "jdbc:sqlite:D:/Fac/Anul 2/Java Codes/Normalization of Foreign Keys/Resources/" + fileName;
		conn = DriverManager.getConnection(url);
		if (check_connectivity()) {
			System.out.println("Connected");
			options();
			return true;}
		else {
			JOptionPane.showMessageDialog(null,"Opened file is not an sqlite Database");
			return false;}
		}catch(SQLException e) {
			return false;}	
	}
	
	
	
	public static boolean check_connectivity(){
		try {String sql = "SELECT \r\n"
				+ "    name\r\n"
				+ "FROM \r\n"
				+ "    sqlite_schema\r\n"
				+ "WHERE \r\n"
				+ "    type ='table' AND \r\n"
				+ "    name NOT LIKE 'sqlite_%'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		}catch (SQLException e) {return false;}
		
		return true;
		
	}
	
	public static void view_table() throws SQLException, IOException {
		File file = new File("D:/Fac/AN3/SGDB/SQLite/GUI/SQLiteStudio/SQLiteStudio.exe");
		Desktop.getDesktop().open(file);
		
	}
	
}
