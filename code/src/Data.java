

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Data {

	private Database d; 

	private String clickLog;
	private String impressionLog;
	private String serverLog;
	private ArrayList<String>filters;
	private ArrayList<String>values;

	public Data(String databaseName, String clickLog, String serverLog, String impressionLog){
		d = new Database(databaseName);
		this.clickLog = clickLog;
		this.impressionLog = impressionLog;
		this.serverLog = serverLog;
		long start = System.nanoTime();
		readData();
		long end = System.nanoTime();
		System.out.println((end - start)/1000000000);
	}

	public Data(String databaseName, ArrayList<String> filters, ArrayList<String> values){
		d = new Database(databaseName, "");
		this.filters = filters;
		this.values = values;
	}

	public void readData(){
		readClickLog(clickLog);
		readServerLog(serverLog);
		readImpressionLog(impressionLog);
	}

	public Database getDatabase(){
		return d;
	}

	public void readClickLog(String clickPathname){
		BufferedReader br = null;

		System.out.println("Loading ClickLog at "+ clickPathname);
		String s;
		int k = 0;

		try {
			d.openConnection();
			d.setAutoCommit(false);
			br = new BufferedReader(new FileReader(clickPathname));
			if(br.readLine()!= null){
				while ((s = br.readLine()) != null) {
					k++;
					String[] splitList = s.split(",");
					d.insertClick(splitList[0],splitList[1], splitList[2]);
				}
				d.commit();
			}
			d.setAutoCommit(true);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			d.closeConnection();

			System.out.println("ClickLog done : " + k +" entries.");
		} 


	}

	public void readServerLog(String serverPathname){
		BufferedReader br = null;

		System.out.println("Loading ServerLog...");
		String s;
		int k = 0;

		try {
			d.openConnection();
			d.setAutoCommit(false);
			br = new BufferedReader(new FileReader(serverPathname));
			if(br.readLine()!= null){
				while ((s = br.readLine()) != null) {
					k++;
					String[] splitList = s.split(",");
					d.insertServer(splitList[0], splitList[1], splitList[2], splitList[3], splitList[4].charAt(0));
				}
				d.commit();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			d.closeConnection();

			System.out.println("ServerLog done : " + k +" entries.");
		} 
	}
	
	public synchronized void exec (PreparedStatement ps, PreparedStatement us, Database d){
		try {
			ps.executeBatch();
			us.executeBatch();
			d.commit();
			ps.clearBatch();
			us.clearBatch();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void readImpressionLog(String impressionPathname){


		System.out.println("Loading ImpressionLog...");

		//		try {
		//			d.openConnection();
		//			d.setAutoCommit(false);
		//			br = new BufferedReader(new FileReader(impressionPathname));
		//			if(br.readLine()!= null){
		//				while ((s = br.readLine()) != null) {
		//					k++;
		//					String[] splitList = s.split(",");
		//					
		//					d.insertImpressions(splitList[0], splitList[1], splitList[5], splitList[6]);
		//					d.insertUser(splitList[1], splitList[2].charAt(0), splitList[3], splitList[4].charAt(0));
		//				}
		//				d.commit();
		//			}
		//			d.setAutoCommit(true);
		//
		//		} 

		class LoadThread extends Thread {

			int threadNum;
			Database d;

			LoadThread(int i, Database d){
				threadNum = i;
				this.d = d;
			}

			public void run() {
				System.out.println("Thread "+threadNum+" started.");
				int k=0;
				BufferedReader br = null;
				try { 
					br = new BufferedReader(new FileReader(impressionPathname));	
					String s;
					String sql = "INSERT INTO impressions VALUES(?,?,?,?);";
					String usr = "INSERT OR IGNORE INTO users VALUES(?,?,?,?);";
					PreparedStatement ps = d.connect.prepareStatement(sql);
					PreparedStatement us = d.connect.prepareStatement(usr);



					if(br.readLine()!= null){
						int i = 0;
						while ((s = br.readLine()) != null) {
							
							i++;
							k++;
							if (i>=10000){
								exec (ps,us,d);
								i = 0;
							}

							String [] split = s.split(",");
							ps.setString(1,split[0]);
							ps.setString(2,split[1]);
							ps.setString(3,split[5]);
							ps.setString(4,split[6]);
							ps.addBatch();

							us.setString(1,split[1]);
							us.setString(2,Character.toString(split[2].charAt(0)));
							us.setString(3,split[3]);
							us.setString(4,Character.toString(split[4].charAt(0)));
							us.addBatch();

						}
						ps.executeBatch();
						us.executeBatch();
						d.commit();
						ps.clearBatch();
						us.clearBatch();
					}
				}catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					System.out.println("SQLException 2: " + e.getMessage());
				} finally {
					try {
						if (br != null)br.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					System.out.println("ImpressionLog done : " + k +" entries.");
				}

			}
		}


		try {
			d.openConnection();
			d.setAutoCommit(false);
			LoadThread t1 = new LoadThread(1,d);
			t1.start();
			t1.join();
			

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		} catch (InterruptedException e){

		} finally {

		} 

	}


}


