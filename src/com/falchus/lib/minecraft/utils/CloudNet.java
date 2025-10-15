package com.falchus.lib.minecraft.utils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceCreateResult;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import eu.cloudnetservice.modules.bridge.player.CloudPlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for CloudNet-related operations.
 */
@UtilityClass
public class CloudNet {

	public static final BridgeServiceHelper bridgeServiceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);
	public static final PlayerManager playerManager = InjectionLayer.ext().instance(ServiceRegistry.class).defaultInstance(PlayerManager.class);
	public static final CloudServiceFactory cloudServiceFactory = InjectionLayer.ext().instance(CloudServiceFactory.class);
	public static final CloudServiceProvider cloudServiceProvider = InjectionLayer.ext().instance(CloudServiceProvider.class);
	
	/**
	 * Broadcasts a message to all players globally.
	 */
	public static void broadcastMessage(@NonNull List<String> message) {
		final String messageFinal = String.join("\n", message);
        Component component = LegacyComponentSerializer.legacySection().deserialize(messageFinal);
        playerManager.globalPlayerExecutor().sendChatMessage(component);
	}
	
	/**
	 * Published an update for the current service info snapshot.
	 */
	public static void publishServiceInfoUpdate() {
        ServiceInfoHolder serviceInfoHolder = InjectionLayer.ext().instance(ServiceInfoHolder.class);
        serviceInfoHolder.publishServiceInfoUpdate();
	}
	
	/**
	 * Creates and starts a new CloudNet service.
	 */
	public static void createAndStartService(@NonNull ServiceConfiguration serviceConfig) {
		cloudServiceFactory.createCloudServiceAsync(serviceConfig)
			.thenAccept(result -> {
				if (result != null && result.state() == ServiceCreateResult.State.CREATED) {
					UUID uuid = result.serviceInfo().serviceId().uniqueId();
					ServiceInfoSnapshot service = cloudServiceProvider.service(uuid);
					service.provider().startAsync();
				}
			});
	}
	
	/**
	 * Gets the player count for the group.
	 */
	public static int getPlayerCountFromGroup(String group) {
		Collection<CloudPlayer> players = playerManager.onlinePlayers().players();
		Collection<ServiceInfoSnapshot> services = cloudServiceProvider.servicesByGroup(group);
		int playerCount = 0;

		for (ServiceInfoSnapshot service : services) {
			playerCount += players.stream()
									.filter(player -> player.connectedService().serviceId().equals(service.serviceId()))
									.count();
		}
		return playerCount;
	}
}
