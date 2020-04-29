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
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class Post {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(new File("laza403.json"), new TypeReference<Map<String, Object>>() {
        });

        String urlParameters = new ObjectMapper().writeValueAsString(map);
        String targetURL = "https://f6a7a1c896a782e004ee9325ab768f44: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@portofino-kozmetika.myshopify.com/admin/products.json";

        String response = executePostV2(targetURL, urlParameters);
    }

    // This POST shopify product does not work it receives 401 response, I do not know why.
    // ToDo: Investigate problem
    public static String executePostV1(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        System.out.println("Route: " + targetURL);

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Cookie", "__cfduid=dddfa2cf29c54ff6d80cc630c2b1cfc801587751676");
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            System.out.println(connection.getResponseMessage());
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

    // This is POST shopify product with java unirest library
    // http://kong.github.io/unirest-java/
    // ToDo: Test logic and Integrate in app flow
    public static String executePostV2(String url, String body) {
        try {
            HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .header("Content-Type", "text/plain")
                    .header("Cookie", "__cfduid=dddfa2cf29c54ff6d80cc630c2b1cfc801587751676")
                    .body(body)
                    .asString();
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
