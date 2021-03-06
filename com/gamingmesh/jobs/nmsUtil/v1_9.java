package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.NMS;

public class v1_9 implements NMS {
    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	List<Block> blocks = new ArrayList<Block>();
	blocks.addAll(event.getBlocks());
	return blocks;
    }

    @Override
    public boolean isElderGuardian(Entity entity) {
	if (entity instanceof Guardian) {
	    Guardian guardian = (Guardian) entity;
	    if (guardian.isElder())
		return true;
	}
	return false;
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInMainHand();
    }

    @Override
    public Block getTargetBlock(Player player, int range) {
	return player.getTargetBlock((Set<Material>) null, range);
    }

}
