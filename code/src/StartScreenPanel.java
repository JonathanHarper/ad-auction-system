import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class StartScreenPanel extends JPanel {

	DashboardFrame frame;
	CardLayout cardLayout;
	JPanel mainContents;
	DashboardPanel metricsScreen;

	private static final ArrayList<String> NULL_FILTER_ARRAY = new ArrayList(Arrays.asList("*"));

	private String pathClick = "";
	private String pathServer ="" ;
	private String pathImpression = "";

	//Marks where the last destination of choosing a log was
	private String prevDest = "";


	TitledBorder newCampaignBorder = BorderFactory.createTitledBorder("Add New Campaign");

	JLabel title = new JLabel("Ad Dashboard");
	JLabel selectCampaign = new JLabel("Choose Existing Campaign");
	JComboBox selectCampaignCombo = new JComboBox();
	JButton loadCampaign = new JButton("Load Campaign");
	JLabel select = new JLabel("Select:"); ////BOLD
	JLabel serverLog = new JLabel("Server Log");
	JButton serverChooser = new JButton("Browse...");
	JLabel clickLog = new JLabel("Click Log");
	JButton clickChooser = new JButton("Browse...");
	JLabel impressionLog = new JLabel("Impression Log");
	JButton impressionChooser = new JButton("Browse...");
	JButton addCampaign = new JButton("Add Campaign");
	JLabel nameCampaign = new JLabel("Campaign name:");
	JTextField edit = new JTextField();

	public static DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();


	StartScreenPanel ssp = this;




	protected Data data;

	SettingsPanel settings;

	private JPanel startPanel = new JPanel();

	/**
	 * @param frame    The JFrame in which the panel appears
	 */
	protected StartScreenPanel(DashboardFrame frame, JPanel mainContents ,CardLayout cardLayout, DashboardPanel metricsScreen, Data d, SettingsPanel settings) {
		this.frame = frame;
		this.cardLayout = cardLayout;
		this.mainContents = mainContents;
		this.metricsScreen = metricsScreen;
		this.data = d;
		this.settings = settings;
		addComponents();
		data = frame.data;

		frame.pack();
	}

	/**
	 * Populates the start screen
	 *
	 * @return  Panel containing the contents of the start screen
	 */
	private void addComponents() {
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.insets = new Insets(50, 0, 50, 0);

		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(DashboardFrame.TITLE_TEXT);

		this.add(title, c);

		c.insets = new Insets(0, 10, 20, 10);
		c.gridwidth = 1;
		c.gridy = 1;

		selectCampaign.setFont(DashboardFrame.NORMAL_TEXT);
		selectCampaign.setVerticalTextPosition(SwingConstants.CENTER);
		this.add(selectCampaign, c);

		c.gridx = 1;
		c.insets = new Insets(0, 10, 20, 10);

		selectCampaignCombo.setFont(DashboardFrame.NORMAL_TEXT);
		//Searches for a folder called 'campaigns' which is populated with all of the existing campaign data
		
		selectCampaignCombo.setModel(model);

		File folder = new File("campaigns");
		if(folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			for (File f : listOfFiles) {
				if (!f.getName().equals(".DS_Store") && (model.getIndexOf(f.getName()) == -1)){
					model.addElement(f.getName());
				}
			}
		} else {
			new File("campaigns").mkdir();
		}

		this.add(selectCampaignCombo, c);


		initPanel(selectCampaignCombo);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.insets = new Insets(0,0,0,0);
		this.add(startPanel, c);

		c.insets = new Insets(20,0,20,0);
		c.gridy = 3;

		loadCampaign.setFont(DashboardFrame.NORMAL_TEXT);


		loadCampaign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				cardLayout.show(mainContents, "Loading");

				prevDest = "";

				System.out.println(mainContents.getSize());
				mainContents.setVisible(false);
				JPanel load = DashboardFrame.loading();

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						Data newData = new Data(selectCampaignCombo.getSelectedItem().toString(),null,null);

						if(!(frame.campaignPrevSelected.equals(selectCampaignCombo.getSelectedItem().toString()))) {
							System.out.println("test1");
							frame.campaignPrevSelected = selectCampaignCombo.getSelectedItem().toString();
							frame.data = newData;
							//Removes everything from the metrics screen (for back button purposes)
							metricsScreen.removeAll();
							metricsScreen.initDashboard(
									//null, null refers to no filters having been applied
									newData.getDatabase().getTotalCost(null, null),
									newData.getDatabase().getNoOfConversions(null, null),
									newData.getDatabase().getCPA(null, null),
									newData.getDatabase().getNoOfBounces(null, null, "pages_1"),
									newData.getDatabase().getNoOfImpressions(null, null),
									newData.getDatabase().getCPM(null, null),
									newData.getDatabase().getCTR(null, null),
									newData.getDatabase().getNoOfClicks(null, null),
									newData.getDatabase().getCPC(null, null),
									newData.getDatabase().getBounceRate(null, null, "pages_1"),
									newData.getDatabase().getNoOfUniques(null, null)
									);

							settings.setData(newData);

						}
						System.out.println("test2");

						//frame.getSettings().setPrevious("Metrics");
						DashboardFrame.previous = "Metrics";
						mainContents.setVisible(true);
						cardLayout.show(mainContents, "Metrics");
						frame.backButton.setVisible(true);
						frame.filterButton.setVisible(true);
						frame.settingsButton.setVisible(true);
					}});

				//If the frame is not fullscreen
				if(!(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)) {
					frame.pack();
					frame.setLocationRelativeTo(null);
				}
			}
		});

		this.add(loadCampaign, c);
	}

	/**
	 * Creates the 'Add New Campaign' section
	 */
	public void initPanel(JComboBox selectCampaign){
		startPanel.setLayout(new GridBagLayout());
		newCampaignBorder.setTitleFont(DashboardFrame.NORMAL_TEXT);
		startPanel.setBorder(newCampaignBorder);

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;

		//JLabel addCampaign = new JLabel("Add New Campaign");
		//addCampaign.setFont(AdAuctionMain.NORMAL_TEXT);
		//addCampaign.setVerticalTextPosition(JLabel.CENTER);
		//startPanel.add(addCampaign, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;

		c.insets = new Insets(0, 10, 5, 0);

		Font bold = new Font(DashboardFrame.NORMAL_TEXT.getName(), Font.BOLD, 
				DashboardFrame.NORMAL_TEXT.getSize());
		select.setFont(bold);
		startPanel.add(select, c);

		c.gridx = 1;

		c.insets = new Insets(0,100,5,10);

		serverLog.setHorizontalAlignment(SwingConstants.RIGHT);
		serverLog.setFont(DashboardFrame.NORMAL_TEXT);
		startPanel.add(serverLog, c);

		c.gridx = 2;
		c.insets = new Insets(0,0,5,10);

		serverChooser.setFont(DashboardFrame.NORMAL_TEXT);

		serverChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				c.setDialogTitle("Server Log Selection");
				c.showOpenDialog(null);
				if(!prevDest.equals("")) {
					c.setCurrentDirectory(new File(prevDest));
				}
				try {
					if(c.getSelectedFile().getAbsolutePath().endsWith(".csv")) {
						pathServer = c.getSelectedFile().getAbsolutePath();
						String[] path = pathServer.split("/");

						serverLog.setText("Server Log: "+path[path.length-1]);
					} else {
						JOptionPane.showMessageDialog(null, "Please select an appropriate CSV file for the server file.", "Warning",  JOptionPane.ERROR_MESSAGE);
					}

					System.out.print("Path:");
					System.out.println(c.getSelectedFile().getAbsolutePath());
					prevDest = c.getCurrentDirectory().toString();
				} catch(NullPointerException e1) {}
			}
		});
		startPanel.add(serverChooser, c);

		c.gridy = 2;
		c.gridx = 1;
		c.insets = new Insets(0,100,5,10);


		clickLog.setHorizontalAlignment(SwingConstants.RIGHT);
		clickLog.setFont(DashboardFrame.NORMAL_TEXT);
		startPanel.add(clickLog, c);

		c.gridx = 2;

		c.insets = new Insets(0,0,5,10);

		clickChooser.setFont(DashboardFrame.NORMAL_TEXT);

		clickChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				c.setDialogTitle("Click Log Selection");
				c.showOpenDialog(null);
				try {
					if (c.getSelectedFile().getAbsolutePath().endsWith(".csv")) {
						pathClick = c.getSelectedFile().getAbsolutePath();
						String[] path = pathClick.split("/");

						clickLog.setText("Click Log: " + path[path.length - 1]);
					} else {
						JOptionPane.showMessageDialog(null, "Please select an appropriate CSV file for the click file.", "Warning", JOptionPane.ERROR_MESSAGE);
					}
					System.out.print("Path:");
					System.out.println(c.getSelectedFile().getAbsolutePath());
				} catch(NullPointerException e2) {}
			}
		});
		startPanel.add(clickChooser, c);

		c.gridy = 3;
		c.gridx = 1;
		c.insets = new Insets(0,100,20,10);

		impressionLog.setHorizontalAlignment(SwingConstants.RIGHT);
		impressionLog.setFont(DashboardFrame.NORMAL_TEXT);
		startPanel.add(impressionLog, c);

		c.gridx = 2;
		c.insets = new Insets(0,0,20,10);


		impressionChooser.setFont(DashboardFrame.NORMAL_TEXT);

		impressionChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				c.setDialogTitle("Impression Log Selection");
				c.showOpenDialog(null);
				try {
					if (c.getSelectedFile().getAbsolutePath().endsWith(".csv")) {
						pathImpression = c.getSelectedFile().getAbsolutePath();
						String[] path = pathImpression.split("/");

						impressionLog.setText("Impression Log: " + path[path.length - 1]);
					} else {
						JOptionPane.showMessageDialog(null, "Please select an appropriate CSV file for the click file.", "Warning", JOptionPane.ERROR_MESSAGE);
					}
					System.out.print("Path:");
					System.out.println(c.getSelectedFile().getAbsolutePath());
				} catch(NullPointerException e3) {}
			}
		});
		startPanel.add(impressionChooser, c);

		c.gridx = 0;
		c.gridy = 4;

		c.insets = new Insets(0,10,20,10);

		nameCampaign.setFont(DashboardFrame.NORMAL_TEXT);
		nameCampaign.setHorizontalAlignment(SwingConstants.RIGHT);
		startPanel.add(nameCampaign, c);

		c.gridx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(0,0,20,10);

		edit.setFont(DashboardFrame.NORMAL_TEXT);
		edit.setDocument(new RestrictLength(20));
		startPanel.add(edit, c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		c.insets = new Insets(0, 10, 10, 10);


		addCampaign.setFont(DashboardFrame.NORMAL_TEXT);


		addCampaign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				cardLayout.show(mainContents, "Loading");

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						try {
							System.out.println(pathClick);
							if(pathClick == null || pathImpression == null ||  pathServer == null || 
									pathClick.equals("") || pathImpression.equals("") || pathServer.equals("")) {
								JOptionPane.showMessageDialog(null, "Please ensure that all logs have been selected.", "Warning", JOptionPane.ERROR_MESSAGE);
								throw new IOException("Must specify file paths.");
							} else {
								if (edit.getText().trim().length() < 1) {
									JOptionPane.showMessageDialog(null, "Please select an appropriate title for the campaign.", "Warning", JOptionPane.ERROR_MESSAGE);
									throw new IOException();
								} else {
									//								new File("campaigns").mkdir();
									//								BufferedWriter bw = new BufferedWriter(new FileWriter("campaigns/" + edit.getText() + ".txt"));
									//								bw.write(edit.getText() + "\n");
									//								bw.write(pathClick + "\n");
									//								bw.write(pathServer + "\n");
									//								bw.write(pathImpression + "\n");
									//								bw.close();

									data = new Data(edit.getText().trim(), pathClick, pathServer, pathImpression);
									File folder = new File("campaigns");
									if(folder.exists()) {
										File[] listOfFiles = folder.listFiles();
										for (File f : listOfFiles) {
											if (!f.getName().equals(".DS_Store") && (model.getIndexOf(f.getName()) == -1)){
												model.addElement(f.getName());
											}
										}
									} else {
										new File("campaigns").mkdir();
									}

								}
							}

						} catch (IOException e1) {
							System.err.println("Cant write to file");
						}

						cardLayout.show(mainContents, "Start Screen");
					}
				});


			}
		});
		startPanel.add(addCampaign, c);
	}


	/**
	 * Restricts the length of the text box so that the user is unable to input a string which is more than the specified length
	 *
	 * Source: http://stackoverflow.com/questions/13075564/limiting-length-of-input-in-jtextfield-is-not-working
	 * From user 474189, Duncan
	 */
	public final class RestrictLength extends PlainDocument {

		private final int limit;

		public RestrictLength(int limit) {
			this.limit = limit;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offs, str, a);
			}
		}
	}

	public void panelRepaint(){
		this.repaint();
	}

	public void setFonts(Font normal, Font titleFont){

		newCampaignBorder.setTitleFont(normal);

		title.setFont(titleFont);
		selectCampaign.setFont(normal);
		selectCampaignCombo.setFont(normal);
		loadCampaign.setFont(normal);
		edit.setFont(normal);

		Font bold = new Font(normal.getName(), Font.BOLD, 
				normal.getSize());

		select.setFont(bold); ////BOLD
		serverLog.setFont(normal);
		serverChooser.setFont(normal);
		clickLog.setFont(normal);
		clickChooser.setFont(normal);
		impressionLog.setFont(normal);
		impressionChooser.setFont(normal);
		addCampaign.setFont(normal);
		nameCampaign.setFont(normal);
	}

	public void loading(boolean b) {
//		newCampaignBorder.set(b);
		edit.setVisible(b);
	}

}