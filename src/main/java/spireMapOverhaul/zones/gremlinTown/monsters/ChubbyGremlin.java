package spireMapOverhaul.zones.gremlinTown.monsters;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireMapOverhaul.SpireAnniversary6Mod;

import static spireMapOverhaul.util.Wiz.*;

public class ChubbyGremlin extends CustomMonster
{
    public static final String ID = SpireAnniversary6Mod.makeID(ChubbyGremlin.class.getSimpleName());
    public static final String NAME;
    private static final String[] MOVES;
    private static final String SKELETON_ATLAS = SpireAnniversary6Mod.makeImagePath(
            "monsters/GremlinTown/ChubbyGremlin/skeleton.atlas");
    private static final String SKELETON_JSON = SpireAnniversary6Mod.makeImagePath(
            "monsters/GremlinTown/ChubbyGremlin/skeleton.json");
    private boolean firstMove = true;
    private static final byte ATTACK = 1;
    private static final int DAMAGE = 5;
    private static final int DAMAGE_A2 = 6;
    private static final int MIN_HP = 32;
    private static final int MAX_HP = 35;
    private static final int MIN_HP_A7 = 35;
    private static final int MAX_HP_A7 = 38;
    private static final int STRENGTH_DOWN_AMOUNT = 2;

    private final int attackDamage;

    public ChubbyGremlin() {
        this(0.0f, 0.0f);
    }

    public ChubbyGremlin(final float x, final float y) {
        super(ChubbyGremlin.NAME, ID, MAX_HP, 0, 0, 110.0f/0.7f, 200.0f, null, x, y);

        type = EnemyType.NORMAL;
        loadAnimation(SKELETON_ATLAS, SKELETON_JSON, 0.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        if (asc() >= 7)
            setHp(MIN_HP_A7, MAX_HP_A7);
        else
            setHp(MIN_HP, MAX_HP);

        if (asc() >= 2)
            attackDamage = DAMAGE_A2;
        else
            attackDamage = DAMAGE;

        damage.add(new DamageInfo(this, attackDamage));
    }

    @Override
    public void takeTurn() {
        atb(new AnimateSlowAttackAction(this));
        atb(new DamageAction(adp(), damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        atb(new ApplyPowerAction(adp(), this, new StrengthPower(adp(), -STRENGTH_DOWN_AMOUNT), -STRENGTH_DOWN_AMOUNT,
                true, AbstractGameAction.AttackEffect.NONE));

        if (!adp().hasPower(ArtifactPower.POWER_ID)) {
            atb(new ApplyPowerAction(adp(), this, new GainStrengthPower(adp(), STRENGTH_DOWN_AMOUNT), STRENGTH_DOWN_AMOUNT,
                    true, AbstractGameAction.AttackEffect.NONE));
        }

        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMove(MOVES[0], ATTACK, Intent.ATTACK_DEBUFF, attackDamage);
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
    }
}