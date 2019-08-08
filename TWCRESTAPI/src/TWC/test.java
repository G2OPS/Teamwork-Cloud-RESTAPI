package TWC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class test {
    public static void main(String[] args) {

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try{
            bypassCertificate();
        }catch (Exception e){

        }

        try{
            Authenticator.setDefault(new TWCAuthenticator());
            URL url = new URL("https://192.168.56.101:8111/osmc/admin/users");

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.addRequestProperty("Content-Type","application/json");


            int responseCode = con.getResponseCode();
            System.out.println(responseCode);
            if(responseCode > 299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();

            } else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
            }
            System.out.println(responseContent.toString());


        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }



    }

    public static void bypassCertificate() throws Exception{
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                //return new X509Certificate[0];
                return null;
            }
        }};

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null,trustAllCerts,new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static class TWCAuthenticator extends Authenticator{
        protected PasswordAuthentication getPasswordAuthentication(){

            //Get information about the request
            String prompt = getRequestingPrompt();
            String hostname = getRequestingHost();
            InetAddress ipaddr = getRequestingSite();
            int port = getRequestingPort();

            String username = "Administrator";
            String password = "feQQ3";

            // Return the information that is used by the authenticator
            return new PasswordAuthentication(username,password.toCharArray());


        }
    }





}
