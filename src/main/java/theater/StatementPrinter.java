package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private Invoice invoice;
    private Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) throws UnknownPlayTypeException {
        this.setInvoice(invoice);
        this.setPlays(plays);
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * 
     * @return the formatted statement
     * @throws UnknownPlayTypeException if one of the play types is not known
     */
    public String statement() {

        final int volumeCredits = getTotalVolumeCredits();

        final int totalAmount = getTotalAmount();

        final StringBuilder result = new StringBuilder("Statement for "
                + getInvoice().getCustomer() + System.lineSeparator());
        for (Performance p : getInvoice().getPerformances()) {
            result.append(String.format("  %s: %s (%s seats)%n", getPlay(p).getName(),
                    usd(getAmount(p)),
                    p.getAudience()));
        }

        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private int getTotalAmount() {
        int totalAmount = 0;
        for (Performance p : getInvoice().getPerformances()) {
            totalAmount += getAmount(p);
        }
        return totalAmount;
    }

    private int getTotalVolumeCredits() {
        int total = 0;
        for (Performance p : getInvoice().getPerformances()) {
            total += getVolumeCredits(p);
        }
        return total;
    }

    private static String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount / Constants.PERCENT_FACTOR);
    }

    private int getVolumeCredits(Performance performance) {
        int result = 0;
        result += Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    private Play getPlay(Performance performance) {
        return getPlays().get(performance.getPlayID());
    }

    private int getAmount(Performance performance) {
        int result;
        final String playType = getPlay(performance).getType();
        switch (playType) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                    * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new UnknownPlayTypeException(String.format("unknown type: %s", playType));
        }
        return result;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    public void setPlays(Map<String, Play> plays) {
        this.plays = plays;
    }
}
