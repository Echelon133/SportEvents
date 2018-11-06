package ml.echelon133.sportevents.event.types;

import ml.echelon133.sportevents.match.Match;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CardEvent extends AbstractMatchEvent {

    public enum CardColor {
        YELLOW, RED
    }

    private String cardedPlayer;

    @Enumerated(value = EnumType.STRING)
    private CardColor cardColor;

    public CardEvent() {}
    public CardEvent(Long time, String message, EventType type, Match eventMatch, String cardedPlayer, CardColor cardColor) {
        super(time, message, type, eventMatch);
        this.cardedPlayer = cardedPlayer;
        this.cardColor = cardColor;
    }

    public String getCardedPlayer() {
        return cardedPlayer;
    }

    public void setCardedPlayer(String cardedPlayer) {
        this.cardedPlayer = cardedPlayer;
    }

    public CardColor getCardColor() {
        return cardColor;
    }

    public void setCardColor(CardColor cardColor) {
        this.cardColor = cardColor;
    }
}
