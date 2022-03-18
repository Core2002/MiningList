package `fun`.fifu.mininglist

import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import kotlin.concurrent.thread

object Middleware {
    lateinit var data: JSONObject
    lateinit var uuid2name: JSONObject
    lateinit var ignore: JSONObject
    const val pluginName = "FiFuMiningList"
    lateinit var ranking: ArrayList<String>


    fun init() {
        data = initConfigFile("data")
        uuid2name = initConfigFile("uuid2name")
        ignore = initConfigFile("ignore")


        thread(start = true) {
            while (true) {
                Thread.sleep(1000 * 60)
                saveConfigFile(data, "data")
                saveConfigFile(uuid2name, "uuid2name")
                saveConfigFile(ignore, "ignore")
            }
        }

        thread(start = true) {
            while (true) {
                Thread.sleep(1000L)
                ranking = calLeaderboard()
            }
        }
    }

    fun uninit() {
        saveConfigFile(data, "data")
        saveConfigFile(uuid2name, "uuid2name")
        saveConfigFile(ignore, "ignore")
    }

    fun calLeaderboard(): ArrayList<String> {
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

    fun putData(uuid: String, num: String) {
        data[uuid] = num
    }

    fun readData(uuid: String): String {
        if (data[uuid] == null)
            return "0"
        return data[uuid] as String
    }

    fun putUuid2Name(uuid: String, name: String) {
        uuid2name[uuid] = name
        saveConfigFile(uuid2name, "uuid2name")
    }

    fun uuid2name(uuid: String): String {
        if (uuid2name[uuid] == null)
            return "<null>"
        return uuid2name[uuid] as String
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
    }

    fun removeIgnore(uuid: String) {
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

}