package docketSystem;

/*
 * 
 * ****************************
 * INVOICE = DOCKET in my own crazy mind
 * **************************** 
 */
import java.sql.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jopendocument.model.OpenDocument;
import org.jopendocument.panel.ODSViewerPanel;
import org.jopendocument.print.DefaultDocumentPrinter;

/**
 * This appears to have become the main class where everything is handled. It's
 * (original) purpose was to draw the main window of the application. Now it
 * ~Stores and manages the connection to the mySQL database ~Launches the
 * NewDocForm window ~Passes the newly created Invoice from NewDocForm. It then
 * opens the finished document in a print window ~Writes the newly created
 * Invoice to the database
 * 
 * @author divo
 * 
 */
public class Interface extends JFrame {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String LITE_DRIVER = "org.sqlite.JDBC";
	static final String DATABASE_URL = "jdbc:mysql://localhost/trimtransport";
	static final String LITEURL = "jdbc:sqlite:trim.db";
	static final String USERNAME = "trim";
	static final String PASSWORD = "truck";
	static final String DEFAULT_QUERY = "SELECT docket.Docket_Number, docket.Date_, addresses.address, addresses1.address,"
			+ " docket.Description, docket.Seal, docket.Customer, docket.Equipment, docket.Return_Empty, haz.Name, haz.UN_Number, docket.Size_, docket.Weight"
			+ " FROM docket LEFT JOIN addresses ON docket.Deliver_to=addresses.ID LEFT"
			+ " JOIN addresses AS addresses1 ON docket.Collect_From=addresses1.ID"
			+ " LEFT JOIN haz ON docket.Haz=haz.ID";
	
	static final String TERM = ";";

	private JPanel main;

	private JMenuBar menu;

	private JButton newDoc;

	private Invoice invoice;
	private NewDocForm newDocForm;

	private Document document;
	private JComponent stuff[];

	private javax.swing.JButton closeButton;
	private javax.swing.JButton editButton;
	private javax.swing.JMenu file;
	private javax.swing.JMenu edit;
	private javax.swing.JMenuBar jMenuBar1;

	private ResultSetTableModel tableModel;
	private JScrollPane tableScroll;

	private Connection connection;

	private javax.swing.JButton searchButton;
	JTable resultTable;

	public Interface() {
		initComponents();
	}

	private void initComponents() {

		main = new javax.swing.JPanel();
		newDoc = new javax.swing.JButton();
		searchButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();
		editButton = new javax.swing.JButton();
		jMenuBar1 = new javax.swing.JMenuBar();
		file = new javax.swing.JMenu();
		edit = new javax.swing.JMenu();
		tableScroll = new JScrollPane();

		// JTable for database of doc
		try {
			/*Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DATABASE_URL, USERNAME,
					PASSWORD);
			tableModel = new ResultSetTableModel(JDBC_DRIVER, connection,
					DEFAULT_QUERY + TERM);*/
			Class.forName(LITE_DRIVER);
			connection = DriverManager.getConnection(LITEURL);
			tableModel = new ResultSetTableModel(LITE_DRIVER, connection, DEFAULT_QUERY + TERM);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultTable = new JTable(tableModel);
		

		tableScroll.setViewportView(resultTable);
		// End jtable

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		newDoc.setText("New");
		newDoc.setIcon(new ImageIcon("data/resources/new.png"));
		newDoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		newDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		newDoc.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newDocActionPerformed(evt);
			}
		});

		searchButton.setText("Find");
		searchButton.setIcon(new ImageIcon("data/resources/search.png"));
		searchButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		searchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		searchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				searchButttonActionPerformed();
			}
		});

		closeButton.setText("Close");
		closeButton.setIcon(new ImageIcon("data/resources/close.png"));
		closeButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed();
			}
		});
		
		editButton.setText("Edit");
		editButton.setIcon(new ImageIcon("data/resources/edit.png"));
		editButton
			.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		editButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				editButtonActionPerformed();
			}
		});
		
		

		javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
		main.setLayout(mainLayout);
		mainLayout
				.setHorizontalGroup(mainLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mainLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												mainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																newDoc,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																66,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																searchButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																66,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																editButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																66,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																closeButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																66,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												tableScroll,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												402, Short.MAX_VALUE)
										.addContainerGap()));
		mainLayout
				.setVerticalGroup(mainLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mainLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												mainLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																tableScroll,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																249,
																Short.MAX_VALUE)
														.addGroup(
																mainLayout
																		.createSequentialGroup()
																		.addComponent(
																				newDoc,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				68,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				searchButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				68,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				editButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				68,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				closeButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				68,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		file.setText("File");
		jMenuBar1.add(file);

		edit.setText("Edit");
		jMenuBar1.add(edit);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(main,
				javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(main,
				javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		setBounds((screenSize.width - 514) / 2, (screenSize.height - 322) / 2,
				750, 500);
		setFonts();
	}

	/**
	 * Insert an invoice object into the sql database.
	 */
	public int saveActionPreformed() {
		// First check to see if the addresses are already present
		System.out.println("Saved action");
		int collectID, deliverID, hazID = 0;

		collectID = insertAddress(invoice.getFrom());
		deliverID = insertAddress(invoice.getTo());
		if (invoice instanceof InvoiceHaz) {
			hazID = insertHaz();
		}

		return insertDocket(collectID, deliverID, hazID);

		// String insert = new String("INSERT INTO docket ")

	}
	
	public void updateActionPreformed(String id){
		System.out.println("Updating record");
		int collectID, deliverID, hazID = 0;
		//False flag stops the weight from being updated if address is unchanged
		collectID = insertAddress(invoice.getFrom(), false);
		deliverID = insertAddress(invoice.getTo(), false);
		if (invoice instanceof InvoiceHaz) {
			hazID = insertHaz();
		}
		//Worry about the haz later i guess
		
		updateDocket(collectID, deliverID, hazID, id);
		
		
	}
	
	public void updateDocket(int collectID, int deliverID, int hazID, String ID){
		Statement statement;
		ResultSet rs;
		try{
			statement = connection.createStatement();
			String query = new String(
					"UPDATE docket SET Equipment='"
					+ invoice.getEqupNo()
					+ "', Customer='"
					+ invoice.getCustomerRefer()
					+ "', Seal='"
					+ invoice.getSeal()
					+ "', Description='"
					+ invoice.getDescript()
					+ "', Berth='"
					+ invoice.getBerth()
					+ "', Weight='"
					+ invoice.getWeight()
					+ "', Size_='"
					+ invoice.getSize()
					+ "', Return_Empty='"
					+ invoice.getReturnEmpty()
					+ "', Deliver_to='"
					+ deliverID
					+ "', Collect_from='"
					+ collectID
					+ "', Haz='"
					+ hazID
					+ "' WHERE Docket_Number='"
					+ ID
					+ "';"
					);
			//System.out.println(query);
			statement.executeUpdate(query);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		setDefaultQuery(); //Be lazy for the moment
		
	}

	public int insertDocket(int collectID, int deliverID, int hazID) {
		Statement statement;
		ResultSet resultSet;
		try {
			statement = connection.createStatement();
			statement
					.executeUpdate(new String(
							"INSERT INTO docket (Equipment, Customer, Seal, Description, Berth, Weight, Size_, Return_Empty, Deliver_to, Collect_from, Haz, Date_) values ('"
									+ invoice.getEqupNo()
									+ "', '"
									+ invoice.getCustomerRefer()
									+ "', '"
									+ invoice.getSeal()
									+ "', '"
									+ invoice.getDescript()
									+ "', '"
									+ invoice.getBerth()
									+ "', '"
									+ invoice.getWeight()
									+ "', '"
									+ invoice.getSize()
									+ "', '"
									+ invoice.getReturnEmpty()
									+ "', '"
									+ deliverID
									+ "', '"
									+ collectID
									+ "', '"
									+ hazID
									+ "', '"
									+ invoice.getTime()
									+ "');"));

			resultSet = statement.executeQuery("SELECT Docket_Number from docket where ");
			resultSet.next();

			tableModel.setQuery(DEFAULT_QUERY + TERM);

			return resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * The same as insertAddress. Checks if something matching the haz spec is
	 * in the database. If it is, use that, else write the new one in
	 * 
	 * @return SQL ID of haz entry
	 */
	public int insertHaz() {
		int result = 0;
		ResultSet resultSet;
		Statement statement;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(new String(
					"SELECT * FROM haz WHERE Name = '"
							+ ((InvoiceHaz) invoice).getName()
							+ "' AND UN_Number = '"
							+ ((InvoiceHaz) invoice).getUnNo()
							+ "' AND Primary_Class = '"
							+ ((InvoiceHaz) invoice).getClass1() + "'"));
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			} else {
				statement
						.executeUpdate(new String(
								"INSERT INTO haz (Name, UN_Number, Packing_Group, Primary_Class, Secondary_Class, Tunnel_code) values ('"
										+ ((InvoiceHaz) invoice).getName()
										+ "', '"
										+ ((InvoiceHaz) invoice).getUnNo()
										+ "', '"
										+ ((InvoiceHaz) invoice).getPg()
										+ "', '"
										+ ((InvoiceHaz) invoice).getClass1()
										+ "', '"
										+ ((InvoiceHaz) invoice).getClass2()
										+ "', '"
										+ ((InvoiceHaz) invoice).getTunnel()
										+ "');"));
				resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
				resultSet.next();
				result = resultSet.getInt(1);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		}

		return result;
	}
	

	public int insertAddress(String address){
		return insertAddress(address, true);
	}
	public int insertAddress(String address, boolean update) {
		ResultSet resultSet;
		Statement statement;
		int result = 0;
		try {
			statement = connection.createStatement();
			// Check the collect from address
			resultSet = statement.executeQuery(new String(
					"SELECT * FROM addresses WHERE Address = \"" + address
							+ "\""));
			// Presume the first object found is the address, as each is unique
			if (resultSet.next()) {
				result = resultSet.getInt(1);
				// If found, store its id and add 1 to its weight
				if(update){
					
					statement.executeUpdate(new String(
							"UPDATE addresses SET weight=" + ((resultSet.getInt(3)) + 1)
							+ " WHERE ID='" + result + "';"));
				}
			}// Else insert the new address into addresses
			else {
				// Insert into address
				statement.executeUpdate(new String(
						"INSERT INTO addresses (Address, weight) values ('"
								+ address + "', '1');"));
				// Getting the number of rows would probably be sufficent to
				// determine the new ID, but an entry might get
				// deleted...maybe...prehaps not, anyway, get the ID.
				resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
				resultSet.next();
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		}

		return result;
	}

	public void setFonts() {
		stuff = new JComponent[] { newDoc, searchButton, closeButton, file,
				edit };
		for (JComponent n : stuff) {
			n.setFont(new java.awt.Font("Dialog", 0, 11));
		}
	}

	private int getMaxID() {
		ResultSet resultSet;
		Statement statement;
		int result = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT max(Docket_Number) FROM docket");
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void printActionPreformed(Invoice invoice) {
		document = new Document(invoice);
		final OpenDocument doc = new OpenDocument();
		doc.loadFrom("data/invoice1.ods");

		// Show time !
		final JFrame mainFrame = new JFrame("Viewer");
		DefaultDocumentPrinter printer = new DefaultDocumentPrinter();

		ODSViewerPanel viewerPanel = new ODSViewerPanel(doc, printer, true);

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		mainFrame.setContentPane(viewerPanel);
		mainFrame.pack();
		mainFrame.setLocation(screenSize.width / 2, (screenSize.height) / 2);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	private void newDocActionPerformed(java.awt.event.ActionEvent evt) {

		// SET THE DOC NUMBER IF IT IS USED
		// newInvoice = new Invoice();
		if (newDocForm != null)
			newDocForm = null; // Just in case
		newDocForm = new NewDocForm(this);
		newDocForm.setVisible(true);

	}

	private void searchButttonActionPerformed() {
		SearchForm search = new SearchForm(this);
		search.setVisible(true);
	}

	private void closeButtonActionPerformed() {
		this.setVisible(false);
		this.dispose();
		//exit(0); ??
	}
	
	private void editButtonActionPerformed(){
		int row = resultTable.getSelectedRow();
		if(row != -1){
			//Pull selected row from backend
			int id = (Integer) resultTable.getValueAt(row, 0); //Modifying the default query may break this, badly
			String query = "SELECT docket.Docket_Number, docket.Date_, addresses.address, addresses1.address,"
                        + " docket.Description, docket.Seal, docket.Customer, docket.Equipment, docket.Return_Empty, haz.Name, haz.UN_Number, docket.Size_, docket.Weight, docket.Berth"
                        + " FROM docket LEFT JOIN addresses ON docket.Deliver_to=addresses.ID LEFT"
                        + " JOIN addresses AS addresses1 ON docket.Collect_From=addresses1.ID"
                        + " LEFT JOIN haz ON docket.Haz=haz.ID WHERE docket.Docket_Number=" + Integer.toString(id);
			//System.out.println(query);
			try {
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);
				resultSet.next();
				
				//This is going to be long and tedious, pull data out of resultSet and feed it to EditDocForm
				//ArrayList <String>list = new ArrayList();
				String[] list = new String[14]; //Ok, now we are getting hacky
				for(int i = 1; i <= 14; i++ ){
					list[(i-1)] = resultSet.getString(i);
					//System.out.println(resultSet.getString(i));
				}
				
				if (newDocForm != null)
					newDocForm = null; // Just in case
				newDocForm = new EditDocForm(this, list );
				newDocForm.setVisible(true);
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Shove it into subclassed NewDocForm
			
			//Save any changes
		}else{ //User has not selected a row
			
		}
	}

	/**
	 * Set the invoice object and return it's ID. (Messy, but needed by
	 * NewDocForm / Document to render to the invoice)
	 * 
	 * @param invoice
	 * @return ID of invoice in SQL database, also used as the invoice number
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
		//invoice.setDocNo(getMaxID() + 1); // Set the docket number as the
		// previous most recent invoice
		//return saveActionPreformed();
		
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public Connection getConnection() {
		
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void setQuery(String where){
		try {
			//System.out.println(DEFAULT_QUERY + where + TERM);
			tableModel.setQuery(DEFAULT_QUERY + where + TERM);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDefaultQuery(){
		try {
			tableModel.setQuery(DEFAULT_QUERY + TERM);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * private void initMenu() { menu = new JMenuBar(); file = new JMenu();
	 * file.setText("File"); menu.add(file); setJMenuBar(menu); }
	 */

}
