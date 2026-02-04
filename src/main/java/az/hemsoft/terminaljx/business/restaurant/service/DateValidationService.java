package az.hemsoft.terminaljx.business.restaurant.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Sistem tarixinin geri \u00e7\u0259kilm\u0259sini a\u015fkar ed\u0259n service
 * Properties fayl\u0131n\u0131n manipulyasiyas\u0131ndan qorunur
 */
public class DateValidationService {

    private static final String LAST_CHECK_FILE = "last_date_check.dat"; // .properties \u0259v\u0259zin\u0259 .dat
    private static final String LAST_CHECK_KEY = "lastCheckTime";
    private static final String CHECKSUM_KEY = "checksum";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SECRET_SALT = "HEMSOFT_DATE_VALIDATION_2024"; // Build zaman\u0131
                                                                              // d\u0259yi\u015fdirilm\u0259lidir

    /**
     * Sistem tarixinin geri \u00e7\u0259kilm\u0259sini yoxlay\u0131r
     * 
     * @return true \u018fg\u0259r tarix geri \u00e7\u0259kilmi\u015fdirs\u0259,
     *         false \u0259ks halda
     */
    public static boolean checkDateManipulation() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime lastCheckTime = loadLastCheckTime();

            // \u0130lk d\u0259f\u0259 yoxlan\u0131rsa, cari zaman\u0131 saxla
            if (lastCheckTime == null) {
                saveLastCheckTime(currentTime);
                return false;
            }

            // \u018fg\u0259r cari tarix son yoxlama tarixind\u0259n ki\u00e7ikdirs\u0259,
            // tarix geri \u00e7\u0259kilmi\u015fdir
            if (currentTime.isBefore(lastCheckTime)) {
                System.err
                        .println("\u26A0\uFE0F X\u018FB\u018FRDARLIQ: Sistem tarixi geri \u00e7\u0259kilmi\u015fdir!");
                System.err.println("   Son yoxlama: " + lastCheckTime.format(FORMATTER));
                System.err.println("   Cari tarix: " + currentTime.format(FORMATTER));
                return true;
            }

            // Normal halda, cari zaman\u0131 yenil\u0259
            saveLastCheckTime(currentTime);
            return false;

        } catch (Exception e) {
            System.err.println("Tarix yoxlamas\u0131 zaman\u0131 x\u0259ta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Son yoxlama zaman\u0131n\u0131 fayldan y\u00fckl\u0259yir
     * Checksum il\u0259 manipulyasiyan\u0131 a\u015fkar et
     */
    private static LocalDateTime loadLastCheckTime() {
        try {
            String userDir = System.getProperty("user.dir");
            Path filePath = Paths.get(userDir, LAST_CHECK_FILE);

            if (!Files.exists(filePath)) {
                return null;
            }

            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                props.load(fis);
            }

            String lastCheckStr = props.getProperty(LAST_CHECK_KEY);
            if (lastCheckStr == null || lastCheckStr.isEmpty()) {
                return null;
            }

            // Checksum yoxla - manipulyasiya a\u015fkar et
            String storedChecksum = props.getProperty(CHECKSUM_KEY);
            String calculatedChecksum = calculateChecksum(lastCheckStr);

            if (storedChecksum == null || !storedChecksum.equals(calculatedChecksum)) {
                System.err
                        .println("\u26A0\uFE0F X\u018FB\u018FRDARLIQ: Tarix fayl\u0131 manipulyasiya edilmi\u015fdir!");
                // Manipulyasiya a\u015fkar edildi, fayl\u0131 sil v\u0259 yenid\u0259n
                // ba\u015fla
                Files.deleteIfExists(filePath);
                return null;
            }

            return LocalDateTime.parse(lastCheckStr, FORMATTER);

        } catch (Exception e) {
            System.err.println("Son yoxlama zaman\u0131 y\u00fckl\u0259n\u0259rk\u0259n x\u0259ta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checksum hesablay\u0131r - manipulyasiyan\u0131 a\u015fkar etm\u0259k
     * \u00fcc\u00fcn
     */
    private static String calculateChecksum(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = data + SECRET_SALT;
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Son yoxlama zaman\u0131n\u0131 fayla yaz\u0131r
     * Checksum il\u0259 manipulyasiyadan qorunur
     */
    private static void saveLastCheckTime(LocalDateTime time) {
        try {
            String userDir = System.getProperty("user.dir");
            Path filePath = Paths.get(userDir, LAST_CHECK_FILE);

            // \u018fg\u0259r fayl varsa v\u0259 readonly-dirs\u0259, \u0259vv\u0259lc\u0259
            // readonly atributunu sil
            if (Files.exists(filePath)) {
                try {
                    // Windows-da readonly atributunu sil
                    java.io.File file = filePath.toFile();
                    if (file.exists() && !file.canWrite()) {
                        file.setWritable(true);
                    }
                    // NIO il\u0259 d\u0259 yoxla
                    try {
                        Boolean isReadOnly = (Boolean) Files.getAttribute(filePath, "dos:readonly");
                        if (isReadOnly != null && isReadOnly) {
                            Files.setAttribute(filePath, "dos:readonly", false);
                        }
                    } catch (Exception e) {
                        // NIO atributu i\u015fl\u0259mirs\u0259, ignore et
                    }
                } catch (Exception e) {
                    // Readonly atributunu sil\u0259rk\u0259n x\u0259ta olsa, davam et
                    System.err.println("Readonly atributunu sil\u0259rk\u0259n x\u0259ta: " + e.getMessage());
                }
            }

            String timeStr = time.format(FORMATTER);
            String checksum = calculateChecksum(timeStr);

            Properties props = new Properties();
            if (Files.exists(filePath)) {
                try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                    props.load(fis);
                }
            }

            props.setProperty(LAST_CHECK_KEY, timeStr);
            props.setProperty(CHECKSUM_KEY, checksum);

            // Binary format-da yaz - daha t\u0259hl\u00fck\u0259siz
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                props.store(fos, "Son tarix yoxlamas\u0131 - DO NOT MODIFY");
            }

        } catch (Exception e) {
            System.err.println("Son yoxlama zaman\u0131 saxlanark\u0259n x\u0259ta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * M\u00fcnt\u0259z\u0259m yoxlama \u00fcc\u00fcn background thread
     * ba\u015flad\u0131r
     * H\u0259r 30 saniy\u0259d\u0259 bir yoxlay\u0131r
     * 
     * @param onManipulationDetected Callback funksiyas\u0131 - tarix
     *                               manipulyasiyas\u0131 a\u015fkar edildikd\u0259
     *                               \u00e7a\u011f\u0131r\u0131l\u0131r
     */
    public static void startPeriodicCheck(Runnable onManipulationDetected) {
        Thread checkThread = new Thread(() -> {
            while (true) {
                try {
                    if (checkDateManipulation()) {
                        // Tarix manipulyasiyas\u0131 a\u015fkar edildi, callback \u00e7a\u011f\u0131r
                        if (onManipulationDetected != null) {
                            onManipulationDetected.run();
                        }
                    }
                    Thread.sleep(30000); // 30 saniy\u0259
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("M\u00fcnt\u0259z\u0259m yoxlama zaman\u0131 x\u0259ta: " + e.getMessage());
                }
            }
        });
        checkThread.setDaemon(true);
        checkThread.start();
    }
}
