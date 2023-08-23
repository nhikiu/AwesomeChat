package com.example.baseproject.utils

import com.example.baseproject.models.Friend
import com.example.baseproject.models.Message

object ListUtils {
    fun removeVietnameseAccents(input: String): String {
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

    fun getListSortByName(friendList: List<Friend>): List<Any> {
        val sortList = sortByName(friendList.toMutableList()).toMutableList()

        val newList = mutableListOf<Any>()
        for (i in sortList.indices) {
            val current = friendList[i]
            val word =
                removeVietnameseAccents(current.name.split(" ").last().lowercase()).uppercase()

            if (word.isNotEmpty() && !newList.contains(word.substring(0, 1))) {
                newList.add(word.substring(0, 1))
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
                { it.id }
            )
        )
    }

    fun sortMessageByTime(messageList: MutableList<Message>): List<Message> {
        return messageList.sortedByDescending { it.sendAt }
    }

    fun getMessageListSortByTime(messageList: MutableList<Message>): List<Any> {
        for (i in messageList) {
            i.position = Constants.POSITION
        }
        var sortList: MutableList<Any> = sortMessageByTime(messageList).toMutableList()

        for (i in sortList.size - 1 downTo 0) {
            var currentItem = sortList[i]
            if (currentItem is Message) {
                val dateTime = DateTimeUtils.convertTimestampToDate(currentItem.sendAt.toLong())
                if (!sortList.contains(dateTime)) {
                    sortList.add(i + 1, dateTime)
                }
            }
        }

        sortList = sortList.reversed().toMutableList()

        for (i in sortList.indices) {
            val pre = sortList.getOrNull(i - 1)
            val cur = sortList[i]
            if (cur is Message && pre !is Message) {
                (sortList[i] as Message).position = Constants.POSITION_ONLY
            } else if (pre is Message && cur is Message && (pre.sendId != cur.sendId)) {
                (sortList[i] as Message).position = Constants.POSITION_ONLY
            } else if (cur is Message && pre is Message && pre.position == Constants.POSITION_ONLY) {
                if (cur.sendId == pre.sendId) {
                    (sortList[i - 1] as Message).position = Constants.POSITION_FIRST
                    (sortList[i] as Message).position = Constants.POSITION_LAST
                } else {
                    (sortList[i] as Message).position = Constants.POSITION_ONLY
                }
            } else if (pre is Message && pre.position == Constants.POSITION_LAST && cur is Message && cur.sendId == pre.sendId) {
                (sortList[i - 1] as Message).position = Constants.POSITION_MIDDLE
                (sortList[i] as Message).position = Constants.POSITION_LAST
            }

        }

        sortList = sortList.reversed().toMutableList()

        val dateTimeList = mutableListOf<Any>()
        for (i in sortList.indices) {
            if (sortList[i] is Message && (sortList[i] as Message).position == Constants.POSITION_LAST) {
                dateTimeList.add(
                    "${(sortList[i] as Message).sendId}_${
                        DateTimeUtils.convertTimestampToDateTime(
                            (sortList[i] as Message).sendAt.toLong()
                        )
                    }"
                )
            }
            dateTimeList.add(sortList[i])
        }

        return dateTimeList
    }
}