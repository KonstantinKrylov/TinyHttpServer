package http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class HttpResponse {
    private static final Logger logger = LogManager.getLogger(HttpResponse.class);
    private static final String VERSION = "HTTP/1.1";

    byte[] body;

    List<String> headers = new ArrayList<>();

    public HttpResponse(HttpRequest request) throws IOException {
        final String method = request.getMethod();


        switch (method) {
            case "GET":
                final String url = request.getUrl();

                final Path path = Paths.get(".",url);

                if (!Files.exists(path)) {
                    fillHeaders(HttpStatus.NOT_FOUND);
                    fillBody("<h1>The requested resource IS not found</h1>");
                    return;
                }

                if (Files.isDirectory(path)) {
                    fillHeaders(HttpStatus.OK);
                    List<String> temp = new ArrayList<>();
                    Files.list(path).forEach(childPath -> temp.add("<a href=\"" + childPath.getFileName() + "/" + "" + "\">"
                            + childPath.getFileName() + "</a><br>"));
                    String bodyFiller = "";
                    for (String s : temp) {
                        if(s.indexOf(".DS_Store")==-1)bodyFiller += s + " ";
                    }
                    fillBody(bodyFiller);
                } else {
                    sendFile(path);
                }

                break;
            case "POST":
                break;
            default:
                break;
        }
    }

    private void sendFile(Path path) {

        try {

            body = Files.readAllBytes(path);
            fillHeaders(HttpStatus.OK);

        } catch (IOException e) {
            logger.error("", e);
            fillHeaders(HttpStatus.SERVER_ERROR);
            fillBody("<p>Error showing file<p>");
        }
    }

    public void write(OutputStream os) throws IOException {
        // write headers
        headers.forEach(s -> writeString(os, s));
        // write empty line
        writeString(os, "");
        // write body
        os.write(body);
    }

    private void writeString(OutputStream os, String str) {
        try {
            os.write((str + "\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    void fillHeaders(HttpStatus status) {
        headers.add(VERSION + " " + status);
        headers.add("Server: Simple Java Core November Http Server");
        headers.add("Connection: close");
    }

    private void fillBody(String str) {
        body = str.getBytes(StandardCharsets.UTF_8);
    }
}
