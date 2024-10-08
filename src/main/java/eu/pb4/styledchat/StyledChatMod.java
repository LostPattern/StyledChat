package eu.pb4.styledchat;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.other.GenericModInfo;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("styledchat")
public class StyledChatMod {
	public static final Logger LOGGER = LogManager.getLogger("Styled Chat");
	public static MinecraftServer server = null;

	public static boolean USE_FABRIC_API = true;

	public static RegistryKey<MessageType> MESSAGE_TYPE_ID = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, Identifier.of("styled_chat", "generic_hack"));

	public static MessageType getMessageType() {
		return server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getOrThrow(MESSAGE_TYPE_ID);
	}

	public StyledChatMod(IEventBus modBus, ModContainer container) {
		crabboardDetection();
		GenericModInfo.build(container);
		PlayerDataApi.register(StyledChatUtils.PLAYER_DATA);
		Placeholders.registerChangeEvent((id, removed) -> ConfigManager.clearCached());
	}


	public static void serverStarting(MinecraftServer s) {
		crabboardDetection();
		ConfigManager.loadConfig(s.getRegistryManager());
		server = s;
	}

	public static void serverStopped(MinecraftServer s) {
		server = null;
	}


	private static void crabboardDetection() {
		if (ModList.get().isLoaded("cardboard")) {
			LOGGER.error("");
			LOGGER.error("Cardboard detected! This mod doesn't work with it!");
			LOGGER.error("You won't get any support as long as it's present!");
			LOGGER.error("");
			LOGGER.error("Read more: https://gist.github.com/Patbox/e44844294c358b614d347d369b0fc3bf");
			LOGGER.error("");
		}
	}
}
