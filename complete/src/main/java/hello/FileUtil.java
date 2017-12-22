package hello;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
	
	private static final String FILENAME = "/Users/ashokarulsamy/projects/poc/REQUEST_XML_TRAIL_BALANCE.xml";

	public static String getFileDataAsString() {

		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder data = new StringBuilder();

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				
				data.append(sCurrentLine.trim());
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		
		return data.toString();

	}

	public static void writeFile(String fileName, String fileLocation, String content) {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(new StringBuilder().append(fileLocation).append(fileName).toString());
			bw = new BufferedWriter(fw);
			bw.write(content);
			
		} catch (IOException e) {

			e.printStackTrace();
			
			throw new RuntimeException(e.getMessage());

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}
	
	public static void deleteFile(String fileName, String fileLocation) {
    	
		try {

    		File file = new File(new StringBuilder().append(fileLocation).append(fileName).toString());

    		if(file.delete()){
    			log.info(file.getName() + " is deleted!");
    		}else{
    			log.info("Delete operation is failed.");
    		}

    	} catch(Exception e){

    		e.printStackTrace();

    	}

    }
	
	
}
