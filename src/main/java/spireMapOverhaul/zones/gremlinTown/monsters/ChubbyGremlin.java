package spireMapOverhaul.zones.gremlinTown.monsters;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.MalleablePower;
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
    private static final byte ATTACK = 1;
    private static final byte REST = 2;
    private static final int DAMAGE = 9;
    private static final int DAMAGE_A2 = 10;
    private static final int MIN_HP = 57;
    private static final int MAX_HP = 63;
    private static final int MIN_HP_A7 = 61;
    private static final int MAX_HP_A7 = 67;
    private static final int MALLEABLE_AMOUNT = 3;
    private static final int MALLEABLE_AMOUNT_A18 = 4;

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

    public void usePreBattleAction() {
        if (asc() >= 18)
            applyToEnemy(this, new MalleablePower(this, MALLEABLE_AMOUNT_A18));
        else
            applyToEnemy(this, new MalleablePower(this, MALLEABLE_AMOUNT));
    }

    @Override
    public void takeTurn() {
        if (nextMove == ATTACK) {
            atb(new AnimateSlowAttackAction(this));
            atb(new DamageAction(adp(), damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
        else {
            atb(new TextAboveCreatureAction(this, DIALOG[0]));
        }

        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!lastMove(ATTACK))
            setMove(MOVES[0], ATTACK, Intent.ATTACK_DEBUFF, attackDamage);
        else
            setMove(MOVES[1], REST, Intent.UNKNOWN);
    }

    static {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
    }
}