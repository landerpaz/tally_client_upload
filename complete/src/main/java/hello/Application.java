package hello;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;


@SpringBootApplication
public class Application {

	@Autowired
	S3Services s3Services;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		ApplicationContext context = SpringApplication.run(Application.class);
		System.exit(SpringApplication.exit(context));
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		
		return args -> {
			
			log.info("Reading config file started...");
			Map<String, String> configDetail = PropertyReader.getConfigDetail();
			
			if(null == configDetail || configDetail.size() < 1) {
				throw new Exception("Config detail not found!!!!!!!!!");
			}
			
			log.info("Reading config file completed.");
			
			//log.info("*********Config details***************");
			/*configDetail.forEach((key, value) -> {
			    log.info("Key : " + key + "  || Value : " + value);
			});*/
			
			List<String> requestList = PropertyReader.getRequestList(configDetail.get(Constants.REQUEST_LIST));
			
			if(null == requestList || requestList.size() < 1) {
				throw new Exception("Request list not valid!!!");
			}
			
			//log.info("*********Tally request list***************");
			//log.info(Arrays.toString(requestList.toArray()));
			
			for(String tallyRequest : requestList) {
				
				//get data from tally
				log.info("Retreiving data from Tally for " + tallyRequest  + ".................");
				//log.info("Tally request : " + configDetail.get(tallyRequest));
				
			    HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.APPLICATION_XML);
			    HttpEntity<String> request = new HttpEntity<String>(configDetail.get(tallyRequest), headers);
			    
			    /*uncomment before commit in git START*/
			    //read from tally response
			    ResponseEntity<String> response = restTemplate.postForEntity(configDetail.get(Constants.TALLY_URL), request, String.class);
			    
			    //log.info(response.getBody().toString());
			    
			    if(null != response && null != response.getStatusCode() ) {
			    	log.info("Response from Tally : " + response.getStatusCode().toString());
			    }
			    
			    log.info("Data retrived from tally successfully!!!");
			    
			    //parse tally response to remove unwanted special characters
			    log.info("Parsing Tally response");
			    String tallyResponse = response.getBody().toString();
			    tallyResponse = tallyResponse.replaceAll("&#", "");
			    tallyResponse = tallyResponse.replaceAll("#&", "");
			    tallyResponse = tallyResponse.replaceAll("UDF:_UDF", "UDF_UDF");
			    
			    if(null == tallyResponse || tallyResponse.length() < 1) {
			    	log.info("No valid response from tally...");
			    	return;
			    }
			    log.info("Parsed Tally response");
			    	
		    	String fileName = new StringBuilder().append(configDetail.get(Constants.COMPANY_ID)).append(Constants.FILE_DELIMITER).append(tallyRequest).append(Constants.FILE_DELIMITER).append(Util.getCurrentdate()).append(Constants.FILE_EXTENSION).toString();
		    	
		    	//write file
		    	log.info("Creating file " + fileName);
		    	FileUtil.writeFile(fileName, configDetail.get(Constants.FILE_PATH), tallyResponse);
		    	log.info("Created file " + fileName);
		    	
		    	//upload file - pass file key and file name with full path
		    	log.info("Uploading file " + fileName);
		    	s3Services.uploadFile(fileName, new StringBuilder().append(configDetail.get(Constants.FILE_PATH)).append(fileName).toString());
		    	log.info("Uploaded file " + fileName);
		    	
		    	//delete file
		    	log.info("Deleting file " + fileName);
		    	FileUtil.deleteFile(fileName, configDetail.get(Constants.FILE_PATH));
		    	log.info("Deleted file " + fileName);
		    	
			}
		    
		};
	}
	
}