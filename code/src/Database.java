import java.sql.*;
import java.util.*;

public class Database {

	static Connection connect = null;
	PreparedStatement stmt;
	static final String JDBC_DRIVER = "org.sqlite.JDBC";  
	static String the_url = "jdbc:sqlite:campaigns/";
	String url, sql;
	public String databaseName;

	public Database(String databaseName){
		stmt = null;
		sql = "";
		this.databaseName = databaseName;
		url =the_url+databaseName + ".db" ;
		connectToSQL();
		setPragmaValues();
		dropTables();
		createUserTable();
		createClickTable();
		createImpressionsTable();
		createServerTable();
	}	
	
	public Database(String databaseName, String n){
		stmt = null;
		url = the_url+databaseName;
		connectToSQL();
	}
	
	//locates the database driver
	private static void connection(){
		try{
			Class.forName(JDBC_DRIVER);
		}catch(ClassNotFoundException e){
			System.out.println(e);
		}
	}
	
	//opens connection to database
	public void openConnection(){
	    try {
			connect = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}
	
	public void setAutoCommit(boolean commit) throws SQLException{
		connect.setAutoCommit(commit);
	}
	
	public void commit() throws SQLException{
		connect.commit();
	}
	
	//connects system to SQL database
	public void connectToSQL(){
		connection();
		try {
			
		    System.out.println("Connecting to database...");
		    openConnection();
		    DatabaseMetaData meta = connect.getMetaData();
		    System.out.println("Connection to database has been established.");
		    System.out.println("Database "+ meta.getURL().split(":")[2] + " was created.");
		} catch (SQLException e) {
			
			//returns sql error if unable to connect
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		    
		}finally{
			try{
				if(connect!=null)
					connect.close(); 
			}catch(SQLException e){
				System.out.println("SQLException: " + e.getMessage());
			}
		}
	}
	
	
	public void setPragmaValues(){
		try{
			openConnection();
			stmt = connect.prepareStatement("PRAGMA TEMP_STORE = MEMORY");
			stmt.execute();
			stmt = connect.prepareStatement("PRAGMA SYNCHRONOUS = 0");
			stmt.execute();
			stmt = connect.prepareStatement("PRAGMA JOURNAL_MODE = MEMORY");
			stmt.execute();
			stmt = connect.prepareStatement("PRAGMA LOCKING_MODE = EXCLUSIVE");
			stmt.execute();
			
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	
	//deletes all tables in database
	public void dropTables(){
		try {
			 System.out.println("Dropping tables...");
			 openConnection();
			stmt = connect.prepareStatement("DROP TABLE IF EXISTS users");
			stmt.executeUpdate();
			stmt = connect.prepareStatement("DROP TABLE IF EXISTS server");
			stmt.executeUpdate();
			stmt = connect.prepareStatement("DROP TABLE IF EXISTS clicks");
			stmt.executeUpdate();
			stmt = connect.prepareStatement("DROP TABLE IF EXISTS impressions");
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}finally{
			closeConnection();
		}
	}
	
	//creates a user table in database
	public void createUserTable(){
		System.out.println("Creating user table...");
		try{
			
		    openConnection();
		    
			stmt = connect.prepareStatement("CREATE TABLE IF NOT EXISTS users("
					+ "id BIGINT NOT NULL,"
					+ "gender CHAR NOT NULL,"
					+ "age VARCHAR(10) NOT NULL,"
					+ "income CHAR NOT NULL,"
					+ "PRIMARY KEY (id)"
					+ ")");
			stmt.executeUpdate();

			System.out.println("User table created.");
		}catch(SQLException e){
		}finally{
			closeConnection();
		}
	}
	
	//creates a click table in database
	public void createClickTable(){
		System.out.println("Creating click table...");
		try{
			
		    openConnection();
			
			stmt = connect.prepareStatement("CREATE TABLE IF NOT EXISTS clicks("
					+ "date DATETIME NOT NULL,"
					+ "id BIGINT NOT NULL,"
					+ "clickCost DOUBLE,"
					+ "PRIMARY KEY (id, date),"
					+ "FOREIGN KEY (id) REFERENCES users(id)"
					+ ")");
			
			
			stmt.executeUpdate();

			System.out.println("Click table created.");
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}finally{
			closeConnection();
		}

	}
	
	//creates a server table in database
	public void createServerTable(){
		System.out.println("Creating server table...");
		try{
			
		    openConnection();

			stmt = connect.prepareStatement("CREATE TABLE IF NOT EXISTS server("
					+ "entryDate DATETIME NOT NULL,"
					+ "id BIGINT NOT NULL,"
					+ "exitDate DATETIME,"
					+ "pagesViewed INTEGER,"
					+ "conversion CHAR NOT NULL,"
					+ "PRIMARY KEY (entryDate, id),"
					+ "FOREIGN KEY (id) REFERENCES users(id)"
					+ ")");
			stmt.executeUpdate();
		
			System.out.println("Server table created.");
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}finally{
			closeConnection();
		}
	}
	
	//creates an impressions table in database
	public void createImpressionsTable(){
		System.out.println("Creating impressions table...");
		try{
			
		    openConnection();

			stmt = connect.prepareStatement("CREATE TABLE IF NOT EXISTS impressions("
					+ "date DATETIME NOT NULL,"
					+ "id BIGINT NOT NULL,"
					+ "context TEXT,"
					+ "impressionCost DOUBLE"
					+ ")");
			stmt.executeUpdate();
			
			System.out.println("Impressions table created.");
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}finally{
			closeConnection();
		}
	}
	
	//inserts users into table
	public void insertUser(String id, char gender, String age, char income)throws SQLException{
		try{
			sql = "INSERT INTO users VALUES ("
					+ id +","
					+ "'" + gender +"',"
					+ "'" + age +"',"
					+ "'" + income +"'"
					+ ")";
			stmt = connect.prepareStatement(sql);
			stmt.executeUpdate();
		}catch(SQLException e){
		}
	}

	//inserts clicks into table
	public void insertClick(String date, String id, String clickCost) throws SQLException{
			sql = "INSERT INTO clicks VALUES("
					+"'"+ date + "',"
					+ id + ","
					+ clickCost
					+ ")";
			stmt = connect.prepareStatement(sql);
			stmt.executeUpdate();	
	}
	
	//insert server data into table
	public void insertServer(String entryDate, String id, String exitDate, String pagesViewed, char conversion) throws SQLException{
			sql = "INSERT INTO server VALUES("
					+"'"+ entryDate + "',"
					+ id +","
					+ "'" + exitDate +"',"
					+ pagesViewed + ",'"
					+ conversion
					+ "')";
			stmt = connect.prepareStatement(sql);
			stmt.executeUpdate();
	}
	
	//inserts impressions into table
	public void insertImpressions(String date, String id, String context, String impressionCost) throws SQLException{
			sql = "INSERT INTO impressions VALUES("
					+ "'" + date + "',"
					+ id + ","
					+ "'" + context + "',"
					+ impressionCost 
					+ ")";
			stmt = connect.prepareStatement(sql);
			stmt.executeUpdate();
	
	}
	
	//returns the no. of times the advert is shown to a user
	
	public int getNoOfImpressions(ArrayList<String> filters, ArrayList<String> values){
		int noOfImpressions = 0;
		String tableName = "impressions";
		String selectStmt = "SELECT COUNT(*) AS total";
		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName);
		
		
		try{
			openConnection();
			
			sql = selectStmt + fromStmt + joinStmt + whereStmt;
			System.out.println(sql);
			stmt = connect.prepareStatement(sql);
			
			ResultSet records = stmt.executeQuery();
			noOfImpressions = records.getInt("total");
			return noOfImpressions;
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return noOfImpressions;
		
	}
	
	//returns the no. of times a user clicks on the advert
	public int getNoOfClicks(ArrayList<String> filters, ArrayList<String> values){
		int noOfClicks = 0;
		
		String tableName = "clicks";
		String selectStmt = "SELECT COUNT(*) AS total";
		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName);
		
		try{
			openConnection();
			
			sql = selectStmt + fromStmt + joinStmt + whereStmt;
			System.out.println(sql);
			
			stmt = connect.prepareStatement(sql);
			
			ResultSet records = stmt.executeQuery();
			noOfClicks = records.getInt("total");
			
			return noOfClicks;
			
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return noOfClicks;
	}
	
	//only takes 3 parameters now
	//bounceDefiniton as 'pages_3' or 'seconds_40' and will only allow one option
	public int getNoOfBounces(ArrayList<String> filters, ArrayList<String> values, String bounceDefinition){
		int noOfBounces = 0;
		
		String tableName = "server";
		String selectStmt = "SELECT COUNT(*) AS 'total'";
		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName);
		if(whereStmt.equals(""))
			whereStmt += " WHERE";
		else
			whereStmt += " AND";
		
		String[] split = bounceDefinition.split("_");
		
		if(bounceDefinition.contains("pages")){
			whereStmt += " pagesViewed <= " + split[1];
		}else if(bounceDefinition.contains("seconds")){
			whereStmt += " (strftime('%s',server.exitDate) - strftime('%s',server.entryDate)) <= " + split[1];
		}else{
			whereStmt += " pagesViewed <= 1";
		}
		
		try{
			openConnection();
			
			sql = selectStmt + fromStmt + joinStmt + whereStmt;
			System.out.println(sql);
			stmt = connect.prepareStatement(sql);
			
			ResultSet records = stmt.executeQuery();
			noOfBounces = records.getInt("total");
			
			return noOfBounces;
			
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return noOfBounces;
	}
	
	//returns no. of times a user clicks on the advert and
	//makes an action on the website
	public int getNoOfConversions(ArrayList<String> filters, ArrayList<String> values){
		int noOfConversions = 0;
		
		String tableName = "server";
		String selectStmt = "SELECT COUNT(*) AS 'total'";
		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName );
		if(!whereStmt.equals(""))
			whereStmt += " AND conversion = 'Y'";
		else
			whereStmt += " WHERE conversion = 'Y'";
		
		try{
			openConnection();
			sql = selectStmt + fromStmt + joinStmt + whereStmt;
			System.out.println(sql);
		
			
			stmt = connect.prepareStatement(sql);
			
			ResultSet records = stmt.executeQuery();
			noOfConversions = records.getInt("total");
			
			return noOfConversions;
			
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return noOfConversions;
	}
	
	//returns total cost of the advertising campaign
	public double getTotalCost(ArrayList<String> filters, ArrayList<String> values){
		double totalCost = 0;
		
		String firstStmt = "SELECT SUM(clickCost) FROM clicks";
		String secondStmt = "SELECT SUM(impressionCost) FROM impressions";
		String joinStmt = generateJoinStmt("clicks", filters);
		firstStmt +=  joinStmt;
		joinStmt = generateJoinStmt("impressions", filters);
		secondStmt +=  joinStmt;
		String whereStmt = generateWhereStmt(filters,values,"clicks");
		firstStmt += whereStmt;
		whereStmt = generateWhereStmt(filters,values,"impressions");
		secondStmt+= whereStmt;

		try{
			openConnection();

			sql = "SELECT (" + firstStmt + ") + (" + secondStmt + ") AS total";
			System.out.println(sql);
			
			stmt = connect.prepareStatement(sql);
			
			ResultSet result = stmt.executeQuery();
			totalCost = result.getDouble("total");
			
			return totalCost;
			
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return totalCost;
	}
	
	public int getNoOfUniques(ArrayList<String> filters, ArrayList<String> values){
		int noOfUniques = 0;
		String tableName = "clicks";
		String selectStmt = "SELECT DISTINCT COUNT(clicks.id) AS 'total'";
		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName);
		
		try{
			openConnection();
			
			sql = selectStmt + fromStmt + joinStmt + whereStmt;
			System.out.println(sql);
			stmt = connect.prepareStatement(sql);
			
			ResultSet result = stmt.executeQuery();
			noOfUniques = result.getInt("total");
			System.out.println(noOfUniques);
			return noOfUniques;
			
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return noOfUniques;
	}
	
	public String getStartDate(ArrayList<String> filters, ArrayList<String> values, String tableName){
		String startDate = "";

		String selectStmt = "";
		if(tableName.equals("server"))
			selectStmt = "SELECT entryDate AS date";
		else
			selectStmt = "SELECT date";	

		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName);

		try{
			openConnection();
			sql = selectStmt + fromStmt + joinStmt + whereStmt + " ORDER BY date LIMIT 1";

			stmt = connect.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();
			startDate = result.getString("date");;

			return startDate;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return startDate;
	}

	public String getEndDate(ArrayList<String> filters, ArrayList<String> values, String tableName){
		String startDate = "";

		String selectStmt = "";
		if(tableName.equals("server"))
			selectStmt = "SELECT entryDate AS date";
		else
			selectStmt = "SELECT date";	

		String fromStmt = " FROM " + tableName;
		String joinStmt = generateJoinStmt(tableName, filters);
		String whereStmt = generateWhereStmt(filters,values,tableName);

		try{
			openConnection();
			sql = selectStmt + fromStmt + joinStmt + whereStmt + " ORDER BY date DESC LIMIT 1";

			stmt = connect.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();
			startDate = result.getString("date");;

			return startDate;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return startDate;
	}
	
	public double getBounceRate(ArrayList<String> filters, ArrayList<String> values, String bounceDef){
		return (double) getNoOfBounces(filters, values, bounceDef) / (double) getNoOfClicks(filters, values);
		
	}
	
	//returns the average no. of clicks per impression
	public double getCTR(ArrayList<String> filters, ArrayList<String> values){
		return (double) getNoOfClicks(filters, values) / (double) getNoOfImpressions(filters,values);
	}
	
	//returns the average money spent per conversion
	public double getCPA(ArrayList<String> filters, ArrayList<String> values){
		return getTotalCost(filters, values) / getNoOfConversions(filters, values);
	}
	
	//returns the average money spent per click
	public double getCPC(ArrayList<String> filters, ArrayList<String> values){
		return getTotalCost(filters, values) / getNoOfClicks(filters, values);
	}
	
	//returns the average money spent per one thousand impression
	public double getCPM(ArrayList<String> filters, ArrayList<String> values){
		return (getTotalCost(filters, values) * 1000 / getNoOfImpressions(filters,values));
	}
	
	//closes connection to database
	public void closeConnection(){
		try{
			if(stmt!=null)
				stmt.close();
			if(connect!=null){
				connect.setAutoCommit(true);
				connect.close();
			}
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}
	}
	
	private String generateJoinStmt(String tableName, ArrayList<String> filters){
		String joinStmt = "";
		boolean usersJoined = false;
		boolean impressionsJoined = false;
		if(filters != null){
			for(int i = 0; i < filters.size(); i++){
				if(!usersJoined && !filters.get(i).equals("context") && !filters.get(i).equals("date")){
					usersJoined = true;
					joinStmt += " INNER JOIN users ON users.id = "+ tableName +".id";
				}else if(!impressionsJoined && !tableName.equals("impressions") && !filters.get(i).equals("date")){
					impressionsJoined = true;
					joinStmt += " INNER JOIN impressions ON impressions.id = "+ tableName +".id";
				}
			}
		}
		return joinStmt;
	}
	
	//NOTE to do multiple values of same field do for example ->  filters as 'context' then values as 'News_Blog'
	//for two values or 'Blog_News_Shopping' for 3 values
	private String generateWhereStmt(ArrayList<String> filters, ArrayList<String> values, String tableName){
		String whereStmt = "";
		String[] dates;
		
		if(filters != null){
			
			for(int i = 0; i < filters.size(); i++){
				if(i==0)
					whereStmt += " WHERE ";
				else
					whereStmt += " OR ";
				
				if(filters.get(i).equals("date")){
					dates = values.get(i).split("_");
					if(tableName.equals("clicks"))
						whereStmt += "clicks.date >= '" + dates[0] + "' AND clicks.date <= '" + dates[1] + "'";
					else if(tableName.equals("impressions"))
						whereStmt += "impressions.date >= '" + dates[0] + "' AND impressions.date <= '" + dates[1] + "'";
					else if(tableName.equals("server"))
						whereStmt += "server.entryDate >= '" + dates[0] + "' AND server.exitDate <= '" + dates[1] + "'";
				}else{
					if(!values.get(i).contains("_")){
						whereStmt += filters.get(i) + " = '" + values.get(i) + "'";
					}else{
						String[] split = values.get(i).split("_");
						for(int k = 0; k < split.length; k++){
							if(k!=0)
								whereStmt += " OR ";
							whereStmt += filters.get(i) + " = '" + split[k] + "'";
						}
					}
				}
			}
		}
		return whereStmt;
	}
	
	
	public void getDatabaseDetails(){
		openConnection();
		DatabaseMetaData md;
		try {
			md = connect.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
		    System.out.println("DATABASE Name: " + md.getURL().split(":")[2]);
		    while (rs.next()) {
		      System.out.println("TABLE Name: "+rs.getString(3));
		    }
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}finally{
			try{
				if(connect!=null)
					connect.close();
			}catch(SQLException e){
				System.out.println("SQLException: " + e.getMessage());
			}
		}
	}
	
	public void testMetricsFor200Records(ArrayList<String> filters, ArrayList<String> values){
		System.out.println("No. of impressions:\nEXPECTED: 200 \nACTUAL: " + getNoOfImpressions(filters,values));
		System.out.println("No. of clicks:\nEXPECTED: 200 \nACTUAL: " + getNoOfClicks(filters,values));
		System.out.println("No. of bounces:\nEXPECTED: 66 \nACTUAL: " + getNoOfBounces(filters,values,""));
		System.out.println("No. of conversions:\nEXPECTED: 21 \nACTUAL: " + getNoOfConversions(filters,values));
		System.out.println("Total cost:\nEXPECTED: 935.6919199 \nACTUAL: " + String.format("%.2f", getTotalCost(filters,values)));
		System.out.println("Click-through-rate:\nEXPECTED: 1 \nACTUAL: " + getCTR(filters,values));
		System.out.println("Cost-per-acquisition:\nEXPECTED: 44.55675805 \nACTUAL: " + String.format("%.2f", getCPA(filters,values)));
		System.out.println("Cost-per-click: \nEXPECTED: 4.678459595 \nACTUAL: " + String.format("%.2f", getCPC(filters,values)));
		System.out.println("Cost-per-thousand-impressions: \nEXPECTED: 4678.459595 \nACTUAL: " + getCPM(filters,values));
		System.out.println("Bounce Rate: " + getBounceRate(filters,values,""));
		System.out.println("No. of Uniques: " + getNoOfUniques(filters,values));
	}

}