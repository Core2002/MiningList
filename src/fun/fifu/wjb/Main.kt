package `fun`.fifu.wjb

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset


class Main : JavaPlugin(), Listener {
    private lateinit var data: JSONObject
    private lateinit var uuid2name: JSONObject
    private lateinit var ignore: JSONObject
    private val pluginName = "wajuebang"
    private lateinit var ranking: ArrayList<String>
    private lateinit var emptyScoreboard: Scoreboard

    companion object {
        lateinit var plugin: Plugin

        @JvmStatic
        fun main(args: Array<String>) {
            for (x in 0 until 16)
                println(16 - 1 - x)
        }


    }

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        data = initConfigFile("data")
        uuid2name = initConfigFile("uuid2name")
        ignore = initConfigFile("ignore")
        emptyScoreboard = Bukkit.getServer().scoreboardManager.newScoreboard
        for (p in server.onlinePlayers)
            putUuid2Name(p.uniqueId.toString(), p.displayName)
        object : BukkitRunnable() {
            override fun run() {
                saveConfigFile(data, "data")
                saveConfigFile(uuid2name, "uuid2name")
                saveConfigFile(ignore, "ignore")
            }
        }.runTaskTimerAsynchronously(plugin, 20, 20 * 60)
        object : BukkitRunnable() {
            override fun run() {
                ranking = calLeaderboard()
                loadBoard()
            }
        }.runTaskTimer(plugin, 20, 20)
        server.pluginManager.registerEvents(this, this)
        server.logger.info("挖掘榜插件已经加载，author: NekokeCore")
    }

    override fun onDisable() {
        saveConfigFile(data, "data")
        saveConfigFile(uuid2name, "uuid2name")
        saveConfigFile(ignore, "ignore")
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
        if (!inIgnore(uuid))
            addIgnore(uuid)
        else
            removeIgnore(uuid)
        return true
    }

    private fun calLeaderboard(): ArrayList<String> {
        val arr = arrayListOf<BigInteger>()
        for (x in data.keys)
            arr.add(BigInteger(readData(x.toString())))
        quickSort(arr, 0, arr.size - 1)

        val over = arrayListOf<String>()
        for (x in arr) {
            for (u in data.keys) {
                val num = BigInteger(data[u] as String)
                if (num == x)
                    over.add(u.toString())
            }
        }

        val end = ArrayList<String>()
        for (x in 0 until over.size)
            if (!end.contains(over[over.size - 1 - x]))
                end.add(over[over.size - 1 - x])

        return end
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        putUuid2Name(player.uniqueId.toString(), player.displayName)
    }

    @EventHandler
    private fun onBreak(event: BlockBreakEvent) {
        val uuid = event.player.uniqueId.toString()
        var num = readData(uuid)
        num = BigInteger(num).add(BigInteger.ONE).toString()
        putData(uuid, num)
    }

    private fun putData(uuid: String, num: String) {
        data[uuid] = num
    }

    private fun readData(uuid: String): String {
        if (data[uuid] == null)
            return "0"
        return data[uuid] as String
    }

    private fun putUuid2Name(uuid: String, name: String) {
        uuid2name[uuid] = name
        saveConfigFile(uuid2name, "uuid2name")
    }

    private fun uuid2name(uuid: String): String {
        return uuid2name[uuid] as String
    }

    private fun inIgnore(uuid: String): Boolean {
        for (x in ignore.keys) {
            if (x as String == uuid)
                return true
        }
        return false
    }

    private fun addIgnore(uuid: String) {
        ignore[uuid] = System.currentTimeMillis().toString()
    }

    private fun removeIgnore(uuid: String) {
        ignore.remove(uuid)
    }

    private fun saveConfigFile(jsonObject: JSONObject, name: String) {
        val file = File("./plugins/${pluginName}/${name}.json")
        if (!file.isFile)
            initConfigFile(name)
        file.writeText(jsonObject.toJSONString())
    }

    private fun initConfigFile(name: String): JSONObject {
        val file = File("./plugins/${pluginName}/${name}.json")
        if (!file.isFile) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText("{}")
        }
        val text = file.readText(Charset.forName("utf-8"))
        return JSONValue.parse(text) as JSONObject
    }

    private fun quickSort(a: ArrayList<BigInteger>, left: Int, right: Int) {
        if (left > right) return
        val pivot = a[left] //定义基准值为数组第一个数
        var i = left
        var j = right
        while (i < j) {
            while (pivot <= a[j] && i < j) //从右往左找比基准值小的数
                j--
            while (pivot >= a[i] && i < j) //从左往右找比基准值大的数
                i++
            if (i < j) //如果i<j，交换它们
            {
                val temp = a[i]
                a[i] = a[j]
                a[j] = temp
            }
        }
        a[left] = a[i]
        a[i] = pivot //把基准值放到合适的位置
        quickSort(a, left, i - 1) //对左边的子数组进行快速排序
        quickSort(a, i + 1, right) //对右边的子数组进行快速排序
    }

    fun loadBoard() {
        val scoreboard: Scoreboard = Bukkit.getServer().scoreboardManager.newScoreboard
        val objective: Objective = scoreboard.registerNewObjective(pluginName, "dummy", "挖掘榜")
        objective.displaySlot = DisplaySlot.SIDEBAR
        var size = ranking.size
        if (size > 16)
            size = 16
        else if (size == 0)
            return
        for (x in 0 until size)
            objective.getScore("§fNo.${size - x}: §b" + uuid2name(ranking[size - x - 1]) + " §e" + readData(ranking[size - x - 1])).score =
                x
        for (p in server.onlinePlayers) {
            if (inIgnore(p.uniqueId.toString())) {
                if (p.scoreboard != emptyScoreboard)
                    p.scoreboard = emptyScoreboard
                continue
            }
            p.scoreboard = scoreboard
        }
    }
}