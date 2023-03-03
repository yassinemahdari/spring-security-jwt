package ma.hps.powercard.compliance.serviceimpl.spec.monitoring;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Statement;
import javax.naming.NamingException;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.log4j.Logger;


@Service
class HealthCheckService {

	private final static Logger logger = Logger.getLogger(HealthCheckService.class);

	private static RestTemplate httpClient;

	@Autowired(required=false)
	@Qualifier("healthCheckDatasource")
	private DataSource dataSource;

	public boolean isDBUp() {

		if (this.dataSource == null)
			return false;
		
		String sql = "SELECT 1 FROM DUAL";
		Connection con = null;
        try {
            con = this.dataSource.getConnection();
            Statement stm = con.createStatement();
            stm.executeQuery(sql);
            return true;
        }
        catch (Exception e) {
            logger.error("Cannot connect to DB. " + e.getMessage());
        }
        finally {
			try {
				if (con != null) con.close();
			} catch (Exception e) {
				logger.error("Could not close connection. " + e.getMessage());
			}
		}
		return false;
	}

	public boolean isUIUp(String url) {
		try {
			return HttpStatus.OK == httpClient().getForEntity(url, String.class).getStatusCode();
		} catch (Exception e) {
			return false;
		}
	}
	
	private static RestTemplate httpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

		if (httpClient != null)
			return httpClient;

		TrustStrategy acceptingTrustStrategy = new TrustSelfSignedStrategy();
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory =	new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		HealthCheckService.httpClient = restTemplate;
		return restTemplate;
	}
	
}

@Configuration
class HealthCheckDatasourceConfig {
	
	private final static Logger logger = Logger.getLogger(HealthCheckDatasourceConfig.class);
	
	@Value("${PWC.HEALTHCHECK.DATASOURCE.NAME:#{'java:/jdbc/HEALTHCHECK_DS'}}")
	private String dataSourceName;

	@Bean(name="healthCheckDatasource")
	public DataSource dataSource() {
		try {
			return (DataSource) new JndiTemplate().lookup(this.dataSourceName);
		} catch (NamingException e) {
			logger.error("Could not find datasource: " + this.dataSourceName);
			return null;
		}
	}

}

