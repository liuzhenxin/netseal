package cn.com.infosec.netseal.appapi.common.util;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SSLSocketFactoryUtil {

	protected KeyManager[] getKeyManagers(String keyStore, String keyStorePassword) throws Exception {

		// First, get the default KeyManagerFactory.
		String alg = KeyManagerFactory.getDefaultAlgorithm();
		KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);

		// Next, set up the KeyStore to use. We need to load the file into
		// a KeyStore instance.
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(keyStore);
			KeyStore ks = KeyStore.getInstance("jks");
			ks.load(fis, keyStorePassword.toCharArray());

			// Now we initialise the KeyManagerFactory with this KeyStore
			kmFact.init(ks, keyStorePassword.toCharArray());

			// And now get the KeyManagers
			KeyManager[] kms = kmFact.getKeyManagers();
			return kms;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	protected TrustManager[] getTrustManagers(String trustStore, String trustStorePassword) throws Exception {
		// First, get the default TrustManagerFactory.
		String alg = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);

		// Next, set up the TrustStore to use. We need to load the file into
		// a KeyStore instance.
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(trustStore);
			KeyStore ks = KeyStore.getInstance("jks");
			ks.load(fis, trustStorePassword.toCharArray());
			fis.close();

			// Now we initialise the TrustManagerFactory with this KeyStore
			tmFact.init(ks);

			// And now get the TrustManagers
			TrustManager[] tms = tmFact.getTrustManagers();
			return tms;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				fis.close();
		}

	}

	public SSLSocketFactory getSSLSocketFactory(Properties sysPro) throws Exception {

		String trustStore = sysPro.getProperty("javax.net.ssl.trustStore");
		String trustStorePass = sysPro.getProperty("javax.net.ssl.trustStorePassword");

		String keyStore = sysPro.getProperty("javax.net.ssl.keyStore");
		String keyStorePass = sysPro.getProperty("javax.net.ssl.keyStorePassword");

		// Call getTrustManagers to get suitable trust managers
		TrustManager[] tms = null;
		if (trustStore != null && trustStore.trim().length() > 0)
			tms = getTrustManagers(trustStore, trustStorePass);

		// Call getKeyManagers (from CustomKeyStoreClient) to get suitable
		// key managers
		KeyManager[] kms = null;
		if (keyStore != null && keyStore.trim().length() > 0)
			kms = getKeyManagers(keyStore, keyStorePass);

		// Next construct and initialise a SSLContext with the KeyStore and
		// the TrustStore. We use the default SecureRandom.
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(kms, tms, null);

		// Finally, we get a SocketFactory, and pass it to SimpleSSLClient.
		SSLSocketFactory ssf = context.getSocketFactory();
		return ssf;
	}

	public ServerSocketFactory getSSLServerSocketFactory(Properties sysPro) throws Exception {

		String trustStore = sysPro.getProperty("javax.net.ssl.trustStore");
		String trustStorePass = sysPro.getProperty("javax.net.ssl.trustStorePassword");

		String keyStore = sysPro.getProperty("javax.net.ssl.keyStore");
		String keyStorePass = sysPro.getProperty("javax.net.ssl.keyStorePassword");

		// Call getTrustManagers to get suitable trust managers
		TrustManager[] tms = null;
		if (trustStore != null && trustStore.trim().length() > 0)
			tms = getTrustManagers(trustStore, trustStorePass);

		// Call getKeyManagers (from CustomKeyStoreClient) to get suitable
		// key managers
		KeyManager[] kms = null;
		if (keyStore != null && keyStore.trim().length() > 0)
			kms = getKeyManagers(keyStore, keyStorePass);

		// Next construct and initialise a SSLContext with the KeyStore and
		// the TrustStore. We use the default SecureRandom.
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(kms, tms, null);

		// Finally, we get a SocketFactory, and pass it to SimpleSSLClient.
		ServerSocketFactory ssf = context.getServerSocketFactory();
		return ssf;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
