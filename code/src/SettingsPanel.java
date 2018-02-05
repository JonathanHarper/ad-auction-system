import javafx.scene.control.RadioButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Group 20
 */
public class SettingsPanel extends JPanel {

	public String previous;
	private DashboardFrame frame;

	private JComboBox<String> selectFont;
	private JComboBox selectFontSize;

	private SettingsPanel panel;

	private Data data;

	private JRadioButton onBtn = new JRadioButton("On");
	private JRadioButton offBtn = new JRadioButton("Off");

	public SettingsPanel(DashboardFrame frame, JPanel mainContents, CardLayout cardLayout, Data d){
		initSettingsPanel(frame, mainContents, cardLayout);
		panel = this;
		data = d;
	}

	/**
	 * Initialise the settings panel
	 */
	protected void initSettingsPanel(DashboardFrame frame, JPanel mainContents, CardLayout cardLayout) {
		this.frame = frame;
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(50, 100, 50, 100);

		JLabel settingsTitle = new JLabel("Settings");
		settingsTitle.setHorizontalAlignment(JLabel.CENTER);
		settingsTitle.setFont(DashboardFrame.TITLE_TEXT);
		this.add(settingsTitle, c);

		c.gridy = 1;
		c.insets = new Insets(0, 100, 20, 100);

		JPanel font = fontPanel();
		this.add(font, c);

		c.gridy = 2;

		JPanel app = applicationPanel();
		this.add(app, c);

		c.gridy = 3;

		JPanel colourTheme = colourPanel();
		//this.add(colourPanel(), c);

		//Apply and cancel JButtons
		JPanel applyCancelBtns = new JPanel();
		applyCancelBtns.setLayout(new FlowLayout());

		c.gridy = 4;

		JButton apply = new JButton("Apply");
		apply.setPreferredSize(new Dimension(100,25));
		apply.setFont(DashboardFrame.NORMAL_TEXT);
		apply.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String f = (String) selectFont.getSelectedItem();
				DashboardFrame.fontBeingUsed = (String) selectFont.getSelectedItem();

				if(selectFontSize.getSelectedIndex() == 0){
					DashboardFrame.fontSizeBeingUsed = 0;
					DashboardFrame.NORMAL_TEXT = new Font (f, Font.PLAIN, 9);
					DashboardFrame.TITLE_TEXT = new Font (f, Font.BOLD, 30);
					DashboardFrame.VALUES_FONT = new Font (f, Font.BOLD, 16);	
					DashboardFrame.LABELS_FONT = new Font (f, Font.PLAIN, 16);					
				} else if (selectFontSize.getSelectedIndex() == 1){
					DashboardFrame.fontSizeBeingUsed = 1;
					DashboardFrame.NORMAL_TEXT = new Font (f, Font.PLAIN, 12);
					DashboardFrame.TITLE_TEXT = new Font (f, Font.BOLD, 50);
					DashboardFrame.VALUES_FONT = new Font (f, Font.BOLD, 20);	
					DashboardFrame.LABELS_FONT = new Font (f, Font.PLAIN, 20);					
				} else {
					DashboardFrame.fontSizeBeingUsed = 2;
					DashboardFrame.NORMAL_TEXT = new Font (f, Font.PLAIN, 20);
					DashboardFrame.TITLE_TEXT = new Font (f, Font.BOLD, 65);
					DashboardFrame.VALUES_FONT= new Font (f, Font.BOLD, 30);			
					DashboardFrame.LABELS_FONT= new Font (f, Font.PLAIN, 30);					
				}

				try {
					frame.filterPanel.setFonts(DashboardFrame.NORMAL_TEXT, DashboardFrame.TITLE_TEXT);
				} catch (Exception ee){

				}
				frame.startScreenPanel.setFonts(DashboardFrame.NORMAL_TEXT, DashboardFrame.TITLE_TEXT);
				frame.dashboardPanel.setFonts(DashboardFrame.LABELS_FONT, DashboardFrame.VALUES_FONT, DashboardFrame.TITLE_TEXT);

				//If the fullscreen button is selected as on, fill the screen
				if(onBtn.isSelected()) {
					frame.fullscreen = true;
					//frame.dispose();
					//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					//frame.setUndecorated(true);
					//frame.setVisible(true);
				} else {
					frame.fullscreen = false;
					//frame.dispose();
					//frame.setExtendedState(JFrame.NORMAL);
					//frame.setUndecorated(false);
					//frame.setVisible(true);
				}

				if(frame.filterButton != null){
					frame.filterButton.setVisible(true);
				}
				frame.settingsButton.setVisible(true);

				frame.revalidate();
				
				cardLayout.show(mainContents, DashboardFrame.previous);
			}
		});

		applyCancelBtns.add(apply);


		JButton cancel = new JButton("Cancel");
		cancel.setFont(DashboardFrame.NORMAL_TEXT);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(frame.filterButton != null){
					frame.filterButton.setVisible(true);
				}
				frame.settingsButton.setVisible(true);
				cardLayout.show(mainContents, DashboardFrame.previous);;
			}
		});

		cancel.setPreferredSize(new Dimension(100,25));
		applyCancelBtns.add(cancel);

		this.add(applyCancelBtns, c);


	}

	protected void setData(Data d) {
		data = d;
	}

	/**
	 * Displays options related to altering the font
	 */
	private JPanel fontPanel() {
		JPanel panel = new JPanel();

		//Setting the border
		TitledBorder title = BorderFactory.createTitledBorder("Font");
		panel.setBorder(title);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(20, 20, 10, 20);

		JLabel font = new JLabel("Font");
		font.setFont(DashboardFrame.NORMAL_TEXT);
		font.setVerticalTextPosition(JLabel.CENTER);

		panel.add(font, c);

		c.gridx = 1;

		selectFont = new JComboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		selectFont.setPreferredSize(DashboardFrame.COMBO_DIMENSION);
		selectFont.setFont(DashboardFrame.NORMAL_TEXT);
		selectFont.setSelectedItem(DashboardFrame.fontBeingUsed);

		panel.add(selectFont, c);

		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 20, 20);

		JLabel fontSize = new JLabel("Font Size");
		fontSize.setFont(DashboardFrame.NORMAL_TEXT);
		fontSize.setVerticalTextPosition(JLabel.CENTER);

		panel.add(fontSize, c);

		c.gridx = 1;

		selectFontSize = new JComboBox();
		selectFontSize.setPreferredSize(DashboardFrame.COMBO_DIMENSION);
		selectFontSize.setFont(DashboardFrame.NORMAL_TEXT);
		selectFontSize.addItem("Small");
		selectFontSize.addItem("Medium");
		selectFontSize.addItem("Large");
		selectFontSize.setSelectedIndex(DashboardFrame.fontSizeBeingUsed);

		panel.add(selectFontSize, c);

		return panel;
	}

	/**
	 * Displays any options which are related to altering the application
	 */
	private JPanel applicationPanel() {
		JPanel panel = new JPanel();

		//Setting the border
		TitledBorder title = BorderFactory.createTitledBorder("Application");
		panel.setBorder(title);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(20, 20, 20, 10);

		JLabel fullScreen = new JLabel("Full Screen");
		fullScreen.setFont(DashboardFrame.NORMAL_TEXT);
		fullScreen.setVerticalTextPosition(JLabel.CENTER);

		panel.add(fullScreen, c);

		//One button selected at any one time
		ButtonGroup bg = new ButtonGroup();
		bg.add(onBtn);
		bg.add(offBtn);

		if(frame.fullscreen) {
			onBtn.setSelected(true);
		} else {
			offBtn.setSelected(true);
		}

		c.insets = new Insets(20, 0, 20, 10);
		c.gridx = 1;

		panel.add(onBtn, c);

		c.insets = new Insets(20, 0, 20, 20);
		c.gridx = 2;

		panel.add(offBtn, c);

		return panel;
	}


	public void setPrevious(String previous){
		this.previous = previous;
	}

	/**
	 * Displays any options which are related to altering the colour/colour theme of the application
	 */
	private JPanel colourPanel() {
		JPanel panel = new JPanel();

		//Setting the border
		TitledBorder title = BorderFactory.createTitledBorder("Colour Themes");
		panel.setBorder(title);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(20, 20, 20, 10);

		JLabel themePicker = new JLabel("Theme Picker");
		themePicker.setFont(DashboardFrame.NORMAL_TEXT);
		themePicker.setVerticalTextPosition(JLabel.CENTER);

		panel.add(themePicker, c);

		c.gridx = 1;
		c.insets = new Insets(20, 0, 20, 20);

		JComboBox themePickerCombo = new JComboBox();
		themePickerCombo.setPreferredSize(DashboardFrame.COMBO_DIMENSION);
		themePickerCombo.setFont(DashboardFrame.NORMAL_TEXT);

		panel.add(themePickerCombo, c);

		return panel;
	}
}