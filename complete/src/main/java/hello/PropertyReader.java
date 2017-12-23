package hello;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyReader {
	
	static Map<String, String> getConfigDetail() {
		Properties prop = new Properties();
		InputStream input = null;
		Map<String, String> propertyData = null;

		try {

			//input = new FileInputStream(Constants.CONFIG_FILE);
			//input = new FileInputStream("/Users/ashokarulsamy/projects/poc/tally_client_upload_config.properties");
			input = new FileInputStream("c:/test_tally/stock_upload.properties"); //stock upload
			//input = new FileInputStream("c:/test_tally/day_book_upload.properties"); //day book upload
			
			// load a properties file
			prop.load(input);
			
			propertyData = new HashMap<String, String>();
			
			//propertyData.put(Constants.AWS_URL, prop.getProperty(Constants.AWS_URL));
			propertyData.put(Constants.TALLY_URL, prop.getProperty(Constants.TALLY_URL));
			//propertyData.put(Constants.TALLY_STOCK, prop.getProperty(Constants.TALLY_STOCK));
			propertyData.put(Constants.TALLY_DAY_BOOK, prop.getProperty(Constants.TALLY_DAY_BOOK));
			propertyData.put(Constants.REQUEST_LIST, prop.getProperty(Constants.REQUEST_LIST));
			propertyData.put(Constants.FILE_PATH, prop.getProperty(Constants.FILE_PATH));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return propertyData;
	}
	
	static List<String> getRequestList(String requestList) {
		
		List<String> requests = null;
		
		try {
			requests = new ArrayList<String>();
			requests = Arrays.asList(requestList.split(","));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return requests;
	}

}
