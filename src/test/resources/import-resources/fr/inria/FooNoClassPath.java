public class FooNoClassPath {

	private Game game = new Game();


	public void m() {
		Coords coords = new Coords(game.board.width - 2, game.board.height - 2);
	}
}
