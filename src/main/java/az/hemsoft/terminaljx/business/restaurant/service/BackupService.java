package az.hemsoft.terminaljx.business.restaurant.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackupService {
    private static BackupService instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Config
    private static final String BACKUP_DIR = "backups";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "qiyas2001";
    private static final String DB_NAME = "terminal_data";

    private BackupService() {
        File dir = new File(BACKUP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized BackupService getInstance() {
        if (instance == null) {
            instance = new BackupService();
        }
        return instance;
    }

    public void startAutoBackup(int intervalHours) {
        scheduler.scheduleAtFixedRate(this::performBackup, 0, intervalHours, TimeUnit.HOURS);
        System.out.println("â° Auto-Backup scheduled every " + intervalHours + " hours.");
    }

    public void stopAutoBackup() {
        scheduler.shutdown();
    }

    public void performBackup() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String filename = "backup_" + sdf.format(new Date()) + ".sql";
        File backupFile = new File(BACKUP_DIR, filename);

        System.out.println("â³ Starting backup: " + filename);

        // Mysqldump command
        // Note: mysqldump must be in system PATH or define absolute path
        ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "-u" + DB_USER,
                "-p" + DB_PASS,
                DB_NAME,
                "-r",
                backupFile.getAbsolutePath());

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("âœ… Backup successful: " + backupFile.getAbsolutePath());
            } else {
                System.err.println("âŒ Backup failed. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
