/*
 * Copyright (c) 2023 NekokeCore(Core2002@aliyun.com)
 * FiFuPowered is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package `fun`.fifu.mininglist

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.math.BigInteger


class MiningList : JavaPlugin(), Listener {

    companion object {
        lateinit var plugin: MiningList
        lateinit var emptyScoreboard: Scoreboard
    }

    override fun onLoad() {
        plugin = this
    }

    private lateinit var br: BukkitTask
    override fun onEnable() {
        emptyScoreboard = Bukkit.getServer().scoreboardManager.newScoreboard
        Middleware.init()
        for (p in server.onlinePlayers)
            Middleware.putUuid2Name(p.uniqueId.toString(), p.displayName)
        br = object : BukkitRunnable() {
            override fun run() {
                loadBoard()
            }
        }.runTaskTimer(plugin, 20, 20)
        server.pluginManager.registerEvents(this, this)
        server.logger.info("挖掘榜插件已经加载，author: NekokeCore 群：710789659")
    }

    override fun onDisable() {
        br.cancel()
        Middleware.unInit()
        server.logger.info("挖掘榜插件已卸载，感谢使用，")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label != "wjb")
            return false
        else if (sender !is Player) {
            sender.sendMessage("你必须是一位玩家")
            return true
        }
        val uuid = sender.uniqueId.toString()
        if (!Middleware.inIgnore(uuid))
            Middleware.addIgnore(uuid)
        else
            Middleware.removeIgnore(uuid)
        return true
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        Middleware.putUuid2Name(player.uniqueId.toString(), player.displayName)
    }

    @EventHandler
    private fun onBreak(event: BlockBreakEvent) {
        val uuid = event.player.uniqueId.toString()
        var num = Middleware.readData(uuid)
        num = BigInteger(num).add(BigInteger.ONE).toString()
        Middleware.putData(uuid, num)
    }

    fun loadBoard() {
        try {
            val scoreboard: Scoreboard = Bukkit.getServer().scoreboardManager.newScoreboard
            val objective: Objective = scoreboard.registerNewObjective(Middleware.pluginName, "dummy", "挖掘榜")
            objective.displaySlot = DisplaySlot.SIDEBAR
            var size = Middleware.ranking.size
            if (size > 16)
                size = 16
            else if (size == 0)
                return
            for (x in 0 until size)
                objective.getScore(
                    "§fNo.${size - x}: §b" + Middleware.uuid2name(Middleware.ranking[size - x - 1]) + " §e" + Middleware.readData(
                        Middleware.ranking[size - x - 1]
                    )
                ).score =
                    x
            for (p in server.onlinePlayers) {
                if (Middleware.inIgnore(p.uniqueId.toString())) {
                    if (p.scoreboard != emptyScoreboard)
                        p.scoreboard = emptyScoreboard
                    continue
                }
                p.scoreboard = scoreboard
            }
        } catch (_: Exception) {
        }
    }
}
