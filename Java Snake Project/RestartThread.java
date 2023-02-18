public class RestartThread extends Thread {
	Game game;
     public RestartThread(Game g) {
         super();
         this.game = g;
     }

     public void run() {
         game.play();
     }
}