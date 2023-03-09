package net.dec4234.commands.stats.clan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.quickchart.QuickChart;
import net.dec4234.database.collections.UserManagement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClanStats {

	private UserManagement userManagement = new UserManagement();

	public String getPastDaysChart(int days) {
		long time = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * days); // Subtract 30 days from current time
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

		QuickChart chart = new QuickChart();

		JsonObject object = new JsonObject();

		object.addProperty("type", "line");

		JsonObject data = new JsonObject();
		JsonArray labels = new JsonArray();

		for(int i = 0; i < 1 + days; i++) {
			labels.add(sdf.format(new Date(time + (i * 1000L * 60 * 60 * 24))));
		}

		data.add("labels", labels);

		JsonObject idata = new JsonObject();
		idata.addProperty("label", "Member Count");

		JsonArray dataArray = new JsonArray();

		int min = 0;

		for(int i = 0; i < 1 + days; i++) {
			int amount = userManagement.getMembersAtTime(time + (i * 1000L * 60 * 60 * 24)).size();
			dataArray.add(Math.min(amount, 100));

			if(min == 0) {
				min = amount;
			}

			min = Math.min(min, amount);
		}

		idata.add("data", dataArray);

		JsonArray datasets = new JsonArray();
		datasets.add(idata);

		data.add("datasets", datasets);

		object.add("data", data);

		JsonObject options = new JsonObject();
		JsonArray yAxes = new JsonArray();
		JsonObject ticks = new JsonObject();

		ticks.addProperty("min", min);
		ticks.addProperty("max", 100);
		ticks.addProperty("stepSize", 1);

		JsonObject ticks2 = new JsonObject();
		ticks2.add("ticks", ticks);

		yAxes.add(ticks2);

		options.add("yAxes", yAxes);

		object.add("options", options);

		chart.setConfig(object.toString());

		return chart.getUrl();
	}
}
