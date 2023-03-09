package net.dec4234.database.framework;

import org.bson.Document;

public class DatabaseConfig extends MongoFramework {

	public DatabaseConfig() {
		super("Benedict", "config");
	}

	public void insertDefaultDoc() {
		if (!hasData()) {
			Document doc = new Document("uuid", "config");
			doc.append(MongoKey.ONLINE.getKey(), true)
			   .append(MongoKey.API_TOKEN.getKey(), "")
			   .append(MongoKey.ACCESS_TOKEN.getKey(), "")
			   .append(MongoKey.REFRESH_TOKEN.getKey(), "")
			   .append(MongoKey.SCAN_MEMBERS.getKey(), true)
			   .append(MongoKey.UPDATE_DISCORD_USERNAMES.getKey(), true);
			mongoCollection.insertOne(doc);
		}
	}

	public boolean isOnline() {
		return getBoolean(MongoKey.ONLINE.getKey());
	}

	public boolean getBoolean(String key) {
		insertDefaultDoc();
		return getDoc().getBoolean(key);
	}

	public String getString(String key) {
		insertDefaultDoc();
		return getDoc().getString(key);
	}

	public void setString(String key, String value) {
		Document doc = getDoc();
		doc.append(key, value);

		replaceDocument(getDoc(), doc);
	}

	public Long getLong(MongoKey mongoKey) {
		if(getDoc().containsKey(mongoKey.getKey())) {
			getDoc().getLong(mongoKey.getKey());
		}

		return 0L;
	}

	public void setLong(MongoKey mongoKey, long longg) {
		replaceDocument(getDoc(), getDoc().append(mongoKey.getKey(), longg));
	}

	private Document getDoc() {
		insertDefaultDoc();
		return (Document) mongoCollection.find(new Document("uuid", "config")).first();
	}

	public boolean hasData() {
		Document doc = new Document("uuid", "config");
		return mongoCollection.find(doc).first() != null;
	}
}
