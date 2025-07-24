import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class MythicalDownloadSorter {

    // Mapping file extensions to categories
    private static final Map<String, String> fileTypeMap = new HashMap<>();
    private static final List<String> knownCategories = new ArrayList<>();
    private static final String mythicalRootFolder = "ValhallaDownloads";
    private static final String unopenedFolderName = "UnopenedFolders";

    // Set user paths
    private static final String USER_HOME = System.getProperty("user.home");
    private static final File DOWNLOADS_DIR = new File(USER_HOME, "Downloads");
    private static final File DOCUMENTS_DIR = new File(USER_HOME, "Documents");

    // System tray variables
    private static SystemTray tray;
    private static TrayIcon trayIcon;

    public static void main(String[] args) {
        initFileTypes(); // Setup file types and categories
        setupSystemTray(); // Setup system tray icon
        processDownloads(); // Initial sorting
        // Note: Task Scheduler handles repeated execution
    }

    // Initialize file types and corresponding category folders
    private static void initFileTypes() {
        fileTypeMap.put(".pdf", "PDF");
        fileTypeMap.put(".jpg", "Images");
        fileTypeMap.put(".jpeg", "Images");
        fileTypeMap.put(".png", "Images");
        fileTypeMap.put(".gif", "Images");
        fileTypeMap.put(".bmp", "Images");
        fileTypeMap.put(".mp3", "Music");
        fileTypeMap.put(".wav", "Music");
        fileTypeMap.put(".flac", "Music");
        fileTypeMap.put(".mp4", "Videos");
        fileTypeMap.put(".mkv", "Videos");
        fileTypeMap.put(".avi", "Videos");
        fileTypeMap.put(".mov", "Videos");
        fileTypeMap.put(".docx", "Docs");
        fileTypeMap.put(".doc", "Docs");
        fileTypeMap.put(".txt", "Docs");
        fileTypeMap.put(".xlsx", "Docs");
        fileTypeMap.put(".pptx", "Docs");
        fileTypeMap.put(".zip", "Archives");
        fileTypeMap.put(".rar", "Archives");
        fileTypeMap.put(".7z", "Archives");
        fileTypeMap.put(".tar", "Archives");
        fileTypeMap.put(".gz", "Archives");
        fileTypeMap.put(".py", "Scripts");
        fileTypeMap.put(".js", "Scripts");
        fileTypeMap.put(".java", "Scripts");
        fileTypeMap.put(".c", "Scripts");
        fileTypeMap.put(".cpp", "Scripts");
        fileTypeMap.put(".exe", "Installers");
        fileTypeMap.put(".msi", "Installers");
        fileTypeMap.put(".apk", "Installers");

        knownCategories.addAll(new HashSet<>(fileTypeMap.values()));
        knownCategories.add("Others");
        knownCategories.add(unopenedFolderName);
    }

    // Setup system tray icon with menu
    private static void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            return;
        }

        tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // Placeholder icon
        PopupMenu popup = new PopupMenu();

        MenuItem sortItem = new MenuItem("Sort Now");
        sortItem.addActionListener(e -> processDownloads());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

        popup.add(sortItem);
        popup.add(exitItem);

        trayIcon = new TrayIcon(image, "MythicalDownloadSorter", popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("Error adding tray icon: " + e.getMessage());
        }
    }

    // Main logic to scan and move files/folders
    private static void processDownloads() {
        if (!DOWNLOADS_DIR.exists()) {
            System.out.println("Downloads directory does not exist!");
            return;
        }

        File[] allItems = DOWNLOADS_DIR.listFiles();
        if (allItems == null) {
            System.out.println("No files or folders found in Downloads!");
            return;
        }

        for (File item : allItems) {
            if (item.isDirectory()) {
                moveToUnopened(item); // Move folders to UnopenedFolders
            } else if (item.isFile()) {
                moveAndSortFile(item); // Move and sort individual files
            }
        }
    }

    // Move folders into the unopened folder inside the root
    private static void moveToUnopened(File folder) {
        File target = new File(DOCUMENTS_DIR, mythicalRootFolder + "/" + unopenedFolderName + "/" + folder.getName());
        if (!target.exists()) {
            createDirectories(target.getParentFile());
            try {
                Files.move(folder.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Error moving folder: " + folder.getName());
            }
        }
    }

    // Move and organize a file based on its type and date
    private static void moveAndSortFile(File file) {
        String ext = getFileExtension(file.getName()).toLowerCase();
        String category = fileTypeMap.getOrDefault(ext, "Others");

        String dateFolder = getFileModifiedDate(file);
        File targetDir = new File(DOCUMENTS_DIR, mythicalRootFolder + "/" + category + "/" + dateFolder);

        createDirectories(targetDir);

        File targetFile = new File(targetDir, file.getName());
        try {
            Files.move(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error moving file: " + file.getName());
        }
    }

    // Return extension of the file
    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return (lastDot > 0) ? filename.substring(lastDot) : "";
    }

    // Get file's last modified date as YYYY-MM-DD
    private static String getFileModifiedDate(File file) {
        long modified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(modified));
    }

    // Create folder if it does not exist
    private static void createDirectories(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
