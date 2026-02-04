package az.hemsoft.terminaljx.business.restaurant.service;

import javafx.scene.image.Image;
import java.io.*;

/**
 * Logo yÃ¼klÉ™mÉ™ vÉ™ idarÉ™etmÉ™ service-i
 * LogolarÄ± SQLite-dÉ™n yÃ¼klÉ™yir, yoxdursa assets-dÉ™n yÃ¼klÉ™yib DB-yÉ™ saxlayÄ±r
 */
public class LogoService {
    private static LogoService instance;
    private DatabaseManager dbManager;

    private LogoService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized LogoService getInstance() {
        if (instance == null) {
            instance = new LogoService();
        }
        return instance;
    }

    /**
     * Logo-nu yÃ¼klÉ™ - É™vvÉ™lcÉ™ DB-dÉ™n, yoxdursa assets-dÉ™n yÃ¼klÉ™yib DB-yÉ™ saxla
     */
    /**
     * Logo-nu yÃ¼klÉ™ - É™vvÉ™lcÉ™ DB-dÉ™n, yoxdursa assets-dÉ™n yÃ¼klÉ™yib DB-yÉ™ saxla
     */
    public Image loadLogo(String logoName) {
        // 1. Check local file system (Priority: High reliability)
        try {
            String userDir = System.getProperty("user.dir");
            File file = new File(userDir, "assets/" + logoName);
            if (file.exists()) {
                System.out.println("âœ… Logo fayldan birbaÅŸa yÃ¼klÉ™nir: " + file.getAbsolutePath());
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            System.err.println("âŒ Fayldan logo yÃ¼klÉ™nmÉ™si xÉ™tasÄ±: " + e.getMessage());
        }

        // 2. Check Database
        try {
            byte[] logoData = dbManager.getLogo(logoName);
            if (logoData != null && logoData.length > 0) {
                return createImageFromBytes(logoData);
            }
        } catch (Exception e) {
            System.err.println("âŒ DB-dÉ™n logo yÃ¼klÉ™nmÉ™si xÉ™tasÄ±: " + e.getMessage());
        }

        // 3. Fallback to resource stream (assets inside JAR)
        try {
            System.out.println("âš ï¸ Logo fail/db-dÉ™ tapÄ±lmadÄ±, assets-dÉ™n yÃ¼klÉ™nir: " + logoName);
            // Use stream to get bytes, then save to DB (legacy support)
            byte[] logoBytes = loadLogoBytesFromAssets(logoName);
            if (logoBytes != null && logoBytes.length > 0) {
                dbManager.saveLogo(logoName, logoBytes);
                return createImageFromBytes(logoBytes);
            }
        } catch (Exception e) {
            System.err.println("âŒ Resource logo yÃ¼klÉ™nmÉ™si xÉ™tasÄ±: " + e.getMessage());
        }

        System.err.println("âŒ Logo heÃ§ yerdÉ™ tapÄ±lmadÄ±: " + logoName);
        return null;
    }

    /**
     * Logo-nu assets-dÉ™n birbaÅŸa file-dan byte array kimi oxu
     */
    private byte[] loadLogoBytesFromAssets(String logoName) {
        try {
            // ÆvvÉ™lcÉ™ resources-dan yoxla
            InputStream is = getClass().getResourceAsStream("/assets/" + logoName);
            if (is != null) {
                System.out.println("âœ… Logo resources-dan oxunur: " + logoName);
                return readInputStreamToBytes(is);
            }

            // LayihÉ™ root-dan yoxla
            String basePath = System.getProperty("user.dir");
            File logoFile = new File(basePath + "/assets/" + logoName);
            if (logoFile.exists()) {
                System.out.println("âœ… Logo layihÉ™ root-dan oxunur: " + logoFile.getAbsolutePath());
                return readFileToBytes(logoFile);
            }

            System.err.println("âŒ Logo tapÄ±lmadÄ±: " + logoName);
            return null;

        } catch (Exception e) {
            System.err.println("âŒ Logo assets-dÉ™n oxunarkÉ™n xÉ™ta: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * File-dan byte array oxu
     */
    private byte[] readFileToBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }

    /**
     * InputStream-dÉ™n byte array oxu
     */
    private byte[] readInputStreamToBytes(InputStream is) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Byte array-dÉ™n Image yarat
     * Temp file YOX, birbaÅŸa Memory-dÉ™n oxuyur
     */
    private Image createImageFromBytes(byte[] imageBytes) {
        try {
            if (imageBytes == null || imageBytes.length == 0)
                return null;

            // BirbaÅŸa yaddaÅŸdan oxu
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            Image image = new Image(bis);

            return image;
        } catch (Exception e) {
            System.err.println("âŒ Byte array-dÉ™n Image yaradÄ±larkÉ™n xÉ™ta: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Logo-nu assets-dÉ™n yÃ¼klÉ™yib DB-yÉ™ kÃ¶Ã§Ã¼r (background thread-dÉ™)
     */
    public void migrateLogoToDatabase(String logoName) {
        new Thread(() -> {
            try {
                if (!dbManager.logoExists(logoName)) {
                    byte[] logoBytes = loadLogoBytesFromAssets(logoName);
                    if (logoBytes != null && logoBytes.length > 0) {
                        dbManager.saveLogo(logoName, logoBytes);
                        System.out.println("âœ… Logo DB-yÉ™ kÃ¶Ã§Ã¼rÃ¼ldÃ¼: " + logoName);
                    }
                }
            } catch (Exception e) {
                System.err.println("Logo kÃ¶Ã§Ã¼rÃ¼lÉ™rkÉ™n xÉ™ta: " + e.getMessage());
            }
        }).start();
    }
}

