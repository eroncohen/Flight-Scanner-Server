package smartspace.plugin;

import java.util.Date;

import org.springframework.lang.Nullable;

import smartspace.data.ElementEntity;

public interface FlightAgentService {

	public ElementEntity FindFlight(Date inbound, @Nullable Date outbound, String origin, String dest,
			ElementEntity route,String currency);

	public double cheaperFlight(Date inbound, @Nullable Date outbound, String origin, String dest,String currency);
}
