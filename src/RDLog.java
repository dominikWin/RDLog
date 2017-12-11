import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class RDLog {
	private static String DIRECTORY = "log/";
	private static final char[] SESSION_ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final int SESSION_ID_LEN = 3;
	private static final String SESSION_LOG = "sessions.csv";
	private static final String VALUE_LOG = "values.csv";
	private static final String COLUMN_LOG = "collumns.csv";
	private static final String DATA_LOG = "data.csv";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z (zzz)");

	private static Optional<RDLog> instance = Optional.empty();

	public static void init() {
		instance = Optional.of(new RDLog());
	}

	public static RDLog get() {
		return instance.get();
	}

	public static void setDirectory(String dir) {
		DIRECTORY = dir;
	}

	private String sessionID;
	private File sessionDir;
	private File rootDir;
	private File valueFile;
	private File columnFile;
	private File dataFile;
	private BufferedWriter out;
	
	private boolean openTopicRegistration;

	private List<RDObject> namespace;
	private List<RDTopic> topics;

	private RDLog() {
		rootDir = new File(DIRECTORY);
		sessionID = genSessionID();
		sessionDir = new File(rootDir, sessionID);

		assert (sessionDir.mkdirs());
		logSession();

		valueFile = new File(sessionDir, VALUE_LOG);
		try {
			assert (valueFile.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		columnFile = new File(sessionDir, COLUMN_LOG);
		try {
			assert (columnFile.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dataFile = new File(sessionDir, DATA_LOG);
		try {
			assert (dataFile.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out = new BufferedWriter(new FileWriter(dataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		openTopicRegistration = true;
		namespace = new ArrayList<>();
		topics = new ArrayList<>();
	}
	
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getSessionID() {
		return sessionID;
	}

	public void registerValue(String key, String value) {
		assert (!isInNamespace(key));
		RDValue valueObj = new RDValue(key, value);
		namespace.add(valueObj);
		appendLine(valueFile, "\"" + key + "\", \"" + value + "\"");
	}

	public void registerQueriedTopic(String key, Supplier<Double> query, RDTopicInfo topicInfo) {
		assert (openTopicRegistration);
		assert (!isInNamespace(key));
		RDQueriedTopic topicObj = new RDQueriedTopic(key, query, topicInfo);
		namespace.add(topicObj);
		appendLine(columnFile, "\"" + key + "\", \"" + topicInfo.getUnit() + "\", \"" + topicInfo.getDesc() + "\"");
	}

	public void endTopicRegistration() {
		if (!openTopicRegistration)
			return;
		openTopicRegistration = false;
		namespace.stream().filter((o) -> (o instanceof RDTopic)).forEach((t) -> topics.add((RDTopic) t));
		String columns[] = Arrays.copyOf(topics.stream().map(RDTopic::getKey).toArray(), topics.size(), String[].class);
		String header = "\"" + String.join("\", \"", columns) + "\"";
		try {
			out.write(header + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logTopics() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < topics.size(); i++) {
			builder.append("" + topics.get(i).getValue() + ((i == topics.size() - 1) ? "\n" : ", "));
		}
		String line = builder.toString();
		try {
			out.write(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isInNamespace(String key) {
		return namespace.stream().map(RDObject::getKey).anyMatch((k) -> key.equals(k));
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
