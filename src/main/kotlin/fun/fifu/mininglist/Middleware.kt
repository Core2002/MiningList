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
import org.bukkit.Material
import org.bukkit.Statistic
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.*

object Middleware {
    var data: JSONObject
    var uuid2name: JSONObject
    var ignore: JSONObject
    const val pluginName = "FiFuMiningList"
    lateinit var ranking: ArrayList<String>

    init {
        data = initConfigFile("data")
        uuid2name = initConfigFile("uuid2name")
        ignore = initConfigFile("ignore")
    }

    fun init() {
        ranking = calLeaderboard()
    }

    fun unInit() {
        saveAll()
    }

    private val arr = arrayListOf<BigInteger>()
    private val over = arrayListOf<String>()
    private val end = ArrayList<String>()
    fun calLeaderboard(): ArrayList<String> {
        arr.clear()
        for (x in data.keys)
            arr.add(BigInteger(readData(x.toString())))
        quickSort(arr, 0, arr.size - 1)
        over.clear()
        for (x in arr) {
            for (u in data.keys) {
                val num = BigInteger(data[u] as String)
                if (num == x)
                    over.add(u.toString())
            }
        }
        end.clear()
        for (x in 0 until over.size)
            if (!end.contains(over[over.size - 1 - x]))
                end.add(over[over.size - 1 - x])

        return end
    }

    fun readData(uuid: String): String {
        if (data[uuid] == null)
            return "0"
        return data[uuid] as String
    }

    fun putData(uuid: String, num: String) {
        data[uuid] = num
        makeChangeFlag()
    }

    fun uuid2name(uuid: String): String {
        if (uuid2name[uuid] == null)
            return "<null>"
        return uuid2name[uuid] as String
    }

    fun putUuid2Name(uuid: String, name: String) {
        if (!uuid2name.contains(uuid)) {
            var oldMineBlockNum = 0
            for (m in Material.entries) {
                if (m.isBlock) {
                    oldMineBlockNum +=
                        Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getStatistic(Statistic.MINE_BLOCK, m)
                }
            }
            uuid2name[uuid] = name
            MiningList.plugin.logger.info("成功为玩家 $name 加载统计数据")
            val mineBlockNum = BigInteger(oldMineBlockNum.toString())
            putData(uuid, mineBlockNum.toString())
        }
        uuid2name[uuid] = name
        makeChangeFlag()
    }

    fun inIgnore(uuid: String): Boolean {
        for (x in ignore.keys) {
            if (x as String == uuid)
                return true
        }
        return false
    }

    fun addIgnore(uuid: String) {
        ignore[uuid] = System.currentTimeMillis().toString()
        makeChangeFlag()
    }

    fun removeIgnore(uuid: String) {
        ignore.remove(uuid)
    }

    private var lastChangeTime = System.currentTimeMillis()

    private fun makeChangeFlag() {
        if (canDoSave()){
            saveAll()
        }
        lastChangeTime = System.currentTimeMillis()
    }

    private fun canDoSave(): Boolean {
        return System.currentTimeMillis() > lastChangeTime + 1000 * 5
    }

    private fun saveAll() {
        ranking = calLeaderboard()
        saveConfigFile(data, "data")
        saveConfigFile(uuid2name, "uuid2name")
        saveConfigFile(ignore, "ignore")
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

}