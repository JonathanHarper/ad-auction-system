import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Hashtable;

/**
 * Created by Group 20
 *
 * This shall display the key metrics to the user
 */
public class DashboardPanel extends JPanel {

	private JPanel mainContents;
	private CardLayout cardLayout;

	public double totalCost;
	public int conversions;
	public double CPA;
	public int bounces;
	public int impressions;
	public double CPI;
	public double CTR;
	public int clicks;
	public double CPC;
	public double BR;
	public int uniques;
	public DashboardFrame frame;

	private String cpaStr = "";
	private String cpiStr = "";
	private String cpcStr = "";
	private String tcStr = "";
	private String impStr = "";
	private String uniquesStr = "";
	private String tclickStr = "";
	private String ctrStr = "";
	private String bStr = "";
	private String brStr = "";
	private String convStr = "";

	DecimalFormat df = new DecimalFormat("#.##");


	JLabel dashboardTitle = new JLabel("Key Metrics");
	JLabel timeLabel = new JLabel("    Time:");
	TitledBorder titleBounceFilter = BorderFactory.createTitledBorder("Bounce Filtering - Adjust Bounces");
	JComboBox time = new JComboBox();
	JLabel pageLabel = new JLabel("Pages:");
	JComboBox page = new JComboBox();
	JButton applyButton = new JButton("Apply");
	TitledBorder titleImpressions = BorderFactory.createTitledBorder("Impressions Metrics");
	JLabel impressionsLbl = new JLabel("Impressions:      ");
	JLabel impressionsValue = new JLabel();
	JLabel uniquesLbl = new JLabel("Number of Uniques:		");
	JLabel uniquesValues= new JLabel();
	TitledBorder titleConversions = BorderFactory.createTitledBorder("Conversions Metrics");
	JLabel conversionsLbl = new JLabel("     Conversions:      ");
	JLabel conversionValue = new JLabel();
	TitledBorder titleBounce = BorderFactory.createTitledBorder("Bounce Metrics");
	JLabel bouncesLbl = new JLabel("Bounces:      ");
	JLabel bouncesValue = new JLabel();
	JLabel bounceRateLbl = new JLabel("Bounces Rate: ");
	JLabel bouncesRate = new JLabel();
	TitledBorder titleCost = BorderFactory.createTitledBorder("Cost Metrics");
	JLabel cpa = new JLabel("Cost Per Acquisition:      ");
	JLabel cpaValue = new JLabel();
	JLabel totalCostLabel = new JLabel("      Total Cost:      ");
	JLabel totalCostValue = new JLabel();
	JLabel cpi = new JLabel("Cost Per Impression:      ");
	JLabel cpiValue = new JLabel();
	JLabel cpc = new JLabel("Cost Per Click:      ");
	JLabel cpcValue = new JLabel();
	TitledBorder titleClick = BorderFactory.createTitledBorder("Click Metrics");
	JLabel totalClicks = new JLabel("Total Clicks:      ");
	JLabel totalClickValue = new JLabel();
	JLabel ctr = new JLabel("Click-Through-Rate:      ");
	JLabel ctrValue = new JLabel();


	/**
	 * @param mainContents  Contains all of the panels of the screens, allowing for efficient switching between them
	 * @param cardLayout    The layout manager used for the main screen content
	 */
	protected DashboardPanel(DashboardFrame frame, JPanel mainContents, CardLayout cardLayout) {
		this.mainContents = mainContents;
		this.cardLayout = cardLayout;
		this.frame = frame;
	}


	public void initDashboard(double totalCost, int conversions, double costPerAcquisition, 
			int bounces, int impressions, double costPerImpressions, double clickThroughRate, int clicks, double costPerClicks, 
			double bounceRate, int noOfUniques) {

		this.totalCost = totalCost;
		this.conversions = conversions;
		this.CPA = costPerAcquisition;
		this.bounces = bounces;
		this.impressions = impressions;
		this.CPI = costPerImpressions;
		this.CTR = clickThroughRate;
		this.clicks = clicks;
		this.CPC = costPerClicks;
		this.BR = bounceRate;
		this.uniques = noOfUniques;

		if (frame.filterButton == null) {
			frame.filterButton = new JButton(new ImageIcon("src/funnel.png"));
			frame.filterButton.setFocusPainted(false);
			frame.filterButton.setOpaque(false);
			frame.filterPanel.initFilterPanel();
			frame.pack();
			//Settings button implementation - opens up the settings
			frame.filterButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(mainContents, "Filter");
					frame.filterButton.setVisible(false);
				}
			});
			frame.toolBar.add(frame.filterButton);
		}

		addComponents();
	}

	//TEST
	protected DashboardPanel() {
		addComponents();
	}

	/**
	 * This populates the dashboard screen with the key metrics display and the title
	 */
	private void addComponents() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(20, 100, 50, 100);
		c.gridwidth = 2;

		dashboardTitle.setHorizontalAlignment(JLabel.CENTER);
		dashboardTitle.setFont(DashboardFrame.TITLE_TEXT);
		this.add(dashboardTitle, c);

		c.gridwidth = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 100, 10, 10);

		this.add(costPanel(), c);

		c.gridx = 1;
		c.insets = new Insets(0, 0, 10, 100);
		c.anchor = GridBagConstraints.NORTH;

		this.add(clickPanel(), c);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 100, 10, 10);
		//c.gridheight = 2;

		this.add(impressionsPanel(), c);


		//c.gridheight = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 100, 20, 10);

		this.add(bounceFilterPanel(), c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.insets = new Insets(0, 0, 10, 100);

		this.add(bouncesPanel(), c);

		c.gridy = 3;
		c.insets = new Insets(0, 0, 20, 100);

		this.add(conversionsPanel(), c);

		c.gridy = 4;
		c.gridx = 0;
		c.insets = new Insets(0, 100, 20, 100);
		c.gridwidth = 2;

		this.add(comparison(), c);

	}

	private JPanel comparison() {
		JPanel panel = new JPanel();

		panel.add(new JLabel("Select a Campaign to Compare "));

		DefaultComboBoxModel<String> model = StartScreenPanel.model;
		model.removeElement(frame.campaignPrevSelected);
		model.addElement("None");
		JComboBox selectCampaign = new JComboBox(model);
		panel.add(selectCampaign);

		JButton apply = new JButton("Apply");

		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectCampaign.getSelectedItem().equals("None")) {
					removeLbls();
				} else {
					changeLbls(selectCampaign);
				}
			}
		});

		panel.add(apply);

		return panel;
	}

	private void removeLbls() {
		cpaValue.setText("<html>"+"£"+df.format(CPA/100)+"<br/></html>");
		cpiValue.setText("<html>"+"£"+df.format(CPI/100)+"<br/></html>");
		cpcValue.setText("<html>"+"£"+df.format(CPC/100)+"<br/></html>");
		totalCostValue.setText("<html>"+"£"+df.format(totalCost*10)+"<br/></html>");
		impressionsValue.setText("<html>"+Integer.toString(impressions)+"<br/></html>");
		uniquesValues.setText("<html>"+Integer.toString(uniques)+"<br/></html>");
		totalClickValue.setText("<html>"+Integer.toString(clicks)+"<br/></html>");
		ctrValue.setText("<html>"+df.format(CTR*100)+"%"+"<br/></html>");
		bouncesValue.setText("<html>"+Integer.toString(bounces)+"<br/></html>");
		bouncesRate.setText("<html>"+df.format(BR*100)+"%"+"<br/></html>");
		conversionValue.setText("<html>"+Integer.toString(conversions)+"<br/></html>");
	}

	private void changeLbls(JComboBox selectCampaign) {
		Data newData = new Data(selectCampaign.getSelectedItem().toString(),null,null);
//
//		initDashboard(
//					//null, null refers to no filters having been applied
//					newData.getDatabase().getTotalCost(null, null),
//					newData.getDatabase().getNoOfConversions(null, null),
//					newData.getDatabase().getCPA(null, null),
//					newData.getDatabase().getNoOfBounces(null, null, null, null),
//					newData.getDatabase().getNoOfImpressions(null, null),
//					newData.getDatabase().getCPM(null, null),
//					newData.getDatabase().getCTR(null, null),
//					newData.getDatabase().getNoOfClicks(null, null),
//					newData.getDatabase().getCPC(null, null),
//					newData.getDatabase().getBounceRate(null, null, null, null),
//					newData.getDatabase().getNoOfUniques(null, null)
//		);

		cpaStr = "<font color=navy>"+"£"+df.format((newData.getDatabase().getCPA(null, null))/100)+"</font></html>";
		cpaValue.setText("<html>"+"£"+df.format(CPA/100)+"<br/>" + cpaStr);

		cpiStr = "<font color=navy>"+"£"+df.format((newData.getDatabase().getCPM(null, null))/100)+"</font></html>";
		cpiValue.setText("<html>"+"£"+df.format(CPI/100)+"<br/>" + cpiStr);

		cpcStr = "<font color=navy>"+"£"+df.format((newData.getDatabase().getCPC(null, null))/100)+"</font></html>";
		cpcValue.setText("<html>"+"£"+df.format(CPC/100)+"<br/>" + cpcStr);

		tcStr = "<font color=navy>"+"£"+df.format((newData.getDatabase().getTotalCost(null, null))*10)+"</font></html>";
		totalCostValue.setText("<html>"+"£"+df.format(totalCost*10)+"<br/>" + tcStr);

		impStr = "<font color=navy>"+newData.getDatabase().getNoOfImpressions(null, null)+"</font></html>";
		impressionsValue.setText("<html>"+Integer.toString(impressions)+"<br/>" + impStr);

		uniquesStr = "<font color=navy>"+newData.getDatabase().getNoOfUniques(null, null)+"</font></html>";
		uniquesValues.setText("<html>"+Integer.toString(uniques)+"<br/>" + uniquesStr);

		tclickStr = "<font color=navy>"+newData.getDatabase().getNoOfClicks(null, null)+"</font></html>";
		totalClickValue.setText("<html>"+Integer.toString(clicks)+"<br/>" + tclickStr);

		ctrStr = "<font color=navy>"+df.format((newData.getDatabase().getCTR(null, null))*100)+"%"+"</font></html>";
		ctrValue.setText("<html>"+df.format(CTR*100)+"%"+"<br/>" + ctrStr);

		tclickStr = "<font color=navy>"+newData.getDatabase().getNoOfBounces(null, null, null)+"</font></html>";
		bouncesValue.setText("<html>"+Integer.toString(bounces)+"<br/>" + bStr);

		brStr = "<font color=navy>"+df.format((newData.getDatabase().getBounceRate(null, null, null))*100)+"%"+"</font></html>";
		bouncesRate.setText("<html>"+df.format(BR*100)+"%"+"<br/>" + brStr);

		convStr = "<font color=navy>"+newData.getDatabase().getNoOfConversions(null, null)+"</font></html>";
		conversionValue.setText("<html>"+Integer.toString(conversions)+"<br/>" + convStr);

		frame.repaint();
	}

	private JPanel bounceFilterPanel(){
		Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
		ht.put("Any", 0);
		ht.put("≤ 30s", 30);
		ht.put("≤ 1m00s", 60);
		ht.put("≤ 1m30s", 90);
		ht.put("≤ 2m00s", 120);
		ht.put("≤ 2m30s", 150);
		ht.put("≤ 3m00s", 180);
		ht.put("≤ 3m30s", 210);
		ht.put("≤ 4m00s", 240);
		ht.put("≤ 4m30s", 270);
		ht.put("≤ 5m00s", 300);

		JPanel bounceP = new JPanel();
		bounceP.setLayout(new FlowLayout());
		//Setting the border
		titleBounceFilter.setTitleFont(DashboardFrame.LABELS_FONT);
		bounceP.setBorder(titleBounceFilter);

		JLabel help = new JLabel(new ImageIcon("src/question_icon.png"));
		help.setToolTipText("<html>Bounce filtering: Bounces can be filtered depending on the time spent on the website or on the number of pages visited</html>");
		bounceP.add(help);

		timeLabel.setFont(DashboardFrame.LABELS_FONT);

		time.setFont(DashboardFrame.LABELS_FONT);
		time.addItem("Any");
		time.addItem("≤ 30s");
		time.addItem("≤ 1m00s");
		time.addItem("≤ 1m30s");
		time.addItem("≤ 2m00s");
		time.addItem("≤ 2m30s");
		time.addItem("≤ 3m00s");
		time.addItem("≤ 3m30s");
		time.addItem("≤ 4m00s");
		time.addItem("≤ 4m30s");
		time.addItem("≤ 5m00s");

		pageLabel.setFont(DashboardFrame.LABELS_FONT);

		page.setFont(DashboardFrame.LABELS_FONT);
		page.addItem("Any");
		page.addItem(1);
		for(int i=2; i<=10; i++){
			page.addItem("≤ "+i);
		}
		page.setSelectedItem(1);

		time.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!time.getSelectedItem().equals("Any")){
					page.setSelectedItem("Any");
				} else {
					if (page.getSelectedItem().equals("Any")){
						page.setSelectedIndex(1);
					}
				}
			}
		});

		page.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!page.getSelectedItem().equals("Any")){
					time.setSelectedItem("Any");
				} else {
					if (time.getSelectedItem().equals("Any")){
						page.setSelectedIndex(1);
					}
				}

			}

		});

		bounceP.add(timeLabel);
		bounceP.add(time);
		bounceP.add(pageLabel);
		bounceP.add(page);

		applyButton.setFont(DashboardFrame.LABELS_FONT);
		applyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(page.getSelectedItem().equals("Any")){
					updateBounces(ht.get(time.getSelectedItem()),100000);
				} else {
					updateBounces(ht.get(time.getSelectedItem()),(Integer) page.getSelectedIndex());
				}


			}

		});
		bounceP.add(applyButton);

		return bounceP;
	}

	public void updateBounces(int t, int p){
		Integer time;
		Integer pages;
		if (t == 0){
			time = null;
		} else {
			time = t;
		}

		if (p == 11){
			pages = null;
		} else {
			pages = p;
		}

		cardLayout.show(mainContents, "Loading");

		BR = frame.data.getDatabase().getBounceRate(frame.filters, frame.values, frame.currentBounceDef);
		bounces = frame.data.getDatabase().getNoOfBounces(frame.values, frame.filters, frame.currentBounceDef);

		bouncesValue.setText("<html>"+Integer.toString(bounces)+"<br/>" + bStr);
		bouncesRate.setText("<html>"+df.format(BR*100)+"%"+"<br/>" + brStr);
		cardLayout.show(mainContents, "Metrics");
	}

	private JPanel impressionsPanel(){
		JPanel impressionsPanel = new JPanel();
		impressionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Setting the border
		titleImpressions.setTitleFont(DashboardFrame.LABELS_FONT);
		impressionsPanel.setBorder(titleImpressions);
		c.insets = new Insets(10, 10, 10, 20);

		c.gridheight = 2;
		JLabel help = new JLabel(new ImageIcon("src/question_icon.png"));
		help.setToolTipText("<html>Impressions: An impression occurs whenever an ad is shown to a user, regardless of whether they click on it<br/><br/>Uniques: The number of unique users that click on an ad during the course of a campaign</html>");
		impressionsPanel.add(help, c);

		c.insets = new Insets(10, 10, 10, 0);

		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;

		impressionsLbl.setFont(DashboardFrame.LABELS_FONT);
		impressionsPanel.add(impressionsLbl,c);

		c.gridx = 2;
		c.insets = new Insets(10, 0, 10, 0);
		impressionsValue.setFont(DashboardFrame.VALUES_FONT);
		//TESTING PURPOSES (tab included for stylisation)
		impressionsValue.setText("<html>"+Integer.toString(impressions)+"<br/>" + impStr);
//		impressionsValue.setText(Integer.toString(impressions));
		impressionsPanel.add(impressionsValue,c);

		c.gridx = 3;
		c.gridheight = 2;
		c.insets = new Insets(0, 20, 10, 10);

		JLabel mouse = new JLabel(new ImageIcon("src/mouse_icon.png"));
		//panel.add(mouse, c);

		c.gridheight = 1;
		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(10, 10, 10, 0);

		uniquesLbl.setFont(DashboardFrame.LABELS_FONT);
		impressionsPanel.add(uniquesLbl, c);

		c.gridx = 2;

		uniquesValues.setFont(DashboardFrame.VALUES_FONT);
		uniquesValues.setText("<html>"+Integer.toString(uniques)+"<br/>" + uniquesStr);
		impressionsPanel.add(uniquesValues, c);


		return impressionsPanel;
	}

	private JPanel conversionsPanel(){
		JPanel conversionPanel = new JPanel();
		conversionPanel.setLayout(new FlowLayout());
		//Setting the border
		titleConversions.setTitleFont(DashboardFrame.LABELS_FONT);
		conversionPanel.setBorder(titleConversions);

		JLabel help = new JLabel(new ImageIcon("src/question_icon.png"));
		help.setToolTipText("<html>Conversion: A conversion, or acquisition, occurs when a user clicks and then<br/>acts on an ad. The specific definition of an action depends on the campaign<br/>(e.g., buying a product, registering as a new customer or joining a mailing list)</html>");
		conversionPanel.add(help);

		conversionsLbl.setFont(DashboardFrame.LABELS_FONT);
		conversionPanel.add(conversionsLbl);

		conversionValue.setFont(DashboardFrame.VALUES_FONT);
		conversionValue.setText("<html>"+Integer.toString(conversions)+"<br/>" + convStr);
		conversionPanel.add(conversionValue);

		return conversionPanel;
	}

	private JPanel bouncesPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Setting the border
		titleBounce.setTitleFont(DashboardFrame.LABELS_FONT);
		panel.setBorder(titleBounce);

		c.insets = new Insets(10,10,10,20);

		c.gridheight = 2;
		JLabel help = new JLabel(new ImageIcon("src/question_icon.png"));
		help.setToolTipText("<html>Bounce: A user clicks on an ad, but then fails to interact with the<br/>website (typically detected when a user navigates away from<br/>the website after a short time, or when only a single page has been viewed)<br/><br/>Bounce Rate: The average number of bounces per click</html>");
		panel.add(help, c);
		c.gridheight = 1;

		c.insets = new Insets(10, 10, 10, 0);
		c.gridx = 1;

		bouncesLbl.setFont(DashboardFrame.LABELS_FONT);
		panel.add(bouncesLbl, c);

		c.gridx = 2;
		c.insets = new Insets(10, 0, 10, 0);
		bouncesValue.setFont(DashboardFrame.VALUES_FONT);
		//TESTING PURPOSES (tab included for stylisation)
		bouncesValue.setText("<html>"+Integer.toString(bounces)+"<br/>" + bStr);
		panel.add(bouncesValue, c);

		c.gridx = 3;
		c.gridheight = 2;
		c.insets = new Insets(0, 20, 10, 10);

		JLabel mouse = new JLabel(new ImageIcon("src/mouse_icon.png"));
		//panel.add(mouse, c);

		c.gridheight = 1;
		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(10, 10, 10, 0);

		bounceRateLbl.setFont(DashboardFrame.LABELS_FONT);
		panel.add(bounceRateLbl, c);

		c.gridx = 2;

		bouncesRate.setFont(DashboardFrame.VALUES_FONT);
		impressionsValue.setText("<html>"+Integer.toString(impressions)+"<br/>" + impStr);
		bouncesRate.setText(df.format(BR*100)+"%");
		panel.add(bouncesRate, c);


		return panel;
	}

	/**
	 * Displays metrics related to the expenditure of the campaign
	 */




	private JPanel costPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Setting the border
		titleCost.setTitleFont(DashboardFrame.LABELS_FONT);
		panel.setBorder(titleCost);

		c.insets = new Insets(10,10,10,20);

		c.gridheight = 2;
		JLabel help = new JLabel(new ImageIcon("src/question_icon.png"));
		help.setToolTipText("<html>Cost-per-acquisition (CPA): The average amount of money spent on an advertising<br/>campaign for each acquisition (i.e., conversion).<br/><br/>Cost per impression: The average amount of money spent on an advertising campaign<br/>for every impression<br/><br/>Cost-per-click (CPC): The average amount of money spent on an advertising campaign for each click<br/><br/>Total cost: The cost of the campaign in total</html>");
		panel.add(help, c);
		c.gridheight = 1;

		c.insets = new Insets(10, 10, 10, 0);
		c.gridx = 1;

		cpa.setFont(DashboardFrame.LABELS_FONT);
		panel.add(cpa, c);

		c.gridx = 2;

		cpaValue.setFont(DashboardFrame.VALUES_FONT);

		//TESTING PURPOSES (tab included for stylisation)
		cpaValue.setText("<html>"+"£"+df.format(CPA/100)+"<br/>" + cpaStr);

		c.insets = new Insets(10, 0, 10, 10);

		panel.add(cpaValue, c);

		c.gridx = 3;
		c.insets = new Insets(10, 0, 10, 0);

		totalCostLabel.setFont(DashboardFrame.LABELS_FONT);
		panel.add(totalCostLabel, c);

		c.gridx = 4;

		totalCostValue.setFont(DashboardFrame.VALUES_FONT);

		//TESTING PURPOSES (tab included for stylisation)
		totalCostValue.setText("<html>"+"£"+df.format(totalCost*10)+"<br/>" + tcStr);
		c.insets = new Insets(10, 0, 10, 10);

		panel.add(totalCostValue, c);

		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(0, 10, 10, 0);

		cpi.setFont(DashboardFrame.LABELS_FONT);
		panel.add(cpi, c);

		c.gridx = 2;
		c.insets = new Insets(0, 0, 10, 0);

		cpiValue.setFont(DashboardFrame.VALUES_FONT);

		cpiValue.setText("<html>"+"£"+df.format(CPI/100)+"<br/>" + cpiStr);

		panel.add(cpiValue, c);

		c.gridx = 3;
		c.gridwidth = 2;
		c.gridheight = 2;

		JLabel calc = new JLabel(new ImageIcon("src/calculator_icon.png"));
		panel.add(calc, c);

		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridy = 2;
		c.gridx = 1;
		c.insets = new Insets(0, 10, 10, 0);

		cpc.setHorizontalAlignment(JLabel.LEFT);
		cpc.setFont(DashboardFrame.LABELS_FONT);
		panel.add(cpc, c);

		c.gridx = 2;
		c.insets = new Insets(0, 0, 10, 0);

		cpcValue.setFont(DashboardFrame.VALUES_FONT);
		cpcValue.setHorizontalAlignment(JLabel.LEFT);
		//TESTING PURPOSES (tab included for stylisation)

		cpcValue.setText("<html>"+"£"+df.format(CPC/100)+"<br/>" + cpcStr);

		panel.add(cpcValue, c);

		return panel;
	}

	/**
	 * Displays the metrics related to the clicks
	 */
	private JPanel clickPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Setting the border
		titleClick.setTitleFont(DashboardFrame.LABELS_FONT);
		panel.setBorder(titleClick);

		c.insets = new Insets(10,10,10,20);

		c.gridheight = 2;
		JLabel help = new JLabel(new ImageIcon("src/question_icon.png"));
		help.setToolTipText("<html>Click: A click occurs when a user clicks on an ad that is shown to them<br/><br/>Click-through-rate (CTR): The average number of clicks per impression</html>");
		panel.add(help, c);
		c.gridheight = 1;

		c.insets = new Insets(10, 10, 10, 0);
		c.gridx = 1;
		totalClicks.setFont(DashboardFrame.LABELS_FONT);
		panel.add(totalClicks, c);

		c.gridx = 2;
		c.insets = new Insets(10, 0, 10, 0);

		totalClickValue.setFont(DashboardFrame.VALUES_FONT);

		totalClickValue.setText("<html>"+Integer.toString(clicks)+"<br/>" + tclickStr);

		panel.add(totalClickValue, c);

		c.gridx = 3;
		c.gridheight = 2;
		c.insets = new Insets(0, 20, 10, 10);

		JLabel mouse = new JLabel(new ImageIcon("src/mouse_icon.png"));
		panel.add(mouse, c);

		c.gridheight = 1;
		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(10, 10, 10, 0);

		ctr.setFont(DashboardFrame.LABELS_FONT);
		panel.add(ctr, c);

		c.gridx = 2;

		ctrValue.setFont(DashboardFrame.VALUES_FONT);

		ctrValue.setText("<html>"+df.format(CTR*100)+"%"+"<br/>" + ctrStr);
		c.insets = new Insets(10, 0, 10, 10);

		panel.add(ctrValue, c);

		return panel;
	}

	public void setFonts (Font normal, Font values, Font titles){
		dashboardTitle.setFont(titles);
		timeLabel.setFont(normal);
		titleBounceFilter.setTitleFont(normal);
		time.setFont(normal);
		pageLabel.setFont(normal);
		page.setFont(normal);
		applyButton.setFont(normal);
		titleImpressions.setTitleFont(normal);
		impressionsLbl.setFont(normal);
		impressionsValue.setFont(values);
		uniquesLbl.setFont(normal);
		uniquesValues.setFont(values);
		titleConversions.setTitleFont(normal);
		conversionsLbl.setFont(normal);
		conversionValue.setFont(values);
		titleBounce.setTitleFont(normal);
		bouncesLbl.setFont(normal);
		bouncesValue.setFont(values);
		bounceRateLbl.setFont(normal);
		bouncesRate.setFont(values);
		titleCost.setTitleFont(normal);
		cpa.setFont(normal);
		cpaValue.setFont(values);
		totalCostLabel.setFont(normal);
		totalCostValue.setFont(values);
		cpi.setFont(normal);
		cpiValue.setFont(values);
		cpc.setFont(normal);
		cpcValue.setFont(values);
		titleClick.setTitleFont(normal);
		totalClicks.setFont(normal);
		totalClickValue.setFont(values);
		ctr.setFont(normal);
		ctrValue.setFont(values);	
	}












}
