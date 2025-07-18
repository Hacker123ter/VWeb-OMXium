package omxium.server;

import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import omxium.ui.AppWindow;


public class LocalServer extends NanoHTTPD {
    private static final String RESOURCE_ROOT = "/html";

    public LocalServer(int port) throws IOException {
        super(port);
        start(SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String ua = session.getHeaders().get("user-agent");
        if (ua == null || !ua.contains("OmxiumWebView/1.0")) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
        }

        String token = session.getHeaders().get("x-omxium-token");
        if (!AppWindow.AUTH_TOKEN.equals(token)) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
        }

        String uri = session.getUri();
        if (uri.equals("/")) uri = "/start.html";

        if (uri.endsWith(".php")) {
            return servePhp(uri);
        }

        InputStream in = getClass().getResourceAsStream(RESOURCE_ROOT + uri);
        if (in == null) {
            InputStream errorPage = getClass().getResourceAsStream(RESOURCE_ROOT + "/error404.html");
            if (errorPage != null) {
                return newChunkedResponse(Response.Status.NOT_FOUND, "text/html; charset=UTF-8", errorPage);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
            }
        }
        String mime = guessMimeType(uri);
        return newChunkedResponse(Response.Status.OK, mime, in);
    }

    private Response servePhp(String uri) {
        try {
            InputStream template = getClass().getResourceAsStream(RESOURCE_ROOT + uri);
            if (template == null) return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 PHP Not Found");

            Path tmp = Files.createTempFile("nano-", ".php");
            Files.copy(template, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            ProcessBuilder pb = new ProcessBuilder("php", tmp.toAbsolutePath().toString());
            Process p = pb.start();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream is = p.getInputStream()) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) > 0) out.write(buf, 0, r);
            }
            p.waitFor();

            byte[] content = out.toByteArray();
            return newFixedLengthResponse(Response.Status.OK, "text/html; charset=UTF-8", new ByteArrayInputStream(content), content.length);
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "500 Internal Server Error\n" + e.getMessage());
        }
    }

    private String guessMimeType(String uri) {
        if (uri.endsWith(".html")) return "text/html; charset=UTF-8";
        if (uri.endsWith(".css"))  return "text/css";
        if (uri.endsWith(".js"))   return "application/javascript";
        if (uri.endsWith(".png"))  return "image/png";
        if (uri.endsWith(".jpg")||uri.endsWith(".jpeg")) return "image/jpeg";
        if (uri.endsWith(".woff"))  return "font/woff";
        if (uri.endsWith(".woff2")) return "font/woff2";
        if (uri.endsWith(".ttf"))   return "font/ttf";
        if (uri.endsWith(".otf"))   return "font/otf";
        return "application/octet-stream";
    }
}
