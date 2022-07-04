
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

public class ProxyTest {
	
	public static void main(String[] args) throws Exception {
		
//		HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(HttpHost.create("http://proxy:vie8Oose8GeH3sh@127.0.0.1:80"));

		
		CloseableHttpAsyncClient client = HttpAsyncClients.custom()
				.setProxy(HttpHost.create("https://127.0.0.1:80"))
				.setDefaultHeaders(null)
				.build();
	    client.start();

	    HttpGet request = new HttpGet("http://www.google.com");
	    
	    Future<HttpResponse> future = client.execute(request, null);
	    HttpResponse response = future.get();
	    System.err.println(response.getStatusLine());
	    client.close();
	    

	}

}
