package com.github.patou.gitmoji

import com.github.patou.gitmoji.PinYinUtil.getPinyin
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
import org.junit.Test
import java.util.*


object PinYinUtil {
    const val LOWERCASE = 1
    const val HUMPCASE = 2
    const val UPPERCASE = 3
    const val ABBREVIATIONCASE = 4

    /**
     * 将汉字转为拼音字符串,传入的字母不会转换大小写
     *
     * @param china 汉字字符串
     * @param type  转换的方式，可选全小写和驼峰两种
     * @return 汉字转拼音 其它字符不变
     */
    fun getPinyin(china: String, type: Int?): String {
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.LOWERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        format.vCharType = HanyuPinyinVCharType.WITH_V
        val arrays = china.trim { it <= ' ' }.toCharArray()
        var result = ""
        try {
            for (i in arrays.indices) {
                val ti = arrays[i]
                if (ti.toString().matches(Regex("[\\u4e00-\\u9fa5]"))) { //匹配是否是中文
                    val temp = PinyinHelper.toHanyuPinyinStringArray(ti, format)
                    if (temp != null) {
                        // 当传入的类型不是这两个的方式时，默认小写
                        when (type) {
                            LOWERCASE -> result += temp[0]
                            HUMPCASE -> {
                                val chars = temp[0].toCharArray()
                                // 首字母大写
                                chars[0] = chars[0] -32
                                result += String(chars)
                                break
                            }

                            UPPERCASE -> result += temp[0].uppercase(Locale.getDefault())
                            ABBREVIATIONCASE -> result += temp[0].substring(0, 1)
                            else -> result += temp[0]
                        }
                    } else {
                        result = "库中无该汉字拼音，其可能为生僻字"
                    }
                } else {
                    result += ti
                }
            }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 判断字符串是否包含汉字
     *
     * @param str 要判断的字符串
     * @return 返会是否含有
     */
    fun hasChinese(str: String?): Boolean {
        if (str == null) {
            return false
        }
        for (c in str.toCharArray()) {
            return c.toString().matches(Regex("[\\u4e00-\\u9fa5]"))
        }
        return false
    }
}

class PinyinTest {
    @Test
    fun test01() {
        println(getPinyin("你好，世界。Hello,world", PinYinUtil.LOWERCASE))
        println(getPinyin("你好，世界。Hello,world", PinYinUtil.HUMPCASE))
        println(getPinyin("你好，世界。Hello,world", PinYinUtil.UPPERCASE))
        println(getPinyin("你好，世界。Hello,world", PinYinUtil.ABBREVIATIONCASE))
    }
}