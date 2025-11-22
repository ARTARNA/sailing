package com.duckblade.osrs.sailing.features.util;

import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.gameval.VarbitID;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SailingUtil
{

	public static final int WORLD_ENTITY_TYPE_BOAT = 2;
	public static final int ACCOUNT_TYPE_UIM = 2;

	public static boolean isSailing(Client client)
	{
		return client.getLocalPlayer() != null &&
			!client.getLocalPlayer().getWorldView().isTopLevel();
	}

	public static boolean isUim(Client client)
	{
		return client.getVarbitValue(VarbitID.IRONMAN) == ACCOUNT_TYPE_UIM;
	}

	// on boats, InteractingChanged fires for the local player but the target is null
	// it DOES fire an event with the expected target for a separate instance of Player with the same ID
	public static boolean isLocalPlayer(Client client, Actor actor)
	{
		return client.getLocalPlayer() != null &&
			actor instanceof Player && ((Player) actor).getId() == client.getLocalPlayer().getId();
	}

}
