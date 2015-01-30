public class KalahMain
{
    public static void main(String args[])
    {
        KalahBot bot;
        Message m = Interfacing.getAndInterpret();

        if (m.getCurrentPlayer() == 0)
            bot = new KalahBot(0);
        else
            bot = new KalahBot(1);

        bot.play();
    }
}
