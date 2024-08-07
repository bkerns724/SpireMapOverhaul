package spireMapOverhaul.zones.brokenspace.relics;

import com.badlogic.gdx.math.MathUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokenOmamori extends BrokenRelic {
    public static final String ID = "BrokenOmamori";

    public static final int AMOUNT = 2;
    public static final int UPGRADE_AMOUNT = 2;

    private boolean triggered = false;

    public BrokenOmamori() {
        super(ID, RelicTier.SPECIAL, LandingSound.CLINK, Omamori.ID);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        for (int i = 0; i < AMOUNT; i++) {
            AbstractCard c = AbstractDungeon.returnRandomCurse();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        }
        flash();
    }

    @Override
    public void onRest() {
        triggered = true;

    }

    @Override
    public void update() {
        super.update();
        if (triggered) {
            triggered = false;
            flash();
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));// 99
            ArrayList<AbstractCard> upgradableCards = new ArrayList<>();

            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.canUpgrade()) {// 102
                    upgradableCards.add(c);// 103
                }
            }

            for (int i = 0; i < UPGRADE_AMOUNT; i++) {
                if (!upgradableCards.isEmpty()) {
                    AbstractCard c = upgradableCards.remove(AbstractDungeon.cardRandomRng.random(upgradableCards.size() - 1));
                    c.upgrade();
                    AbstractDungeon.player.bottledCardUpgradeCheck(c);
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1F, 0.9F) * (float) Settings.WIDTH, MathUtils.random(0.2F, 0.8F) * (float) Settings.HEIGHT));// 105 107 108 109
                    incrementUpgradesStat(1);
                }
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + AMOUNT + DESCRIPTIONS[1] + UPGRADE_AMOUNT + DESCRIPTIONS[2];
    }

    private static final Map<String, Integer> stats = new HashMap<>();
    private static final String UPGRADES_STAT = "upgrades";

    public String getStatsDescription() {
        return DESCRIPTIONS[3].replace("{0}", stats.get(UPGRADES_STAT) + "");
    }

    public String getExtendedStatsDescription(int totalCombats, int totalTurns) {
        return getStatsDescription();
    }

    public void resetStats() {
        stats.put(UPGRADES_STAT, 0);
    }

    public JsonElement onSaveStats() {
        Gson gson = new Gson();
        List<Integer> statsToSave = new ArrayList<>();
        statsToSave.add(stats.get(UPGRADES_STAT));
        return gson.toJsonTree(statsToSave);
    }

    public void onLoadStats(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            stats.put(UPGRADES_STAT, jsonArray.get(0).getAsInt());
        } else {
            resetStats();
        }
    }

    public static void incrementUpgradesStat(int amount) {
        stats.put(UPGRADES_STAT, stats.getOrDefault(UPGRADES_STAT, 0) + amount);
    }
}
