package net.dec4234.database.framework;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.dec4234.src.BenedictMain;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;

public class MongoFramework {

	protected MongoCollection mongoCollection;

	public MongoFramework(String database, String collection) {
		mongoCollection = BenedictMain.getMongoClient().getDatabase(database).getCollection(collection);
	}

	/**
	 * Replace the oldDoc with the newDoc
	 */
	public void replaceDocument(Document oldDoc, Document newDoc) {
		mongoCollection.replaceOne(oldDoc, newDoc);
	}

	/**
	 * Get a doc that has the following key and value
	 */
	public Document getDoc(String key, String value) {
		return (Document) mongoCollection.find(new Document(key, value)).first();
	}

	/**
	 * Returns if the collection contains the document with the following id's
	 */
	public boolean hasDoc(String key1, String value) {
		return getDoc(key1, value) != null;
	}

	public List<Document> getAllDocuments() {
		List<Document> list = new LinkedList<>();

		MongoCursor mongoCursor = mongoCollection.find().cursor();

		while(mongoCursor.hasNext()) {
			list.add((Document) mongoCursor.next());
		}

		return list;
	}
}
