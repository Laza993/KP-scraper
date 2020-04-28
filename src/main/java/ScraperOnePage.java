import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ScraperOnePage {

    public static void main(String[] args) throws Exception {
    	int counter = 2325;
    	String pageLink = "https://www.kupujemprodajem.com//elektronika-i-komponente/sijalice-i-led-rasveta/led-diode-umetak-limeni-32w-toplo-bela/oglas/27000812";

    	producToJSON(pageLink, counter);
   	
    }
    
    
    public static void producToJSON(String link, int prodNum) throws Exception {
		final Document document = Jsoup.connect(link).timeout(3000).get();
		
		//data
		
    	String title = document.select("h1.oglas-title").text();
    	String product_type = filterProduct_type(document);
    	String someTags = document.select("div.breadcrumbs a.crumbs").text();

    	String body_html = filteredBody(document, prodNum); 			
    	String[] tags = (title.replaceAll("[^\\w\\s]"," ") + someTags.replaceAll("[^\\w\\s]"," ")).split(" ");
    	
    	
    	final String vendor = "061 63 66 254 minutshop.com";
    	final Boolean published = false;
    	ArrayList images = getImages(document, title);
    	ArrayList variantsList = getVariants(document);
    	   
    	//answer
    	Map<String, Object> product = new LinkedHashMap<String, Object>();
    	product.put("title", title);
    	product.put("body_html", body_html);
    	product.put("vendor", vendor);
    	product.put("product_type", product_type);
    	product.put("images", images);
    	product.put("published", published);
    	product.put("tags", tags);
    	product.put("variants", variantsList);
    	
    	//conversion into JSON file
    	     	
		String jsonOdgovor = new ObjectMapper().writeValueAsString(product);
		System.out.println(jsonOdgovor);
    	
    	Map<String, Object> answer = new LinkedHashMap<String, Object>();
    	answer.put("product", product);
		
    	String filePath = "C:\\Users\\laza\\Desktop\\java scraper\\imdb-scraper-master\\lazaJSONs\\laza" + prodNum + ".json";
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(Paths.get(filePath).toFile(), answer);
	
	}

    public static int priceCounter(Document document) {
    	int price = 0;
    	
    	if(document.select("div.price-holder").html().split(" ").length <= 1 ) {
//    		System.out.println(document.select("div.price-holder").html().split(" ").length);
    		return 0;
    	}
    	if(document.select("div.price-holder").html().contains("Kontakt")) {
//    		System.out.println("nema cene");
    		return 0;
    	}
    	if(document.select("div.price-holder").html().contains("&nbsp;")){
    		String cena = (document.select("div.price-holder").html().split(" "))[1];

        	String cifra = (cena.split("&nbsp;"))[0];
        	String valuta = (cena.split("&nbsp;"))[1];
        	String filtriran;
    	
        	if((cifra.split("\\.")).length == 2) {
        		filtriran = (cifra.split("\\."))[0] + (cifra.split("\\."))[1];
        	} else if((cifra.split(",")).length == 2) {
        		filtriran = (cifra.split(","))[0];
        	} else {
        		filtriran = cifra;
        	}
        	
        	price = Integer.valueOf(filtriran);
        	
        	if(valuta.equals("€")) {
        		price = price * 118;
        	}
    	}
    	return price;
    }
    
    public static ArrayList getImages(Document document, String title) throws IOException {
    	
    	//Slikeeeeeeeeeeeeeeeeeeeeee 
    	
    	String baseImgPath = "https://images.kupujemprodajem.com/";
    	String baseKPath = "https://www.kupujemprodajem.com";
    	
    	Elements docIMGpath = document.select("div.fixed-big-ad-image-holder a");
    	String imgSource = baseKPath + docIMGpath.attr("href"); 
    	
    	Document docIMG = Jsoup.connect(imgSource).timeout(10000).get();
	
    	ArrayList images = new ArrayList();
    	
    	for(Element row : docIMG.select("#thumbs-holder-inner a")) {    		
    		String path = baseImgPath + row.attr("photo-path");
    		Map<String, Object> image = new LinkedHashMap<String, Object>();
    		image.put("alt", title);
        	image.put("src", path);
        	images.add(image);
    	}
    	return images;
    	}
 
    public static String filteredBody(Document document, int prodNum) {
    	StringBuffer retVal = new StringBuffer("<p><em><span>Artikal 0"
    			+ prodNum
    			+ "</span></em></p>\n" + 
    			"<p> </p>");
    	
    	String toFilter = document.select("div.oglas-description").html();
    	
    	if(toFilter.split("<p>Opis u izradi pitajte za sve što Vas zanima</p>").length > 1 ) {
    		toFilter = toFilter.split("<p>Opis u izradi pitajte za sve što Vas zanima</p>")[0];
    	}
    	if(toFilter.split("<p>Pošaljite poruku (preko KUPUJEMPRODAJEM) sa Vašim podacima: </p>").length > 1 ) {
    		toFilter = toFilter.split("<p>Pošaljite poruku (preko KUPUJEMPRODAJEM) sa Vašim podacima: </p>")[0];
    	}

    	retVal.append(toFilter);

    	if(priceCounter(document) == 0) {
    		retVal.append("<p>Cena Kontakt<br></p>\n");
    	}
    	   	
    	retVal.append("<p>Opis u izradi pitajte za sve što Vas zanima<br></p>\n" + 
    			"<p>Pošaljite poruku  sa Vašim podacima:</p>\n" + 
    			"<p><strong>Ime i prezime.                                                       </strong><br><strong>Ulica i broj.                                                       </strong><br><strong>Mesto i poštanski broj.                                                       </strong><br><strong>Broj telefona za kontakt.                             ili sms na broj 061 63 66 254</strong></p>\n" + 
    			"<br> <br>");
    	
    	return retVal.toString();
    }
    
    public static String filterProduct_type(Document document) {
    	String product_type = document.select("h1.oglas-title").text();
    	if(product_type.split(" ").length > 1) {	
    		product_type = product_type.split(" ")[0];
    		}
    	return product_type;
    }
    
    public static ArrayList getVariants(Document document) {
    	ArrayList variantsList = new ArrayList();
    	
    	int price = priceCounter(document);
  
    	Map<String, Object> variants = new LinkedHashMap<String, Object>();
    	variants.put("price", price);
    	variants.put("currency_code", "RSD");
    	
    	variantsList.add(variants);
    	
    	return variantsList;
    }
 
}
