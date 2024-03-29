package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Whitelist {
    private final JavaPlugin plugin;
    private static final String wfilePath = "plugins/VanilifeProtector/wlisteduuids.dat";
    public boolean isEnable;
    public int minDays;
    public ArrayList<UUID> whitelists;
    private KickPlayer kp = null;

    public Whitelist(JavaPlugin plugin) {
        this.plugin = plugin;
        isEnable = false;
        minDays = -1;
        whitelists = new ArrayList<>();
    }

    public void setKp(KickPlayer kp) {
        this.kp = kp;
    }

    /**
     * wlisteduuids.datからUUIDリストを読み込む
     * @return 結果の成否
     */
    public boolean loadWlistData() {
        ArrayList<UUID> resList = loadListData(wfilePath);
        if (resList != null) {
            this.whitelists = resList;
            return true;
        }
        return false;
    }

    /**
     * wlisteduuids.datにUUIDリストを書き出す
     */

    public void saveWlistData() {
        saveListData(wfilePath, this.whitelists);
    }

    /**
     * olisteduuids.datからUUIDリストを読み込む
     * @return 結果の成否
     */
    public static ArrayList<UUID> loadListData(String filePath) {
        ArrayList<UUID> resList = new ArrayList<>();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filePath);
        }
        catch (FileNotFoundException e) {
            saveListData(filePath, resList);
            return resList;
        }

        GZIPInputStream gzis;
        try {
            gzis = new GZIPInputStream(fis);
        } catch (IOException e) {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }

        BukkitObjectInputStream bois;
        try {
            bois = new BukkitObjectInputStream(gzis);
        } catch (IOException e) {
            try {
                gzis.close();
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }

        try {
            resList = (ArrayList<UUID>) bois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            try {
                bois.close();
                gzis.close();
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }

        try {
            bois.close();
            gzis.close();
            fis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return resList;
    }

    /**
     * olisteduuids.datにUUIDリストを書き出す
     * @return 結果の成否
     */

    public static boolean saveListData(String filePath, ArrayList<UUID> saveList) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        GZIPOutputStream gzos;
        try {
            gzos = new GZIPOutputStream(fos);
        } catch (IOException e) {
            try {
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }

        BukkitObjectOutputStream boos;
        try {
            boos = new BukkitObjectOutputStream(gzos);
        } catch (IOException e) {
            try {
                gzos.close();
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }

        try {
            boos.writeObject(saveList);
        } catch (IOException e) {
            try {
                boos.close();
                gzos.close();
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }

        try {
            boos.close();
            gzos.close();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * ホワイトリストのON/OFFを切り替える → 互換性確保のため残し
     */
    public void toggleWlist() {
        this.isEnable = !(this.isEnable);
        this.plugin.getConfig().set("whitelist.isEnable", this.isEnable);
        this.plugin.saveConfig();
        if (this.isEnable) {
            this.plugin.getServer().broadcastMessage(ChatColor.RED + "ホワイトリストが有効になりました．");
            OpenCloseNotifier.close();
        }
        else {
            this.plugin.getServer().broadcastMessage(ChatColor.AQUA + "ホワイトリストが無効になりました．");
            OpenCloseNotifier.open();
            kp.resetKickedCount();
        }
        this.plugin.getLogger().info("ホワイトリストが"+this.isEnable+"になりました．");
    }

    public void onWlist() {
        if(this.isEnable) return;

        this.isEnable = true;
        this.plugin.getConfig().set("whitelist.isEnable", true);
        this.plugin.saveConfig();

        this.plugin.getServer().broadcastMessage(ChatColor.RED + "ホワイトリストが有効になりました．");
        OpenCloseNotifier.close();
        this.plugin.getLogger().info("ホワイトリストが有効になりました．");
    }

    public void offWlist() {
        if(!this.isEnable) return;

        this.isEnable = false;
        this.plugin.getConfig().set("whitelist.isEnable", false);
        this.plugin.saveConfig();

        this.plugin.getServer().broadcastMessage(ChatColor.AQUA + "ホワイトリストが無効になりました．");
        OpenCloseNotifier.open();
        this.plugin.getLogger().info("ホワイトリストが無効になりました．");
    }
}
