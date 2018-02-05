import com.sun.deploy.uitoolkit.impl.fx.ui.FXMessageDialog;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Chart panel display charts for all key metric with varying time granularity.
 */
public class ChartPanel extends JPanel {

	private Scene currentScene;

	private JTabbedPane chartTabbedPane;
	private JFXPanel fxLineChartPanel;
	private JFXPanel fxScatterChartPanel;
	private JFXPanel fxBarChartPanel;

	JComboBox databasesCombo;
	
	private DashboardFrame mainFrame;

	private boolean comparing = false;

	private ArrayList<ChartData> chartData1 = new ArrayList();
	private ArrayList<ChartData> chartData2 = new ArrayList();
	private ArrayList<ChartData> histChartData1 = new ArrayList();
	private ArrayList<ChartData> histChartData2 = new ArrayList();

	private KeyMetrics keyMetric;
	private TimeGranularity timeGranularity;

	XYChart.Series keyMetricDataSeries = new XYChart.Series();
	XYChart.Series histKeyMetricDataSeries = new XYChart.Series();

	enum KeyMetrics {
		NUM_OF_IMPRESIONS("Number of Impressions"), NUM_OF_CLICKS("Number of Clicks"), NUM_OF_BOUNCES("Number of Bounces"),
		NUM_OF_CONVERSIONS("Number of Conversions"), TOTAL_COST("Total Cost (£)"), NUM_OF_UNIQUES("Number of Uniques"),
		BOUNCE_RATE("Bounce Rate (%)"), CLICK_THROUGH_RATE("Click Through Rate - CTR (%)"), COST_PER_ACQUISITION("Cost per Acquisition - CPA (£)"),
		COST_PER_CLICK("Cost per Click - CPC (£)"), COST_PER_THOUSAND_IMPRESSION("Cost per Thousand Impressions - CPM (£)");
		private final String displayName;
		KeyMetrics(String s) {
			displayName = s;
		}
		@Override
		public String toString() {
			return displayName;
		}
	}

	enum TimeGranularity {
		HOUR("Hour"), DAY("Day"), WEEK("Week");
		private final String displayName;
		TimeGranularity(String s) {
			displayName = s;
		}
		@Override
		public String toString() {
			return displayName;
		}
	}

	public ChartPanel(DashboardFrame frame){
		mainFrame = frame;
		init();
	}

	/**
	 * Initialises the chart panels gui components
	 */
	public void init(){
		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(20,20,20,20));

		/* Creates all the chart panels and adds them to a tabbed pane. */

		chartTabbedPane = new JTabbedPane();
		chartTabbedPane.setTabPlacement(SwingConstants.BOTTOM);

		fxLineChartPanel = new JFXPanel();
		fxScatterChartPanel = new JFXPanel();
		fxBarChartPanel = new JFXPanel();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fxLineChartPanel.setScene(initChartScene());
				fxScatterChartPanel.setScene(initChartScene());
				fxBarChartPanel.setScene(initChartScene());
			}
		});

		chartTabbedPane.addTab("Line", fxLineChartPanel);
		chartTabbedPane.addTab("Scatter", fxScatterChartPanel);
		chartTabbedPane.addTab("Histogram", fxBarChartPanel);
		this.add(chartTabbedPane, BorderLayout.CENTER);


		/* Creates the settings panel */


		JPanel chartSettingsPanel = new JPanel();
		GridLayout gridLayout;
		gridLayout = new GridLayout(2,3);

		gridLayout.setHgap(60);
		chartSettingsPanel.setLayout(gridLayout);
		chartSettingsPanel.setBorder(BorderFactory.createEmptyBorder(0,100,0,100));

		chartSettingsPanel.add(new JLabel("Key Metric"));
		chartSettingsPanel.add(new JLabel("Time Granularity"));

		JButton saveButton = new JButton("Save");
		JPanel savePrintPanel = new JPanel();

		saveButton.addActionListener((event) -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					saveChart();
				}
			});
		});
		JButton printButton = new JButton("Print");
		printButton.addActionListener((event) -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					updateCurrentScene();
					Node node = currentScene.getRoot();
					printChart(node);
				}
			});
		});
		savePrintPanel.add(saveButton);
		savePrintPanel.add(printButton);
		savePrintPanel.setLayout(new GridLayout(1,2));

		chartSettingsPanel.add(savePrintPanel);

		JComboBox keyMetricsCombo = new JComboBox(KeyMetrics.values());
		keyMetricsCombo.addActionListener((event) -> {
			keyMetric = (KeyMetrics) keyMetricsCombo.getSelectedItem();
		});
		keyMetricsCombo.setSelectedIndex(0);
		chartSettingsPanel.add(keyMetricsCombo);

		JComboBox timeGranularityCombo = new JComboBox(TimeGranularity.values());
		timeGranularityCombo.addActionListener((event) -> {
			timeGranularity = (TimeGranularity) timeGranularityCombo.getSelectedItem();
		});
		timeGranularityCombo.setSelectedIndex(0);
		chartSettingsPanel.add(timeGranularityCombo);


		JButton loadButton = new JButton("Load");
		loadButton.addActionListener((event) -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					updateCharts();
				}
			});
		});
		chartSettingsPanel.add(loadButton);

		this.add(chartSettingsPanel, BorderLayout.SOUTH);
	}

	/**
	 * Allows the user to save an image of the current chart
	 */
	private void saveChart(){
		if (currentScene != null) {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
			fileChooser.getExtensionFilters().add(extFilter);
			fileChooser.setTitle("Select folder to store the chart.");
			File file = fileChooser.showSaveDialog(null);


			updateCurrentScene();

			WritableImage snapshot = currentScene.snapshot(null);
			BufferedImage bImage = SwingFXUtils.fromFXImage(snapshot, null);
			try {
				ImageIO.write(bImage, "png", file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Allows the user to print charts via a print dialog
	 * @param nodeToPrint
	 */
	private void printChart(final Node nodeToPrint){
		Printer printer = Printer.getDefaultPrinter();
		PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);

		PrinterJob job = PrinterJob.createPrinterJob();

		if (job != null && job.showPrintDialog(nodeToPrint.getScene().getWindow())){

			boolean success = job.printPage(pageLayout, nodeToPrint);
			if (success) {
				job.endJob();
			}
		}
	}

	/**
	 * Updates the current chart scene
	 */
	private void updateCurrentScene(){
		int chartid = chartTabbedPane.getSelectedIndex();
		if (chartid == 0){
			currentScene = fxLineChartPanel.getScene();
		} else if (chartid == 1){
			currentScene = fxScatterChartPanel.getScene();
		} else if (chartid == 2){
			currentScene = fxBarChartPanel.getScene();
		}
	}

	/**
	 * Updates the current data and charts depending on the combo boxes.
	 */
	private int getTimeMilli(){
		int timeDifference;
		if(timeGranularity.toString().equals("Hour")){
			timeDifference = 3600000;
		} else if (timeGranularity.toString().equals("Day")){
			timeDifference = 3600000*24;
		} else {
			timeDifference = 3600000*24*7;
		}
		return timeDifference;
	}

	/**
	 * Update all three charts based off of the key metrics and time granularity.
	 */
	private void updateCharts(){

		int timeDifference = getTimeMilli();

		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


		mainFrame.cardLayout.show(mainFrame.mainContents, "Loading");
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				System.out.println(timeDifference);
				System.out.println(mainFrame.data.getDatabase().getStartDate(null, null, "impressions"));
				System.out.println(mainFrame.data.getDatabase().getEndDate(null, null, "impressions"));
				Date startDate = null;
				Boolean filteredDate = false;
				Date endDate = null;
				String filterDate = "";
				try {
					if (mainFrame.currentFilters.indexOf("date") >= 0){
						filterDate = mainFrame.filterValues.get(mainFrame.currentFilters.indexOf("date"));
						String[] dates = filterDate.split("_");
						startDate = DATE_FORMAT.parse(dates[0]);
						endDate = DATE_FORMAT.parse(dates[1]);
						filteredDate = true;
					} else {
						startDate = DATE_FORMAT.parse(mainFrame.data.getDatabase().getStartDate(null, null, "impressions"));
						endDate = DATE_FORMAT.parse(mainFrame.data.getDatabase().getEndDate(null, null, "impressions"));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date currentDate = startDate;
				Date dateInterval = new Date();

				ArrayList<ChartData> data= new ArrayList<ChartData>();
				ArrayList<ChartData> histData= new ArrayList<ChartData>();
				ArrayList<ChartData> data2= new ArrayList<ChartData>();
				ArrayList<ChartData> histData2= new ArrayList<ChartData>();

				//System.out.println(endDate);

				Number num = 0;

				Number previous = 0;
				while (currentDate.compareTo(endDate) < 0) {
					dateInterval.setTime(currentDate.getTime()+timeDifference);
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTime(currentDate);
					String date =  DATE_FORMAT.format(currentDate) +"_"+ DATE_FORMAT.format(dateInterval);
					ArrayList<String> filters = new ArrayList<String>();
					ArrayList<String> values = new ArrayList<String>();

					int i = -1;

					i = mainFrame.currentFilters.indexOf("date");
					if (i >= 0){
						mainFrame.filterValues.remove(i);
						mainFrame.filterValues.add(i, date);
					} else {
						mainFrame.currentFilters.add("date");
						mainFrame.filterValues.add(date);
					}

					//System.out.println(mainFrame.data.getDatabase().getNoOfImpressions(filters,values));

					for(String s :values){
						System.out.println(s);
					}

					switch (keyMetric){
					case COST_PER_CLICK:
						num = checkInfinity(mainFrame.data.getDatabase().getCPC(mainFrame.currentFilters, mainFrame.filterValues));
						data.add(new ChartData(gc, num));
						histData.add(new ChartData(gc, num));
						break;
					case TOTAL_COST:
						num = checkInfinity(mainFrame.data.getDatabase().getTotalCost(mainFrame.currentFilters, mainFrame.filterValues));
						data.add(new ChartData(gc, previous.doubleValue() + num.doubleValue()));
						histData.add(new ChartData(gc, num));
						break;
					case BOUNCE_RATE:
						num = checkInfinity(mainFrame.data.getDatabase().getBounceRate(mainFrame.currentFilters, mainFrame.filterValues,mainFrame.currentBounceDef));
						data.add(new ChartData(gc, num));
						histData.add(new ChartData(gc, num));
						break;
					case CLICK_THROUGH_RATE:
						num = checkInfinity(mainFrame.data.getDatabase().getCTR(mainFrame.currentFilters, mainFrame.filterValues));
						data.add(new ChartData(gc, num));
						histData.add(new ChartData(gc, num));
						break;
					case COST_PER_ACQUISITION:
						num = checkInfinity(mainFrame.data.getDatabase().getCPA(mainFrame.currentFilters, mainFrame.filterValues));
						data.add(new ChartData(gc, num));
						histData.add(new ChartData(gc, num));
						break;
					case COST_PER_THOUSAND_IMPRESSION:
						num = checkInfinity(mainFrame.data.getDatabase().getCPM(mainFrame.currentFilters, mainFrame.filterValues));
						data.add(new ChartData(gc, num));
						histData.add(new ChartData(gc, num));
						break;
					case NUM_OF_BOUNCES:
						num = mainFrame.data.getDatabase().getNoOfBounces(mainFrame.currentFilters, mainFrame.filterValues,mainFrame.currentBounceDef);
						data.add(new ChartData(gc, previous.intValue() + num.intValue()));
						histData.add(new ChartData(gc, num));
						break;
					case NUM_OF_CLICKS:
						num = mainFrame.data.getDatabase().getNoOfClicks(mainFrame.currentFilters, mainFrame.filterValues);
						data.add(new ChartData(gc, previous.intValue() + num.intValue()));
						histData.add(new ChartData(gc, num));
						break;
					case NUM_OF_CONVERSIONS:
						num = mainFrame.data.getDatabase().getNoOfConversions(mainFrame.currentFilters, mainFrame.filterValues);
						data.add(new ChartData(gc, previous.intValue() + num.intValue()));
						histData.add(new ChartData(gc, num));
						break;
					case NUM_OF_IMPRESIONS:
						num = mainFrame.data.getDatabase().getNoOfImpressions(mainFrame.currentFilters, mainFrame.filterValues);
						data.add(new ChartData(gc, previous.intValue() + num.intValue()));
						histData.add(new ChartData(gc, num));
						break;
					case NUM_OF_UNIQUES:
						num = mainFrame.data.getDatabase().getNoOfUniques(mainFrame.currentFilters, mainFrame.filterValues);
						data.add(new ChartData(gc, previous.intValue() + num.intValue()));
						histData.add(new ChartData(gc, num));
						break;
					}
/*
					if (!databasesCombo.getSelectedItem().equals("None")) {
						Database db = new Database(databasesCombo.getSelectedItem().toString());
						switch (keyMetric){
						case COST_PER_CLICK:
							num = checkInfinity(db.getCPC(mainFrame.currentFilters, mainFrame.filterValues));
							data2.add(new ChartData(gc, num));
							histData2.add(new ChartData(gc, num));
							break;
						case TOTAL_COST:
							num = checkInfinity(db.getTotalCost(mainFrame.currentFilters, mainFrame.filterValues));
							data2.add(new ChartData(gc, previous.doubleValue() + num.doubleValue()));
							histData2.add(new ChartData(gc, num));
							break;
						case BOUNCE_RATE:
							num = checkInfinity(db.getBounceRate(mainFrame.currentFilters, mainFrame.filterValues,mainFrame.currentBounceDef));
							data2.add(new ChartData(gc, num));
							histData2.add(new ChartData(gc, num));
							break;
						case CLICK_THROUGH_RATE:
							num = checkInfinity(db.getCTR(mainFrame.currentFilters, mainFrame.filterValues));
							data2.add(new ChartData(gc, num));
							histData2.add(new ChartData(gc, num));
							break;
						case COST_PER_ACQUISITION:
							num = checkInfinity(db.getCPA(mainFrame.currentFilters, mainFrame.filterValues));
							data2.add(new ChartData(gc, num));
							histData2.add(new ChartData(gc, num));
							break;
						case COST_PER_THOUSAND_IMPRESSION:
							num = checkInfinity(db.getCPM(mainFrame.currentFilters, mainFrame.filterValues));
							data2.add(new ChartData(gc, num));
							histData2.add(new ChartData(gc, num));
							break;
						case NUM_OF_BOUNCES:
							num = db.getNoOfBounces(mainFrame.currentFilters, mainFrame.filterValues,mainFrame.currentBounceDef);
							data2.add(new ChartData(gc, previous.intValue() + num.intValue()));
							histData2.add(new ChartData(gc, num));
							break;
						case NUM_OF_CLICKS:
							num = db.getNoOfClicks(mainFrame.currentFilters, mainFrame.filterValues);
							data2.add(new ChartData(gc, previous.intValue() + num.intValue()));
							histData2.add(new ChartData(gc, num));
							break;
						case NUM_OF_CONVERSIONS:
							num = db.getNoOfConversions(mainFrame.currentFilters, mainFrame.filterValues);
							data2.add(new ChartData(gc, previous.intValue() + num.intValue()));
							histData2.add(new ChartData(gc, num));
							break;
						case NUM_OF_IMPRESIONS:
							num = db.getNoOfImpressions(mainFrame.currentFilters, mainFrame.filterValues);
							data2.add(new ChartData(gc, previous.intValue() + num.intValue()));
							histData2.add(new ChartData(gc, num));
							break;
						case NUM_OF_UNIQUES:
							num = db.getNoOfUniques(mainFrame.currentFilters, mainFrame.filterValues);
							data2.add(new ChartData(gc, previous.intValue() + num.intValue()));
							histData2.add(new ChartData(gc, num));
							break;
						}
					}*/
					previous = previous.doubleValue() + num.doubleValue();

					currentDate.setTime(currentDate.getTime()+timeDifference);
				}
				mainFrame.cardLayout.show(mainFrame.mainContents, "Metrics");
				if (filteredDate){
					mainFrame.filterValues.add(mainFrame.currentFilters.indexOf("date"), filterDate);
					filteredDate = false;
				} else {
					if (mainFrame.currentFilters.indexOf("date") >= 0){
						mainFrame.filterValues.remove(mainFrame.currentFilters.indexOf("date"));
						mainFrame.currentFilters.remove("date");
					}
				}
				chartData1 = data;
				histChartData1 = histData;
				chartData2 = data2;
				histChartData2 = histData2;
				initChartPanels();
			}
		});
	}

	/**
	 * Updates all chart panels with the current data.
	 */
	private void initChartPanels(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initLineChart(fxLineChartPanel);
				initScatterChart(fxScatterChartPanel);
				initBarChart(fxBarChartPanel);
			}
		});
	}

	/**
	 * Loads a line chart of the current data onto the fx panel given.
	 * @param chartPanel
	 */
	private void initLineChart(JFXPanel chartPanel){

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

		if (!comparing){
			XYChart.Series keyMetricDataSeries = initChart(lineChart, xAxis, yAxis, chartData1);
			lineChart.getData().addAll(keyMetricDataSeries);
		} else {
			XYChart.Series keyMetricDataSeries1 = initChart(lineChart, xAxis, yAxis, chartData1);
			XYChart.Series keyMetricDataSeries2 = initChart(lineChart, xAxis, yAxis, chartData2);
			lineChart.getData().addAll(keyMetricDataSeries1, keyMetricDataSeries2);
		}

		Scene scene  = new Scene(lineChart);
		currentScene = scene;

		try {
			scene.getStylesheets().add("chart.css");
		} catch (Exception e){
			System.err.println(e);
		}

		chartPanel.setScene(scene);
	}

	/**
	 * Loads a scatter chart of the current data onto the fx panel given.
	 * @param chartPanel
	 */
	private void initScatterChart(JFXPanel chartPanel){
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final ScatterChart<String,Number> scatterChart = new ScatterChart<String,Number>(xAxis,yAxis);

		if (!comparing){
			XYChart.Series keyMetricDataSeries = initChart(scatterChart, xAxis, yAxis, chartData1);
			scatterChart.getData().addAll(keyMetricDataSeries);
		} else {
			XYChart.Series keyMetricDataSeries1 = initChart(scatterChart, xAxis, yAxis, chartData1);
			XYChart.Series keyMetricDataSeries2 = initChart(scatterChart, xAxis, yAxis, chartData2);
			scatterChart.getData().addAll(keyMetricDataSeries1, keyMetricDataSeries2);
		}

		Scene scene  = new Scene(scatterChart);
		currentScene = scene;

		try {
			scene.getStylesheets().add("chart.css");
		} catch (Exception e){
			System.err.println(e);
		}

		chartPanel.setScene(scene);
	}

	/**
	 * Loads a bar chart of the current data onto the fx panel given.
	 * @param chartPanel
	 */
	private void initBarChart(JFXPanel chartPanel){
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String,Number> barChart = new BarChart(xAxis,yAxis);
		barChart.setCategoryGap(0);
		barChart.setBarGap(0);

		if (!comparing){
			XYChart.Series keyMetricDataSeries = initChart(barChart, xAxis, yAxis, chartData1);
			barChart.getData().addAll(keyMetricDataSeries);
		} else {
			XYChart.Series keyMetricDataSeries1 = initChart(barChart, xAxis, yAxis, histChartData1);
			XYChart.Series keyMetricDataSeries2 = initChart(barChart, xAxis, yAxis, histChartData2);
			barChart.getData().addAll(keyMetricDataSeries1, keyMetricDataSeries2);
		}
		Scene scene  = new Scene(barChart);
		currentScene = scene;

		try {
			scene.getStylesheets().add("chart.css");
		} catch (Exception e){
			System.err.println(e);
		}

		chartPanel.setScene(scene);
	}

	/**
	 * Initialises and loads the chart with the current data.
	 * @param chart
	 * @param xAxis
	 * @param yAxis
	 * @return keyMetricDataSeries
	 */
	private XYChart.Series initChart(Chart chart, CategoryAxis xAxis, NumberAxis yAxis, ArrayList<ChartData> chartData){

		xAxis.setLabel("Date (" + timeGranularity + ")");
		yAxis.setLabel(keyMetric.toString());
		chart.setTitle(keyMetric + " Over Time");

		keyMetricDataSeries = new XYChart.Series();
		keyMetricDataSeries.setName(keyMetric.toString());

		SimpleDateFormat dateFormat = null;
		switch (timeGranularity){
		case HOUR:
			dateFormat = new SimpleDateFormat("ha EEE, d MMM yy");
			break;
		case DAY:
			dateFormat = new SimpleDateFormat("EEE, d MMM yy");
			break;
		case WEEK:
			dateFormat = new SimpleDateFormat("d MMM yy");
			break;
		}

		for (ChartData data : chartData){
			keyMetricDataSeries.getData().add(new XYChart.Data(dateFormat.format(( data.getDate().getTime())), data.getNumber()));
		}

		return keyMetricDataSeries;
	}

	/**
	 * Chart panels display a start up message.
	 */
	private Scene initChartScene(){
		Text startText = new Text("Select and load a key metric");
		StackPane root = new StackPane();
		root.getChildren().add(startText);

		Scene startScene = new Scene(root);
		return startScene;
	}

	public Double checkInfinity (Double d){
		if (d == Double.POSITIVE_INFINITY)
			return 0.0;
		if (Double.isNaN(d))
			return 0.0;
		return d;
	}
}
