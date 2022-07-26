This is a fairly simple mod that implements the 'plagued skies' effect.

Under this effect, the (overworld) sky will slowly be blotted out by
impenetrable dragonskin blocks (similar to bedrock) that spread like a cancer.

The plague can be cured by travelling to the end, killing the dragon, and
placing the egg in the overworld. The skin will slowly dissolve, occasionally
dropping dead skin in the form of dragonscale.

Details:

At the start of the game, pieces of dragonskin will randomly be seeded in the
sky. They will randomly spread to adjcent spaces, which will in turn spread to
other adjacent spaces. If no spread happens in a given amount of time (for
instance, no loaded chunks have skin in them), new growths will be seeded. You
can outrun the darkness, but it will slowly catch up.

Once the dragon egg is placed in the overworld, the blocks of skin will
randomly die, with a random chance of dropping dragonscale, which falls like
gravel. The egg need only be placed once; healing will continue even if it is
picked up again.

Small patches of plague can be temporarily removed by placing a beacon
underneath it (activating it is not necessary), with no blocks in between.
This only removes plague when the beacon is placed; a sitting beacon will
not prevent growth around it.

Configuration:

The plaguesky.cfg file has a few configuration options to control the spread
and decay of dragonskin:

seedTime: If no existing dragonskin blocks spread in this many seconds, a
new one is seeded in a loaded chunk.

seedChunks: When seeding new dragonskin growths, how many chunks to add new
growths to

growthPercent: This controls the rate of spread of existing growths. Each time
a dragonskin block ticks (similar to grass growing), it has this percentage
chance of spreading to an adjacent space. This can be greater than 100
percent, in which case multiple growths can spawn per blocktick. 200 = two
growths per blocktick. 150 = either one or two growths per blocktick. etc.

spreadDelay: This causes plague block spread to be batched and performed at
once, with an interval of this many seconds. The overall growth rate should be
roughly the same, but it might help with lag.

spreadCap: The maximum number of block spreads performed in a single update.
This limits the total spread to an average of spreadCap per spreadDelay
seconds. Lower this if you're getting a lot of lag/dropped ticks on spreading.
(0=No limit)

decayPercent: The speed at which dragonskin decays in heal mode. 100 is
roughtly the speed that grass dies when covered. If decaying too much at once
lags, try lowering this.

sloughPercent: When healing is active, a dragonskin block has this percentage
chance of dropping dragonscale, as opposed to simply disappearing.

orePercent: The percent chance that, upon landing, a dragonscale block turns
into a valuable ore. Also controls the speed at which an already-landed block
turns into ore.

beaconBlastRadius: Size of area affected when a beacon is placed under
a plague patch

skinOres: The ores that might be released from Dragonscale. This can be a
resource ID (minecraft:stone), a resource ID with metadata (minecraft:coal:1)
or an oredict entry (oreIron). Note that despite the name, this can be any
placeable block, not just ores.

healDefault: Start the world in heal mode. Perhaps you want to configure your
pack to start the plague when a certain event happens. (The command
/healplague off can start it.)

patchyDecay: If true, dragonskin decays in 'patches' of blocks, rather than
individual blocks

Commands:

There is one command, avaiable to ops:

/healplague: Turns plague healing on or off. (/healplague on, or /healplague
off)

There is another command, not strictly related to the Plagued Sky effect. It
serves mainly as a helper command for the Creeping Dark pack:

/loadchunk <dimension> <chunkX> <chunkZ>

This loads the chunk at the given dimension and coordinates.

Changes:

v1.4:
 * Added feature to remove dragonskin using beacon

