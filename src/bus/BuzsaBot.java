package bus;

import TelegramApi.TelegramApiUrls;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utils.Tokens;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BuzsaBot {

	private static boolean emergencyStop = false;
	private static Set<Long> updateIds = new HashSet<>();

	public static void main(String[] args) throws IOException {
		TelegramApiUrls api = new TelegramApiUrls();
		URL urlUpdate = api.getUpdateURL();

		while(!emergencyStop){ // While not stopped
			long tStart = System.currentTimeMillis();
			JSONObject updateResult = new JSONObject(Utils.call4json(urlUpdate));
			if (updateResult.has("ok") && updateResult.getBoolean("ok")) { // If we get an update
				JSONArray results = updateResult.getJSONArray("result");
				for (Object data : results) { // Iterate every update
					JSONObject result = new JSONObject(data.toString());
					if (processMessage(result)) { // And do stuff if we have to

						JSONObject message = result.getJSONObject("message");
                        long messageId = message.getLong("message_id");
                        String[] words = message.getString("text").split(" ");
                        JSONObject chat = message.optJSONObject("chat");
                        long chatId = chat.getLong("id");

						String sendResult = "";
						switch (words[0]) {
							case "/poste@BuzsaBot":
								if (words.length > 1) {
									if (words[1].equals("parar")){
										Logger.getLogger(BuzsaBot.class.getName()).log(Level.OFF, "Parando el bot...");
										emergencyStop = true;
									} else {
										String response = getBusData(words);
										sendResult = Utils.call4json(api.getSendMessageURL(chatId, response, messageId));
										Logger.getLogger(BuzsaBot.class.getName()).log(Level.INFO, sendResult);
									}
								}
								break;

							default:
								String sendText = "Error: el comando no existe :(";
								sendResult = Utils.call4json(api.getSendMessageURL(chatId, sendText));
								Logger.getLogger(BuzsaBot.class.getName()).log(Level.INFO, sendResult);
								break;
						}
						addProcessedUpdateId(result.getLong("update_id"));
					}
				}
			}
            Utils.waitAsecond(tStart);
		}
	}



    /**
	 * @param text String[] containing the invoked method at [0], and parameters at the rest of position
	 */
	private static String  getBusData(String[] text) {
		final String INFO_TABLE_CSS = "#contenido > table.info > tbody > tr:nth-child(2) > td > table.info > tbody > tr"; 
		String busStop = text[1]; 
		String posteUrl = Utils.getUrlAuzsa(busStop).toString();
		String response = ""; 
		
		try{
			Document htmlDocument = Jsoup.connect(posteUrl).get();
			ArrayList<BusStop> busTimeList = new ArrayList<>();
			Elements infoTable = htmlDocument.select(INFO_TABLE_CSS);
			
			// First row will be the table titles: no need to get them
			for (int i = 1; i < infoTable.size(); i++) {
				busTimeList.add(new BusStop(infoTable.get(i), busStop));
			}
			response = busTimeList.stream()
					.map(bus -> bus.toString())
					.collect(Collectors.joining("\n"));
		} catch (HttpStatusException http){
			http.getMessage(); 
		} catch (IOException e) {
			Logger.getLogger(BuzsaBot.class.getName()).log(Level.SEVERE, e.getMessage());
		} finally {
			// If boom, blank or null
			response = StringUtils.defaultIfBlank(response, "Petición fallida o parada sin estimaciones");  
		}
		
		return response; 
	}

	/**
	 * @param result
	 *            - A JSONObject containing an update result
	 * @return true if result is a message, has some text, and text has to be
	 *          processed by this bot
	 */
	private static boolean processMessage(JSONObject result) {
		if (result.has("message")) {
			String text = StringUtils.defaultString(result.getJSONObject("message").optString("text"));
			String[] splitMessage = text.split(" ");
			return StringUtils.endsWith(splitMessage[0], Tokens.BOT_NAME) 
					&& !processedId(result.getLong("update_id"));
		} else {
			return false;
		}
	}


	/**
	 * Checks if an update ID has been processed. All processed Ids are stored
	 * in a properties file.
	 *
	 * @param update_id
	 *            String containing an update_id
	 * @return true if id was found, false if not.
	 */
	private static boolean processedId(Long update_id) {
		File file = Utils.getProcessedIdsFile();
		Set<Long> processedIds = Utils.getUpdateIds(file);
		return processedIds.contains(update_id);
	}

	/**
	 * 
	 * @param update_id An update id
	 */
	private static void addProcessedUpdateId(Long update_id) {
		try {
			if (Files.exists(Utils.getProcessedIdsFile().toPath(), LinkOption.NOFOLLOW_LINKS)) {
				if (Files.isWritable(Utils.getProcessedIdsFile().toPath())) {
					FileUtils.write(Utils.getProcessedIdsFile(), update_id.toString() + "\n", true);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
