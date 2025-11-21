package com.duckblade.osrs.sailing;

import com.duckblade.osrs.sailing.model.Boat;
import com.duckblade.osrs.sailing.model.HelmTier;
import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
public class RapidsOverlay extends Overlay
{

	private static final Color COLOR_RAPID_SAFE = Color.CYAN;
	private static final Color COLOR_RAPID_DANGER = Color.RED;
	private static final Color COLOR_RAPID_UNKNOWN = Color.YELLOW;

	private final Map<Integer, HelmTier> MIN_HELM_TIER_BY_RAPID_TYPE = ImmutableMap.<Integer, HelmTier>builder()
		.put(ObjectID.SAILING_RAPIDS, HelmTier.IRON)
		.put(ObjectID.SAILING_RAPIDS_STRONG, HelmTier.MITHRIL)
		.put(ObjectID.SAILING_RAPIDS_POWERFUL, HelmTier.RUNE)
		.build();

	private final Client client;
	private final SailingConfig config;
	private final BoatTracker boatTracker;

	private final Set<GameObject> rapids = new HashSet<>();

	@Inject
	public RapidsOverlay(Client client, SailingConfig config, BoatTracker boatTracker)
	{
		this.client = client;
		this.config = config;
		this.boatTracker = boatTracker;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	void shutDown()
	{
		rapids.clear();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned e)
	{
		GameObject o = e.getGameObject();
		if (SailingUtil.RAPIDS_IDS.contains(o.getId()))
		{
			rapids.add(o);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned e)
	{
		rapids.remove(e.getGameObject());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOADING)
		{
			rapids.clear();
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!SailingUtil.isSailing(client) || !config.highlightRapids())
		{
			return null;
		}

		for (GameObject rapid : rapids)
		{
			OverlayUtil.renderTileOverlay(graphics, rapid, "", getHighlightColor(rapid));
		}

		return null;
	}

	private Color getHighlightColor(GameObject rapid)
	{
		HelmTier minTier = MIN_HELM_TIER_BY_RAPID_TYPE.get(rapid.getId());
		if (minTier == null)
		{
			return COLOR_RAPID_UNKNOWN;
		}

		Boat boat = boatTracker.getBoat(client.getLocalPlayer().getWorldView().getId());
		if (boat == null)
		{
			return COLOR_RAPID_UNKNOWN;
		}

		HelmTier helmTier = boat.getHelmTier();
		if (helmTier == null)
		{
			return COLOR_RAPID_UNKNOWN;
		}

		if (helmTier.ordinal() >= minTier.ordinal())
		{
			return COLOR_RAPID_SAFE;
		}

		return COLOR_RAPID_DANGER;
	}
}
