package net.azisaba.vanilife.vanilifeprotector.whitelisting;

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
    public boolean isEnable;
    public int minDays;
    public ArrayList<UUID> uuids;
    public ArrayList<UUID> observers;

    public Whitelist(JavaPlugin plugin) {
        this.plugin = plugin;
        isEnable = false;
        minDays = -1;
        uuids = new ArrayList<>();
        observers = new ArrayList<>();
    }

    /**
     * wlisteduuids.datからUUIDリストを読み込む
     * @return 結果の成否
     */
    public boolean loadWlistData() {
        FileInputStream fis;
        try {
            fis = new FileInputStream("wlisteduuids.dat");
        }
        catch (FileNotFoundException e) {
            return this.saveWlistData();
        }

        GZIPInputStream gzis;
        try {
            gzis = new GZIPInputStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        BukkitObjectInputStream bois;
        try {
            bois = new BukkitObjectInputStream(gzis);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            this.uuids = (ArrayList<UUID>) bois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * wlisteduuids.datにUUIDリストを書き出す
     * @return 結果の成否
     */

    public boolean saveWlistData() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("wlisteduuids.dat");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        GZIPOutputStream gzos;
        try {
            gzos = new GZIPOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        BukkitObjectOutputStream boos;
        try {
            boos = new BukkitObjectOutputStream(gzos);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            boos.writeObject(this.uuids);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * ホワイトリストのON/OFFを切り替える
     */
    public void toggleWlist() {
        this.isEnable = !(this.isEnable);
        this.plugin.getConfig().set("whitelist.isEnable", this.isEnable);
    }
}
