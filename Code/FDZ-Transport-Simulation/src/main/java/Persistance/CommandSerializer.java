package Persistance;

import Model.Command.Command;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CommandSerializer implements JsonSerializer<Command>, JsonDeserializer<Command> {
	@Override
	public Command deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("commandType").getAsString();
		JsonElement  element = jsonObject.get("properties");
		try {
			return context.deserialize(element, Class.forName("Model.Command."+type));
		}catch (ClassNotFoundException e){
			throw new JsonParseException("Unknown element type: "+ type, e);
		}
	}

	@Override
	public JsonElement serialize(Command src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("commandType", new JsonPrimitive(src.getClass().getSimpleName()));
		//result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}
}
