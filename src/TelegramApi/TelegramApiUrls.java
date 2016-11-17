package TelegramApi;

import org.apache.commons.lang3.StringUtils;
import utils.Tokens;
import utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class TelegramApiUrls {

	private static final String URL = "https://api.telegram.org/bot<token>";
	private static final String GET_UPDATES_METHOD = "/getUpdates";
	private static final String SEND_MESSAGE_METHOD = "/sendMessage";

	/**
	 * @return a ready URL for calling Telegram's API getUpdate method. 
	 */
	public URL getUpdateURL() {
		return Utils.getUrlInstance(getMainURL()
				.append(GET_UPDATES_METHOD)
				.toString());
	}

	/**
	 * @return StringBuffer containing the main URL with the bot's token. 
	 */
	private StringBuffer getMainURL() {
		return new StringBuffer(StringUtils.replace(URL, "<token>", Tokens.BOT_TOKEN));
	}

	public URL getSendMessageURL(long chatId, String text){
		StringBuffer sb = getMainURL();

		sb.append(SEND_MESSAGE_METHOD).append("?chat_id=").append(chatId).append("&text=").append(text); 
		
		return Utils.getUrlInstance(sb.toString());
	}
	
	public URL getSendMessageURL(long chatId, String text, Long messageId){
		StringBuffer sb = getMainURL();

		try {
			sb.append(SEND_MESSAGE_METHOD)
				.append("?chat_id=")
				.append(chatId)
				.append("&text=")
				.append(URLEncoder.encode(text, "UTF-8"))
				.append("&reply_to_message_id=")
				.append(messageId);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		return Utils.getUrlInstance(sb.toString());
	}

}
