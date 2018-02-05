

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Group 20
 */
public class FilterPanel extends JPanel {

	public String previous;
	public DashboardFrame frame;
	private Database d;

	private JComboBox<Font> selectFont;
	private JComboBox selectFontSize;

	JCheckBox checkAnyDate;

	UtilDateModel startModel;
	UtilDateModel endModel;

	JCheckBox checkAnyContext;
	JCheckBox checkNews;
	JCheckBox checkShopping;
	JCheckBox checkSocial;
	JCheckBox checkBlog;
	JCheckBox checkHobbies;
	JCheckBox checkTravel;

	JCheckBox checkAnyAge;
	JCheckBox checkLess25;
	JCheckBox check2534;
	JCheckBox check3544;
	JCheckBox check4554;
	JCheckBox checkGreat55;

	JCheckBox checkAnyIncome;
	JCheckBox checkLow;
	JCheckBox checkMedium;
	JCheckBox checkHigh;

	JCheckBox checkAnyGender;
	JCheckBox checkMale;
	JCheckBox checkFemale;

	Date chosenStartDate;
	Date chosenEndDate;
	String chosenDate = "";
	
	

	JLabel genderLabel = new JLabel("Gender");
	JLabel filterTitle = new JLabel("Filtering");
	JLabel context = new JLabel("Context");
	JLabel contextDate = new JLabel("Date");
	JLabel ageLabel = new JLabel("Age");
	JLabel incomeLabel = new JLabel("Income");
	JButton apply = new JButton("Apply");
	JButton cancel = new JButton("Cancel");
	

	TitledBorder titleAudience = BorderFactory.createTitledBorder("Audience");
	TitledBorder titleContext = BorderFactory.createTitledBorder("Context");
	TitledBorder titleDate = BorderFactory.createTitledBorder("Date");


	JPanel mainContents;
	CardLayout cardLayout;

	public FilterPanel(DashboardFrame frame, JPanel mainContents, CardLayout cardLayout){
		//initFilterPanel(frame, mainContents, cardLayout);
		this.frame = frame;
		this.cardLayout = cardLayout;
		this.mainContents = mainContents;
	}

	protected void initFilterPanel() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(50, 100, 50, 100);

		filterTitle.setHorizontalAlignment(JLabel.CENTER);
		filterTitle.setFont(DashboardFrame.TITLE_TEXT);
		this.add(filterTitle, c);

		c.gridy = 1;
		c.insets = new Insets(0, 100, 20, 100);

		JPanel context = contextPanel();
		JPanel date = datePanel();
		JPanel aud =audiencePanel();
		this.add(date , c);

		c.gridy = 2;

		this.add(context, c);

		c.gridy = 3;

		this.add(aud, c);

		//Apply and cancel JButtons
		JPanel applyCancelBtns = new JPanel();
		applyCancelBtns.setLayout(new FlowLayout());

		c.gridy = 4;

		apply.setPreferredSize(new Dimension(100,25));
		apply.setFont(DashboardFrame.NORMAL_TEXT);
		apply.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> filters = new ArrayList<String>();
				ArrayList<String> values = new ArrayList<String>();
				if (!checkAnyDate.isSelected()){
					if (endModel.getValue() != null){
						if (startModel.getValue() != null){
							chosenEndDate = endModel.getValue();
							chosenStartDate = startModel.getValue();
							chosenDate = new SimpleDateFormat("yyyy-MM-dd 12:00:00").format(chosenStartDate);
							chosenDate += "_" + new SimpleDateFormat("yyyy-MM-dd 12:00:00").format(chosenEndDate);
							filters.add("date");
							values.add(chosenDate);
						}
					}
				}
				if (!checkAnyContext.isSelected()){
					String val = "";
					ArrayList<String> vals = new ArrayList<String>();
					if(checkNews.isSelected()){
						vals.add("News");
					}
					if(checkShopping.isSelected()){
						vals.add("Shopping");						
					}
					if(checkSocial.isSelected()){
						vals.add("Social Media");
					}
					if(checkBlog.isSelected()){
						vals.add("Blog");
					}
					if(checkHobbies.isSelected()){
						vals.add("Hobbies");
					}
					if(checkTravel.isSelected()){
						vals.add("Travel");
					}
					for (String s : vals){
						if (vals.indexOf(s) == 0){
							filters.add("context");
							val  = s;
						} else {
							val += "_"+s;
						}
					}
					if (!val.equals(""))
						values.add(val);
				}
				if(!checkAnyAge.isSelected()){
					String val = "";
					ArrayList<String> vals = new ArrayList<String>();

					if(checkLess25.isSelected()){
						vals.add("<25");
					}
					if(check2534.isSelected()){
						vals.add("25-34");
					}
					if(check3544.isSelected()){
						vals.add("35-44");
					}
					if(check4554.isSelected()){
						vals.add("45-54");
					}
					if(checkGreat55.isSelected()){
						vals.add(">55");
					}
					for (String s : vals){
						if (vals.indexOf(s) == 0){
							filters.add("age");
							val  = s;
						} else {
							val += "_"+s;
						}
					}
					if (!val.equals(""))
						values.add(val);
				}
				if(!checkAnyIncome.isSelected()){
					String val = "";
					ArrayList<String> vals = new ArrayList<String>();
					if(checkLow.isSelected()){
						vals.add("L");
					}
					if(checkMedium.isSelected()){
						vals.add("M");
					}
					if(checkHigh.isSelected()){
						vals.add("H");
					}
					for (String s : vals){
						if (vals.indexOf(s) == 0){
							filters.add("income");
							val  = s;
						} else {
							val += "_"+s;
						}
					}
					if (!val.equals(""))
						values.add(val);
				}

				if(!checkAnyGender.isSelected()){
					String val = "";
					ArrayList<String> vals = new ArrayList<String>();

					if(checkMale.isSelected()){
						vals.add("M");
					} else {
						vals.add("F");
					}
					for (String s : vals){
						if (vals.indexOf(s) == 0){
							filters.add("gender");
							val  = s;
						} else {
							val += "_"+s;
						}
					}
					if (!val.equals(""))
						values.add(val);
				}

				//Removes everything from the metrics screen (for back button purposes)

				frame.filterButton.setVisible(true);

				cardLayout.show(mainContents, "Loading");

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){


						frame.currentFilters = filters;
						frame.filterValues = values;

						if(frame.data.getDatabase().getTotalCost(filters, values) != 0){
							//cardLayout.show(mainContents, "Loading");

							frame.dashboardPanel.removeAll();

							frame.filters = filters;
							frame.values = values;

							frame.dashboardPanel.initDashboard(
									//null, null refers to no filters having been applied
									frame.data.getDatabase().getTotalCost(filters,values),
									frame.data.getDatabase().getNoOfConversions(filters,values),
									frame.data.getDatabase().getCPA(filters,values),
									frame.data.getDatabase().getNoOfBounces(filters,values, frame.currentBounceDef),
									frame.data.getDatabase().getNoOfImpressions(filters,values),
									frame.data.getDatabase().getCPM(filters,values),
									frame.data.getDatabase().getCTR(filters,values),
									frame.data.getDatabase().getNoOfClicks(filters,values),
									frame.data.getDatabase().getCPC(filters,values),
									frame.data.getDatabase().getBounceRate(filters,values, frame.currentBounceDef),
									frame.data.getDatabase().getNoOfUniques(filters,values)
									);


							DashboardFrame.previous = "Metrics";
							cardLayout.show(mainContents, "Metrics");
						} else {
							cardLayout.show(mainContents, "Filter");
							JOptionPane.showMessageDialog(null, "Incorrect Filtering", "Error", JOptionPane.ERROR_MESSAGE);
						}

					}
				});
				
				


				//				frame.getSettings().setPrevious("Metrics");

				//If the frame is not fullscreen
				if(!(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)) {
					frame.pack();
					frame.setLocationRelativeTo(null);
				}

			}
		});

		applyCancelBtns.add(apply);


		cancel.setFont(DashboardFrame.NORMAL_TEXT);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainContents, "Metrics");
				frame.filterButton.setVisible(true);
			}
		});

		cancel.setPreferredSize(new Dimension(100,25));
		applyCancelBtns.add(cancel);

		this.add(applyCancelBtns, c);


	}

	private JPanel contextPanel() {
		JPanel panel = new JPanel();

		//Setting the border
		titleContext.setTitleFont(frame.NORMAL_TEXT);
		panel.setBorder(titleContext);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(20, 20, 20, 10);

		context.setFont(DashboardFrame.NORMAL_TEXT);
		context.setVerticalTextPosition(JLabel.CENTER);

		panel.add(context, c);

		c.gridx = 1;

		checkAnyContext = new JCheckBox("Any");
		checkNews = new JCheckBox("News");
		checkShopping = new JCheckBox("Shopping");
		checkSocial = new JCheckBox("Social Media");
		checkBlog = new JCheckBox("Blog");
		checkHobbies = new JCheckBox("Hobbies");
		checkTravel = new JCheckBox("Travel");
		checkAnyContext.setSelected(true);

				checkAnyContext.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if (checkAnyContext.isSelected()){
							checkNews.setSelected(true);
							checkShopping.setSelected(true);
							checkSocial.setSelected(true);
							checkBlog.setSelected(true);
							checkHobbies.setSelected(true);
							checkTravel.setSelected(true);
						} else {
							checkNews.setSelected(false);
							checkShopping.setSelected(false);
							checkSocial.setSelected(false);
							checkBlog.setSelected(false);
							checkHobbies.setSelected(false);
							checkTravel.setSelected(false);
		
						}
					}
				});
				ActionListener deselect = new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {		
						if(checkAnyContext.isSelected()){
							checkAnyContext.setSelected(false);
						}
						if(checkNews.isSelected() && checkShopping.isSelected() && checkSocial.isSelected() && 
								 checkBlog.isSelected() && checkHobbies.isSelected() &&
								checkTravel.isSelected()){
							checkAnyContext.setSelected(true);
						}
					}
		
				};
		
				
				checkNews.addActionListener(deselect);
				checkShopping.addActionListener(deselect);
				checkSocial.addActionListener(deselect);
				checkBlog.addActionListener(deselect);
				checkHobbies.addActionListener(deselect);
				checkTravel.addActionListener(deselect);
				

		c.insets = new Insets(20, 0, 20, 10);		
		c.gridx = 1;
		panel.add(checkAnyContext, c);
		c.gridx = 2;
		panel.add(checkNews, c);
		c.gridx = 3;
		panel.add(checkShopping, c);		
		c.gridx = 4;
		panel.add(checkSocial, c);		
		c.gridx = 5;	
		panel.add(checkBlog, c);	
		c.gridx = 6;
		panel.add(checkHobbies, c);	
		c.gridx = 7;
		panel.add(checkTravel, c);


		return panel;
	}

	private JPanel datePanel() {
		JPanel panel = new JPanel();

		//Setting the border
		titleDate.setTitleFont(frame.NORMAL_TEXT);
		panel.setBorder(titleDate);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(20, 20, 20, 10);

		contextDate.setFont(DashboardFrame.NORMAL_TEXT);
		contextDate.setVerticalTextPosition(JLabel.CENTER);

		panel.add(contextDate, c);

		c.gridx = 1;

		checkAnyDate = new JCheckBox("All");
		JDatePicker dp = null;


		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		startModel = new UtilDateModel();
		endModel = new UtilDateModel();
		Date startDate = new Date();
		try {
			d = frame.data.getDatabase();
			startDate = DATE_FORMAT.parse(d.getStartDate(null, null, "impressions"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int year = startDate.getYear() + 1900;
		int month = startDate.getMonth();
		int day = startDate.getDate();


		System.out.println(year+" "+month);

		startModel.setDate(year,month,day);
		endModel.setDate(year,month,day);
		
		Runnable modelActionListener = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(startModel, p);
		JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel, p);
		JDatePickerImpl startDatePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
		JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateComponentFormatter());

		//JDatePicker datePicker = new JDatePicker();



		checkAnyDate.setSelected(true);

		c.insets = new Insets(20, 0, 20, 10);
		c.gridx = 1;

		panel.add(checkAnyDate, c);

		c.insets = new Insets(20, 0, 20, 10);		
		c.gridx = 2;
		panel.add(startDatePicker);
		c.gridx = 3;
		panel.add(endDatePicker);


		return panel;
	}

	private JPanel audiencePanel() {
		JPanel panel = new JPanel();

		//Setting the border
		titleAudience.setTitleFont(frame.NORMAL_TEXT);
		panel.setBorder(titleAudience);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(20, 20, 20, 10);

		ageLabel.setFont(DashboardFrame.NORMAL_TEXT);
		ageLabel.setVerticalTextPosition(JLabel.CENTER);

		panel.add(ageLabel, c);

		checkAnyAge = new JCheckBox("Any");
		checkLess25 = new JCheckBox("<25");
		check2534 = new JCheckBox("25-34");
		check3544 = new JCheckBox("35-44");
		check4554 = new JCheckBox("45-54");
		checkGreat55 = new JCheckBox(">55");
		checkAnyAge.setSelected(true);
				checkAnyAge.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if (checkAnyAge.isSelected()){
							checkLess25.setSelected(true);
							check2534.setSelected(true);
							check3544.setSelected(true);
							check4554.setSelected(true);
							checkGreat55.setSelected(true);
						} else {
							checkLess25.setSelected(false);
							check2534.setSelected(false);
							check3544.setSelected(false);
							check4554.setSelected(false);
							checkGreat55.setSelected(false);
						}
					}
				});
		
				ActionListener deselectAge = new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {		
						if(checkAnyAge.isSelected()){
							checkAnyAge.setSelected(false);
						}
						if(checkLess25.isSelected() && check2534.isSelected() && check3544.isSelected() &&
								check4554.isSelected() && checkGreat55.isSelected()){
							checkAnyAge.setSelected(true);
						}
					}
		
				};
		
				checkLess25.addActionListener(deselectAge);
				check2534.addActionListener(deselectAge);
				check3544.addActionListener(deselectAge);
				check4554.addActionListener(deselectAge);
				checkGreat55.addActionListener(deselectAge);

	

		c.insets = new Insets(20, 0, 20, 10);
		c.gridx = 1;

		panel.add(checkAnyAge, c);

		c.insets = new Insets(20, 0, 20, 10);		
		c.gridx = 2;
		panel.add(checkLess25, c);
		c.gridx = 3;
		panel.add(check2534, c);
		c.gridx = 4;
		panel.add(check3544, c);		
		c.gridx = 5;
		panel.add(check4554, c);		
		c.gridx = 6;
		panel.add(checkGreat55, c);


		c.insets = new Insets(20, 20, 20, 10);
		c.gridx = 0;
		c.gridy = 1;

		incomeLabel.setFont(DashboardFrame.NORMAL_TEXT);
		incomeLabel.setVerticalTextPosition(JLabel.CENTER);

		panel.add(incomeLabel, c);

		checkAnyIncome = new JCheckBox("Any");
		checkLow = new JCheckBox("Low");
		checkMedium = new JCheckBox("Medium");
		checkHigh = new JCheckBox("High");
		//One button selected at any one time
		checkAnyIncome.setSelected(true);
				checkAnyIncome.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if (checkAnyIncome.isSelected()){
							checkLow.setSelected(true);
							checkMedium.setSelected(true);
							checkHigh.setSelected(true);
						} else {
							checkLow.setSelected(false);
							checkMedium.setSelected(false);
							checkHigh.setSelected(false);
						}
					}
				});
				ActionListener deselectIncome = new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {		
						if(checkAnyIncome.isSelected()){
							checkAnyIncome.setSelected(false);
						}
						if(checkLow.isSelected() && checkMedium.isSelected() && checkHigh.isSelected()){
							checkAnyIncome.setSelected(true);
						}
					}
		
				};
				checkLow.addActionListener(deselectIncome);
				checkMedium.addActionListener(deselectIncome);
				checkHigh.addActionListener(deselectIncome);



		c.insets = new Insets(20, 0, 20, 10);
		c.gridx = 1;

		panel.add(checkAnyIncome, c);

		c.insets = new Insets(20, 0, 20, 20);		
		c.gridx = 2;
		panel.add(checkLow, c);
		c.gridx = 3;
		panel.add(checkMedium, c);
		c.gridx = 4;
		panel.add(checkHigh, c);		

		c.insets = new Insets(20, 20, 20, 10);
		c.gridx = 0;
		c.gridy = 2;

		genderLabel.setFont(DashboardFrame.NORMAL_TEXT);
		genderLabel.setVerticalTextPosition(JLabel.CENTER);

		panel.add(genderLabel, c);

		checkAnyGender = new JCheckBox("Any");
		checkMale = new JCheckBox("Male");
		checkFemale = new JCheckBox("Female");
		//One button selected at any one time
		checkAnyGender.setSelected(true);

				checkAnyGender.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if (checkAnyGender.isSelected()){
							checkMale.setSelected(true);
							checkFemale.setSelected(true);
						} else {
							checkMale.setSelected(false);
							checkFemale.setSelected(false);
						}
					}
				});
				ActionListener deselectGender= new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {		
						if(checkAnyGender.isSelected()){
							checkAnyGender.setSelected(false);
						}
						if(checkMale.isSelected() && checkFemale.isSelected()){
							checkAnyGender.setSelected(true);
						}
					}
		
				};
				checkMale.addActionListener(deselectGender);
				checkFemale.addActionListener(deselectGender);


		c.insets = new Insets(20, 0, 20, 10);
		c.gridx = 1;

		panel.add(checkAnyGender, c);

		c.insets = new Insets(20, 0, 20, 20);		
		c.gridx = 2;
		panel.add(checkMale, c);
		c.gridx = 3;
		panel.add(checkFemale, c);

		return panel;
	}

	public void setPrevious(String previous){
		this.previous = previous;
	}

	public void panelRepaint(){
		System.out.println("repaint");
		this.repaint();
		frame.revalidate();
	}
	
	public void setFonts (Font f, Font titleFont) {
		


		titleAudience.setTitleFont(f);
		titleContext.setTitleFont(f);
		titleDate.setTitleFont(f);

		checkAnyDate.setFont(f);

		checkAnyContext.setFont(f);
		checkNews.setFont(f);
		checkShopping.setFont(f);
		checkSocial.setFont(f);
		checkBlog.setFont(f);
		checkHobbies.setFont(f);
		checkTravel.setFont(f);

		checkAnyAge.setFont(f);
		checkLess25.setFont(f);
		check2534.setFont(f);
		check3544.setFont(f);
		check4554.setFont(f);
		checkGreat55.setFont(f);
		
		checkAnyIncome.setFont(f);
		checkLow.setFont(f);
		checkMedium.setFont(f);
		checkHigh.setFont(f);

		checkAnyGender.setFont(f);
		checkMale.setFont(f);
		checkFemale.setFont(f);
		

		genderLabel.setFont(f);
		filterTitle.setFont(titleFont);
		context.setFont(f);
		contextDate.setFont(f);
		ageLabel.setFont(f);
		incomeLabel.setFont(f);
		
		apply.setFont(f);
		cancel.setFont(f);
		
		
	}
	
}
