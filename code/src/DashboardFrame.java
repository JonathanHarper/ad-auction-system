import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

/**
 * Created by Group 20
 */
public class DashboardFrame extends JFrame {
	protected String campaignPrevSelected = "";
	
	public static String previous;

	//Size of combo boxes
	public Data data;

	//References the JFrame on which the dashboard is on
	protected JFrame frame = this;

	private String currentScreen;

	public static final Dimension COMBO_DIMENSION = new Dimension(300, 20);

	public static final Font SMALL_NORM = new Font("Arial", Font.PLAIN, 9);
	public static final Font MED_NORM = new Font("Arial", Font.PLAIN, 12);
	public static final Font LARGE_NORM = new Font("Arial", Font.PLAIN, 20);

	public static final Font SMALL_TITLE = new Font("Arial", Font.BOLD, 30);
	public static final Font MED_TITLE = new Font("Arial", Font.BOLD, 50);
	public static final Font LARGE_TITLE = new Font("Arial", Font.BOLD, 65);

	public static final Font SMALL_VALUES = new Font("Arial", Font.BOLD, 16);
	public static final Font MED_VALUES = new Font("Arial", Font.BOLD, 20);
	public static final Font LARGE_VALUES = new Font("Arial", Font.BOLD, 30);

	public static final Font SMALL_LABELS = new Font("Arial", Font.PLAIN, 16);
	public static final Font MED_LABELS = new Font("Arial", Font.PLAIN, 20);
	public static final Font LARGE_LABELS = new Font("Arial", Font.PLAIN, 30);

	//Defines the font that is currently being used
	public static String fontBeingUsed = "Arial";
	//Defines the current font size
	public static int fontSizeBeingUsed = 1;
	//Keeps track of the application if it is in fullscreen or not
	public static boolean fullscreen = false;

	//Font style of title and normal text types
	public static Font TITLE_TEXT = MED_TITLE;
	public static Font NORMAL_TEXT = MED_NORM;
	//Specifies the font styles for the values of the metrics
	public static Font VALUES_FONT = MED_VALUES;
	public static Font LABELS_FONT = MED_LABELS;
	
	public ArrayList<String> currentFilters = new ArrayList<String>();
	public ArrayList<String> filterValues = new ArrayList<String>();
	public String currentBounceDef = "pages_1";
	//Allows for the switching of JPanels on the same JFrame
	protected JPanel mainContents = new JPanel();
	protected CardLayout cardLayout = new CardLayout();

	//Panels being added to the cardLayout

	FilterPanel filterPanel = new FilterPanel(this, mainContents, cardLayout);
	protected DashboardPanel dashboardPanel = new DashboardPanel(this, mainContents, cardLayout);
	protected SettingsPanel settingsPanel = new SettingsPanel(this, mainContents, cardLayout, data);
	protected StartScreenPanel startScreenPanel = new StartScreenPanel(this, mainContents, cardLayout, dashboardPanel, data, settingsPanel);
	public JToolBar toolBar;
	public JButton backButton;
	public JButton filterButton;
	public JButton settingsButton;
	public ChartPanel chartPanel = new ChartPanel(this);
	

	ArrayList<String> filters = new ArrayList<String>();
	ArrayList<String> values = new ArrayList<String>();

	protected JTabbedPane tabPanel = new JTabbedPane();

	//protected JPanel loading = (JPanel) this.getGlassPane();
	JPanel loading  = loading();

	public DashboardFrame(Data d){
		super("Ad Dashboard");
		this.data = d;
		currentScreen = "Start Screen";
		settingsPanel.setPrevious("Start Screen");
		tabPanel.add("Key Metrics", dashboardPanel);
		tabPanel.add("Graphs", chartPanel);
	}

	public SettingsPanel getSettings(){
		return settingsPanel;
	}

	public String getCurrentScreen(){
		return currentScreen;
	}

	/* public void changeLoading(Boolean l){
    	System.out.println("setting loading: " + l);

    	//loading.requestFocusInWindow();
    	loading.setVisible(l);

    }*/

	/**
	 * Initialises the start screen, setting key settings
	 */
	public void initLoading(){
	}

	public static JPanel loading(){

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		JLabel l = new JLabel("Loading...");//new ImageIcon("src/loading.gif"));

		l.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(l, BorderLayout.CENTER);
		l.setVisible(true);
		p.setVisible(true);

		return p;
	}

	protected void init() {
		this.setResizable(true);
		initLoading();

		//loading.requestFocusInWindow();
		//this.setGlassPane(loading);


		Container startPanel = this.getContentPane();
		startPanel.setLayout(new BorderLayout());
		mainContents.setLayout(cardLayout);


		//Populating the card layout
		mainContents.add(startScreenPanel, "Start Screen");
		mainContents.add(settingsPanel, "Settings");
		mainContents.add(tabPanel, "Metrics");
		mainContents.add(loading, "Loading");
		mainContents.add(filterPanel, "Filter");
		mainContents.addComponentListener(new ComponentListener(){

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				if (!DashboardFrame.fullscreen){
					frame.pack();
					frame.setLocationRelativeTo(null);
					System.out.println("Packing 1");
				}

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				if (!DashboardFrame.fullscreen){
					frame.pack();
					frame.setLocationRelativeTo(null);
					System.out.println("Packing 2");
				}

			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				if (!DashboardFrame.fullscreen){
					frame.pack();
					frame.setLocationRelativeTo(null);
					System.out.println("Packing 3");
				}

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				if (!DashboardFrame.fullscreen){
					frame.pack();
					frame.setLocationRelativeTo(null);
					System.out.println("Packing 4");
				}
			}
		});

		//Initially show the start screen
		cardLayout.show(mainContents, "Start Screen");

		previous = "Start Screen";

		this.add(mainContents, BorderLayout.CENTER);
		toolBar=toolBar();
		this.add(toolBar, BorderLayout.NORTH);


		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Creates a toolbar appearing at the top of the screen, featuring a button which allows for settings to be adjusted
	 *
	 * @return  Toolbar featuring the settings button
	 */
	protected JToolBar toolBar() {
		JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);

		backButton = new JButton(new ImageIcon("src/home.png"));
		backButton.setFocusPainted(false);
		backButton.setOpaque(false);
		backButton.setVisible(false);

		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainContents, "Start Screen");
				previous = "Start Screen";
				backButton.setVisible(false);
				filterButton.setVisible(false);
			}
		});

		toolBar.add(Box.createHorizontalGlue());

		settingsButton = new JButton(new ImageIcon("src/settings_icon.png"));
		settingsButton.setFocusPainted(false);
		settingsButton.setOpaque(false);

		//Settings button implementation - opens up the settings
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainContents, "Settings");
				if(filterButton != null){
					filterButton.setVisible(false);
				}
				settingsButton.setVisible(false);
			}
		});

		toolBar.add(backButton);
		toolBar.add(settingsButton);

		return toolBar;
	}
}