package com.duckblade.osrs.sailing.features.barracudatrials;

import com.duckblade.osrs.sailing.SailingConfig;
import com.duckblade.osrs.sailing.features.util.SailingUtil;
import com.duckblade.osrs.sailing.module.PluginLifecycleComponent;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HidePortalTransitions
	implements PluginLifecycleComponent
{
	private static final int SCRIPT_PORTAL_TRANSITION_EFFECT = 5986;

	private final Client client;

	@Override
	public boolean isEnabled(SailingConfig config)
	{
		return config.barracudaHidePortalTransitions();
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != SCRIPT_PORTAL_TRANSITION_EFFECT)
		{
			return;
		}

		if (!SailingUtil.isSailing(client))
		{
			return;
		}

		Arrays.fill(event.getScriptEvent().getArguments(), 0);
	}

}
