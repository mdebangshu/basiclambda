package org.aws.simplejava.basiclambda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.aws.simplejava.basiclambda.model.Country;
import org.aws.simplejava.basiclambda.model.WorldTime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * International Time Handler AWS Lambda Function
 *
 */
public class InternationalTimeHandler implements RequestHandler<Country, WorldTime> {

	public WorldTime handleRequest(Country country, Context context) {
		try {

			URL url = new URL("http://worldtimeapi.org/api/timezone/Europe/" + country.getCountry());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuilder build = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				build.append(inputLine);
			}
			in.close();
			ObjectMapper mapper = new ObjectMapper();
			WorldTime worldTime = mapper.readValue(build.toString(), WorldTime.class);
			conn.disconnect();

			return worldTime;
		} catch (Exception e) {
			throw new RuntimeException("Unable to process request");
		}
	}
}
