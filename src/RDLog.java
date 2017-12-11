import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class RDLog {
	private static final String DIRECTORY = "log/";
	private static final char[] SESSION_ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final int SESSION_ID_LEN = 3;
	private static final String SESSION_LOG = "sessions.csv";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z (zzz)");

	private static Optional<RDLog> instance = Optional.empty();

	public static void init() {
		instance = Optional.of(new RDLog());
	}

	public static RDLog get() {
		return instance.get();
	}

	private String sessionID;
	private File sessionDir;
	private File rootDir;

	private RDLog() {
		rootDir = new File(DIRECTORY);
		sessionID = genSessionID();
		sessionDir = new File(rootDir, sessionID);
		assert (sessionDir.mkdirs());
		logSession();
	}

	private String genSessionID() {
		assert (rootDir.isDirectory());
		assert (rootDir.canRead());
		assert (rootDir.canWrite());
		String candidate;
		do {
			candidate = "";
			for (int i = 0; i < SESSION_ID_LEN; i++)
				candidate += SESSION_ID_CHARS[(int) (Math.random() * (double) SESSION_ID_CHARS.length)];

		} while (Arrays.asList(rootDir.list()).contains(candidate));
		return candidate;
	}

	private void logSession() {
		File sessionLog = new File(rootDir, SESSION_LOG);
		if (sessionLog.exists())
			assert (sessionLog.canWrite());
		else
			try {
				assert (sessionLog.createNewFile());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

		Date now = new Date();
		String date = DATE_FORMAT.format(now);
		appendLine(sessionLog, sessionID + ", " + "\"" + date + "\"");
	}

	private void appendLine(File outFile, String text) {
		assert (outFile.getParentFile().isDirectory());
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, true))) {
			writer.write(text + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
