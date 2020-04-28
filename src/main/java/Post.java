import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Post {
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(new File("laza403.json"), new TypeReference<Map<String,Object>>(){});
		
		String urlParameters = new ObjectMapper().writeValueAsString(map);
		String targetURL = "https://f6a7a1c896a782e004ee9325ab768f44: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@portofino-kozmetika.myshopify.com/admin/products.json";
		
//		System.out.println(urlParameters);
		executePost(targetURL, urlParameters);
		
	}
	
	
	public static String executePost(String targetURL, String urlParameters) {
		  HttpURLConnection connection = null;

		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", "application/json; utf-8");
		    connection.setRequestProperty("Accept", "*/*");
		    connection.setRequestProperty("Content-Length", 
		    Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("Content-Language", "en-US");  
		    connection.setRequestProperty("Cookie", "__cfduid=dfeb4187f318ba68e6587acbcfab80af71588024068");
		    connection.setRequestProperty("Cache-Control", "no-cache");
		    connection.setRequestProperty("User-Agent", "PostmanRuntime/7.24.1");
		    connection.setRequestProperty("Connection", "keep-alive");
		    connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		    
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.close();

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		    
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		}
}
