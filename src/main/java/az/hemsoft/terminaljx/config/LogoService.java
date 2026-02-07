package az.hemsoft.terminaljx.config;

import javafx.scene.image.Image;
import java.io.InputStream;

public class LogoService {
    private static LogoService instance;

    private LogoService() {
    }

    public static synchronized LogoService getInstance() {
        if (instance == null) {
            instance = new LogoService();
        }
        return instance;
    }

    public Image loadLogo(String name) {
        try {
            InputStream is = getClass().getResourceAsStream("/assets/" + name);
            if (is != null)
                return new Image(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void migrateLogoToDatabase(String name) {
        // Stub
    }
}
