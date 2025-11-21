package com.duckblade.osrs.sailing;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@Getter
public enum SeaChartTask
{
	;

	private final int taskId;
	private final WorldPoint point;
	@Nullable
	private final NPC npc;
	@Nullable
	private final GameObject gameObject;

	@Inject
	private final Client client;
	private final int[] TASK_VARP_IDS = {5016, 5017, 5018, 5019, 5020, 5021, 5022, 5023, 5024, 5025, 5026, 5027};

	public boolean isCompleted()
	{
		final int varpIndex = taskId / 32;
		final int bitIndex = taskId % 32;
		return (client.getVarpValue(TASK_VARP_IDS[varpIndex]) & (1 << bitIndex)) != 0;
	}
}
