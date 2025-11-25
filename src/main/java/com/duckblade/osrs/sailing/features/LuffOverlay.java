package com.duckblade.osrs.sailing.features;

import com.duckblade.osrs.sailing.SailingConfig;
import com.duckblade.osrs.sailing.features.util.SailingUtil;
import com.duckblade.osrs.sailing.features.util.BoatTracker;
import com.duckblade.osrs.sailing.model.Boat;
import com.duckblade.osrs.sailing.module.PluginLifecycleComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Slf4j
@Singleton
public class LuffOverlay
	extends Overlay
	implements PluginLifecycleComponent
{

	private static final String CHAT_LUFF_AVAILABILE = "You feel a gust of wind.";
	private static final String CHAT_LUFF_PERFORMED = "You trim the sails, catching the wind for a burst of speed!";
	private static final String CHAT_LUFF_ENDED = "The wind dies down and your sails with it.";

	private final Client client;
	private final SailingConfig config;
	private final BoatTracker boatTracker;
	private final ModelOutlineRenderer modelOutlineRenderer;

	private boolean needLuff = false;

	@Inject
	public LuffOverlay(Client client, SailingConfig config, BoatTracker boatTracker, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.config = config;
		this.boatTracker = boatTracker;
		this.modelOutlineRenderer = modelOutlineRenderer;

		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public boolean isEnabled(SailingConfig config)
	{
		return config.highlightTrimmableSails();
	}

	@Subscribe
	public void onChatMessage(ChatMessage e)
	{
		if (!SailingUtil.isSailing(client))
		{
			return;
		}

		String msg = e.getMessage();
		if (CHAT_LUFF_AVAILABILE.equals(msg))
		{
			needLuff = true;
		}
		else if (CHAT_LUFF_PERFORMED.equals(msg) || CHAT_LUFF_ENDED.equals(msg))
		{
			needLuff = false;
		}
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!needLuff || !SailingUtil.isSailing(client) || !config.highlightTrimmableSails())
		{
			return null;
		}

		Boat boat = boatTracker.getBoat();
		GameObject sail = boat != null ? boat.getSail() : null;
		if (sail == null)
		{
			return null;
		}

		SailingConfig.SailHighlightMode mode = config.sailHighlightMode();

		if (mode == SailingConfig.SailHighlightMode.AREA)
		{
			Shape shape = sail.getConvexHull();
			if (shape != null)
			{
				OverlayUtil.renderPolygon(g, shape, Color.green);
			}
		}
		else if (mode == SailingConfig.SailHighlightMode.SAIL)
		{
			modelOutlineRenderer.drawOutline(sail, 2, Color.green, 250);
		}

		return null;
	}
}
