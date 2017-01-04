import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultipartUtil {
    private String boundaryString;
    private HttpURLConnection urlConnection;
    private OutputStream outputStreamToRequestBody;
    private BufferedWriter httpRequestBodyWriter;

    private static final String boundaryPrefix = "--";
    private static final String boundarySuffix = "--\r\n";
    private static final String lineBreak = "\r\n";
    private static final String doubleLineBreak = "\r\n\r\n";

    public MultipartUtil(URL url){

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            boundaryString = "--------" + System.currentTimeMillis();

            // Indicate that we want to write to the HTTP request body
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);

            //To write binary ojects like files into the output stream
            outputStreamToRequestBody = urlConnection.getOutputStream();

            //To write strings into the output stream
            httpRequestBodyWriter =
                    new BufferedWriter(new OutputStreamWriter(outputStreamToRequestBody));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendForm(String formName, String formContent) throws IOException {
        httpRequestBodyWriter.write(boundaryPrefix + boundaryString + lineBreak);
        httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"" + formName + "\"");
        httpRequestBodyWriter.write(doubleLineBreak);
        httpRequestBodyWriter.write(formContent + lineBreak);
        httpRequestBodyWriter.flush();
    }

    public void sendFormFile(String fileName) throws IOException {
        File logFileToUpload = new File(fileName);

        httpRequestBodyWriter.write(boundaryPrefix + boundaryString + lineBreak);
        httpRequestBodyWriter.write("Content-Disposition: form-data;"
                + "name=\"fileAttached\";"
                + "filename=\"" + fileName + "\""
                + "\r\nContent-Type:" + URLConnection.guessContentTypeFromName(fileName) + doubleLineBreak);
        httpRequestBodyWriter.flush();

        FileInputStream inputStreamToLogFile = new FileInputStream(logFileToUpload);

        int bytesRead;
        byte[] dataBuffer = new byte[1024];
        while ((bytesRead = inputStreamToLogFile.read(dataBuffer)) != -1) {
            outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
        }

        httpRequestBodyWriter.write(lineBreak);
        httpRequestBodyWriter.flush();
    }

    public void finish() throws IOException {
        // Mark the end of the multipart http request
        httpRequestBodyWriter.write(boundaryPrefix + boundaryString + boundarySuffix);
        httpRequestBodyWriter.flush();

        // Close the streams
        outputStreamToRequestBody.close();
        httpRequestBodyWriter.close();
    }

    public void printResponse() throws IOException {
        int status = urlConnection.getResponseCode();
        System.out.println(status);
        System.out.println();
        BufferedReader httpResponseReader;
        if (status >=200 && status <=299) {
            // Read response from web server, which will trigger the multipart HTTP request to be sent.
            httpResponseReader =
                    new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } else {
            //Read the error response from the server
            httpResponseReader =
                    new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        }
        String lineRead;
        while ((lineRead = httpResponseReader.readLine()) != null) {
            System.out.println(lineRead);
        }
    }
}
