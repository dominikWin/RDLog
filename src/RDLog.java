import java.util.Optional;

public class RDLog {
	private static Optional<RDLog> instance = Optional.empty();
	
	private RDLog() {		
	}
	
	public static void init() {
		instance = Optional.of(new RDLog());
	}
	
	public static RDLog get() {
		return instance.get();
	}
}
