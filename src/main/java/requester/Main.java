package requester;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
	private String getXmlContent() {
		return "<note>\n" +
				"<to>Tove</to>\n" +
				"<from>Jani</from>\n" +
				"<heading>Reminder</heading>\n" +
				"<body>Don't forget me this weekend!</body>\n" +
				"</note>";
	}

	private String streamToString(InputStream inp) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(inp));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	private void showResponse(InputStream inp) throws IOException {
		System.out.println("Response from the service:");
		String response = streamToString(inp);
		System.out.println(response);
		System.out.println();

		System.out.println("Extracted YAML content:");
		JSONObject jsono = new JSONObject(response);
		String yamlContent = jsono.getString("yamlContent");
		System.out.println(yamlContent);
		System.out.println();
	}

	private void makeRequest(String someXmlContent) {
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("http://localhost:9000/xml-to-yaml");
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("xmlbody", someXmlContent));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (InputStream instream = entity.getContent()) {
					showResponse(instream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Some exception occured");
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Main app = new Main();
		String xmlContent = app.getXmlContent();
		app.makeRequest(xmlContent);
		System.out.println("Request was sent");
	}

}
