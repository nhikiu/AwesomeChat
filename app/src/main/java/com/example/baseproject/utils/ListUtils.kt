package com.example.baseproject.utils

import com.example.baseproject.models.Friend

object ListUtils {
    private fun removeVietnameseAccents(input: String): String {
        val originalChars = "àảãáạăằẳẵắặâầẩẫấậèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵđ"
        val replaceChars = "aaaaaaaaaaaaaaaaaeeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyd"

        val sb = StringBuilder()
        for (c in input) {
            val index = originalChars.indexOf(c)
            if (index >= 0) {
                sb.append(replaceChars[index])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun getListSortByName(friendList: List<Friend>) : List<Any> {
        val sortList = sortByName(friendList.toMutableList()).toMutableList()

        val newList = mutableListOf<Any>()
        for (i in sortList.indices) {
            val current = friendList[i]
            val word = removeVietnameseAccents(current.name.split(" ").last().lowercase()).uppercase()

            if (word.isNotEmpty() && !newList.contains(word.substring(0, 1))) {
                newList.add(word.substring(0,1))
            }
            newList.add(current)
        }
        return newList
    }

    fun sortByName(friendList: MutableList<Friend>): List<Friend> {
        return friendList.toMutableList().sortedWith(
            compareBy(
                { removeVietnameseAccents(it.name.split(" ").last().lowercase()) },
                { removeVietnameseAccents(it.name.split(" ").first().lowercase()) },
                { removeVietnameseAccents(it.name.lowercase()) },
                {it.id}
            )
        )
    }
}