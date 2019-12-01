package smartspace.skyscannerAPI;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class SkyScannerAPIConnectivityTest {

	@Test
	public void testCreateSession() throws Exception {
		int createdStatusCode = 201;
		// GIVEN I have a sky scanner API


		// WHEN I create Session (POST)

		HttpResponse<JsonNode> response = createSessionWithSkyScanner();

		// System.err.println(response.getStatus() + " " + response.getStatusText());
		// System.err.println(response.getHeaders().getFirst("Location"));

		// THEN I get Status Code: 201 as a Response
		// AND Location header is not Empty

		assertThat(response.getStatus()).isEqualTo(createdStatusCode);
		assertThat(response.getHeaders().getFirst("Location")).isNotEmpty();

	}

	@Test
	public void testGetSessionKey() throws Exception {
		// GIVEN I created Session with Sky Scanner (POST)

		HttpResponse<JsonNode> response = createSessionWithSkyScanner();
		System.err.println(response.getStatus());
		// WHEN I replace notNeededpath with "" I get the sessions key
		String sessiongKey = getSessionKey(response.getHeaders().getFirst("Location"));

		// THEN sessiongKey is not empty
		// AND seesionKey is substringOf Location Header (where the session key is)

		// System.err.println(response.getHeaders().getFirst("Location"));
		// System.err.println(sessiongKey);

		assertThat(sessiongKey).isNotEmpty();
		assertThat(sessiongKey).isSubstringOf(response.getHeaders().getFirst("Location"));

	}

	@Test
	public void testGetFlight() throws Exception {
		int responseStatusCode = 200;
		// GIVEN I created Session with Sky Scanner (POST)
		// AND I have the sessionKey
		

		String sessionKey = getSessionKey(createSessionWithSkyScanner().getHeaders().getFirst("Location"));
		// WHEN I GET itineraries from a created session
		
		HttpResponse<JsonNode> response = getItineraries(sessionKey);
		// Then the response code is 200 OK
		// AND Response body is not Null
		
//		System.err.println(response.getBody());
//		System.err.println(response.getStatus() +" "+ response.getStatusText());
		assertThat(response.getStatus()).isEqualTo(responseStatusCode);
		assertThat(response.getBody()).isNotNull();
	}

	private HttpResponse<JsonNode> createSessionWithSkyScanner() throws UnirestException {

		String[] APIkey = { "829af922c7mshb81cfc1a0f08c2fp13565ejsna07216e7de10",
				"7fa5858dcfmshd98f8fad2777c6cp1c7f54jsncbd98348b863",
				"57e4777b3fmsh99c19fe9fa25aa5p16ae04jsn617bad3df955",
				"aae4ef1091msh498cd400a0ed953p1182afjsnd2f89de056e4"};

		Random rand = new Random();
		int n = rand.nextInt(100);

		return Unirest.post("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0")
				.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
				.header("X-RapidAPI-Key", APIkey[n % 4])
				.header("Content-Type", "application/x-www-form-urlencoded")
				.field("inboundDate", "2019-09-10")
				.field("cabinClass", "business")
				.field("children", 0)
				.field("infants", 0)
				.field("country", "US")
				.field("currency", "USD")
				.field("locale", "en-US")
				.field("originPlace", "SFO-sky")
				.field("destinationPlace", "LHR-sky")
				.field("outboundDate", "2019-09-01")
				.field("adults", 1)
				.asJson();
	}

	private String getSessionKey(String location) {
		final String notNeededpath = "http://partners.api.skyscanner.net/apiservices/pricing/uk2/v1.0/";
		return location.replace(notNeededpath, "");
	}
	
	private HttpResponse<JsonNode> getItineraries(String sessionKey) throws UnirestException{
		return Unirest
				.get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/uk2/v1.0/"
						+ sessionKey + "?pageIndex=0&pageSize=10")
				.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
				.header("X-RapidAPI-Key", "829af922c7mshb81cfc1a0f08c2fp13565ejsna07216e7de10")
				.asJson();
	}

}
