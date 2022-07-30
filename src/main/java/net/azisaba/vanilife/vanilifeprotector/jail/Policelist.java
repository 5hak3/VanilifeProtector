package net.azisaba.vanilife.vanilifeprotector.jail;

import net.azisaba.vanilife.vanilifeprotector.whitelisting.KickPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

import static net.azisaba.vanilife.vanilifeprotector.whitelisting.Whitelist.loadListData;
import static net.azisaba.vanilife.vanilifeprotector.whitelisting.Whitelist.saveListData;

public class Policelist {
    private final JavaPlugin plugin;
    private static final String wfilePath = "plugins/VanilifeProtector/plisteduuids.dat";
    public boolean isEnable;
    public int minDays;
    public ArrayList<UUID> policelist;
    private KickPlayer kp = null;

    public Policelist(JavaPlugin plugin) {
        this.plugin = plugin;
        isEnable = false;
        minDays = -1;
        policelist = new ArrayList<>();
    }

    public boolean loadPlistData() {
        ArrayList<UUID> resList = loadListData(wfilePath);
        if (resList != null) {
            this.policelist = resList;
            return true;
        }
        return false;
    }

    public void savePlistData() {
        saveListData(wfilePath, this.policelist);
    }
}
