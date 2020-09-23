package com.sbeaney.pl_e;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class GetProductLists {

    private final Logger logger = LoggerFactory.getLogger(GetProductLists.class);

    private void getFileUrls(String urlStr, String dirStr) {
        try {
            Document doc = Jsoup.connect(urlStr).get();
            String[] f = doc.toString().split("\n");
            for (String l : f) {
                if (l.contains("class=\"file-title\"")) {
                    if (l.contains("href=\"") ) {
                        int a = l.indexOf("href=\"") + 6;
                        int b = l.substring(a).indexOf("\"");
                        String targetUrl = l.substring(a, a + b);
                        String targetFile = dirStr + "/" + targetUrl.substring(targetUrl.lastIndexOf("/") + 1);
                        HttpURLConnection connection = getHttpURLConnection(targetUrl);
                        int code = connection.getResponseCode();
                        if (code == 301) {
                            Map<String, List<String>> headers = connection.getHeaderFields();
                            logger.warn("url relocated "+targetUrl + " to " + headers.get("Location").get(0) );
                            connection = getHttpURLConnection(headers.get("Location").get(0));
                            code = connection.getResponseCode();
                        }
                        if( code == 200 ) {
                            logger.info("copying " + connection.getURL().toString() + " to "+targetFile );
                            getData(targetFile, connection);
                        } else {
                            logger.error(" failed to connect to url " + connection.getURL());
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("FATAL product list url not found for broken\n"+e);
        }
    }

    private HttpURLConnection getHttpURLConnection(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    private void getData(String targetFile, HttpURLConnection connection) throws IOException {
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
        fileOutputStream.close();
        in.close();
    }

    public static void main(String[] args) {
        GetProductLists getProductLists = new GetProductLists();
        getProductLists.getFileUrls( args[0], args[1] );
    }


}
