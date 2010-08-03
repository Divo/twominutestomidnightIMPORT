package docketSystem;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.jopendocument.model.OpenDocument;
import org.jopendocument.panel.ODSViewerPanel;
import org.jopendocument.print.DefaultDocumentPrinter;

public class Interface extends JFrame {

	JPanel main;

	JMenuBar menu;
	JMenu file;

	JButton newDoc;

	Invoice invoice;
	NewDocForm newDocForm;
	
	Document document;

	public Interface() {
		initComponents();
	}

	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		main = new JPanel();
		initMenu();
		initPanel();
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(main,
				javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(main,
				javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE));

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		setBounds((screenSize.width - 410) / 2, (screenSize.height - 330) / 2,
				410, 330);

	}
	
	public void saveActionPreformed(){
		;
	}
	
	public void printActionPreformed(Invoice invoice){
		document = new Document(invoice);
		final OpenDocument doc = new OpenDocument();
		doc.loadFrom("src/data/invoice1.ods");
		

		// Show time !
		final JFrame mainFrame = new JFrame("Viewer");
		DefaultDocumentPrinter printer = new DefaultDocumentPrinter();
		

		ODSViewerPanel viewerPanel = new ODSViewerPanel(doc, printer, true);
		

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
		.getScreenSize();
		mainFrame.setContentPane(viewerPanel);
		mainFrame.pack();
		mainFrame.setLocation(screenSize.width /2, (screenSize.height)/2);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	private void initPanel() {
		newDoc = new JButton("new");
		newDoc.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newDocActionPerformed(evt);
			}
		});

		// I'm using netbeans to create GUI code, so it's gonna be shit....
		javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
		main.setLayout(mainLayout);
		mainLayout.setHorizontalGroup(mainLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				mainLayout.createSequentialGroup().addContainerGap()
						.addComponent(newDoc,
								javax.swing.GroupLayout.PREFERRED_SIZE, 66,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(322, Short.MAX_VALUE)));
		mainLayout.setVerticalGroup(mainLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				mainLayout.createSequentialGroup().addContainerGap()
						.addComponent(newDoc,
								javax.swing.GroupLayout.PREFERRED_SIZE, 68,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(201, Short.MAX_VALUE)));

	}

	private void newDocActionPerformed(java.awt.event.ActionEvent evt) {
		
		//SET THE DOC NUMBER IF IT IS USED
		//newInvoice = new Invoice();
		if (newDocForm != null)
			newDocForm = null;	//Just in case
		newDocForm = new NewDocForm(this);
		newDocForm.setVisible(true);
		

	}
	
	public void setInvoice(Invoice invoice){
		this.invoice = invoice;
		saveActionPreformed();
		//printActionPreformed(this.invoice);
	}
	
	
	
	
	
	
	public Invoice getInvoice(){
		return invoice;
	}

	private void initMenu() {
		menu = new JMenuBar();
		file = new JMenu();
		file.setText("File");
		menu.add(file);
		setJMenuBar(menu);
	}

}