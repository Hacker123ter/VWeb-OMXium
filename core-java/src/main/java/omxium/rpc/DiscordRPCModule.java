package omxium.rpc;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscordRPCModule {

    private static Process nodeProcess;
    private static Path tempDir;

    public static void start() {
        if (!isNodeInstalled()) {
            promptInstallNode();
            return;
        }

        if (nodeProcess != null && nodeProcess.isAlive()) return;

        try {
            tempDir = Files.createTempDirectory("discord_rpc");
            copyResources(tempDir);

            ProcessBuilder pb = new ProcessBuilder("node", "discord_rpc_bridge.js");
            pb.directory(tempDir.toFile());
            pb.redirectErrorStream(true);

            nodeProcess = pb.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(nodeProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Node.js] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (nodeProcess != null && nodeProcess.isAlive()) {
            nodeProcess.destroy();
        }

        if (tempDir != null) {
            try {
                deleteDirectoryRecursively(tempDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static boolean isNodeInstalled() {
        try {
            Process p = new ProcessBuilder("node", "--version")
                    .redirectErrorStream(true)
                    .start();
            if (!p.waitFor(3, TimeUnit.SECONDS)) {
                p.destroy();
                return false;
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static void promptInstallNode() {
        String url = "https://nodejs.org/en/download/";
        System.err.println("Node.js не найден. Пожалуйста, установите его: " + url);
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Runtime.getRuntime().exec(new String[] { "xdg-open", url });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void copyResources(Path outputDir) throws IOException, URISyntaxException {
        URI uri = Objects.requireNonNull(DiscordRPCModule.class.getResource("/module/DiscordRpc")).toURI();
        FileSystem fs = null;
        try {
            Path resourceRoot;
            if (uri.getScheme().equals("jar")) {
                fs = FileSystems.newFileSystem(uri, java.util.Collections.emptyMap());
                resourceRoot = fs.getPath("/module/DiscordRpc");
            } else {
                resourceRoot = Paths.get(uri);
            }

            Files.walk(resourceRoot).forEach(path -> {
                try {
                    Path target = outputDir.resolve(resourceRoot.relativize(path).toString());
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            throw new IOException("Ошибка копирования ресурсов: " + e.getMessage(), e);
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
    }

    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}