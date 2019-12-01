package smartspace;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import smartspace.layout.ActionBoundary;
import smartspace.layout.UserBoundaryKey;

@Profile("production")
@Component
public class SubscribeTimer implements CommandLineRunner {

	private RestTemplate restTemplate;

	@Override
	public void run(String... args) throws Exception {
		restTemplate = new RestTemplate();
		ActionBoundary boundary = new ActionBoundary();
		boundary.setActionKey(null);
		boundary.setPlayer(new UserBoundaryKey("2019b.rickyd", "myTimer@Timer"));
		boundary.setType("cheaperFlightFinder");

		while (true) {

			double currentTime = System.currentTimeMillis();
			currentTime = currentTime / 600;
			if ((currentTime % 30) == 0) {
				System.err.println(currentTime);// to delete
				restTemplate.postForObject("http://localhost:9052/smartspace/actions", boundary, Object.class);
			}
		}
	}

}
