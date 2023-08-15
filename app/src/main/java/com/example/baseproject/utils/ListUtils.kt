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

    fun sortMessageByTime(messageList: MutableList<Message>): List<Message> {
        return messageList.sortedByDescending { it.sendAt }
    }

    fun getMessageListSortByTime(messageList: MutableList<Message>) : List<Any> {
        val sortList: MutableList<Message> = sortMessageByTime(messageList).toMutableList()
        for (i in sortList.indices) {
            val preMessage = sortList.getOrNull(i - 1)
            val nextMessage = sortList.getOrNull(i + 1)
            val currentMessage = sortList[i]
            if (preMessage == null && nextMessage == null) {
                sortList[i].position = Constants.POSITION_ONLY
            } else if (preMessage == null && nextMessage != null) {
                if (nextMessage.sendId != currentMessage.sendId) {
                    sortList[i].position = Constants.POSITION_ONLY
                } else {
                    sortList[i].position = Constants.POSITION_LAST
                }
            } else if (preMessage != null && nextMessage == null) {
                if (currentMessage.sendId == preMessage.sendId) {
                    sortList[i].position = Constants.POSITION_FIRST
                } else {
                    sortList[i].position = Constants.POSITION_ONLY
                }
            } else if (preMessage != null && nextMessage != null){
                if (currentMessage.sendId != preMessage.sendId && currentMessage.sendId != nextMessage.sendId) {
                    sortList[i].position = Constants.POSITION_ONLY
                } else if (currentMessage.sendId != preMessage.sendId && currentMessage.sendId == nextMessage.sendId) {
                    sortList[i].position = Constants.POSITION_LAST
                } else if (currentMessage.sendId == preMessage.sendId && currentMessage.sendId != nextMessage.sendId) {
                    sortList[i].position = Constants.POSITION_FIRST
                } else if (currentMessage.sendId == preMessage.sendId && currentMessage.sendId == nextMessage.sendId) {
                    sortList[i].position = Constants.POSITION_MIDDLE
                }
            }
        }

        // Thêm thời gian dạng dd/MM/yyyy HH:mm vào list tin nhắn đã sắp xếp theo thời gian
        val dateTimeList = mutableListOf<Any>()
        for (i in sortList.indices){
            if (sortList[i].position == Constants.POSITION_LAST) {
                dateTimeList.add("${sortList[i].sendId}_${DateTimeUtils.convertTimestampToDateTime(sortList[i].sendAt.toLong())}")
            }
            dateTimeList.add(sortList[i])
        }

        // Thêm thời gian dạng dd/MM/yyyy hoặc Today, Yesterday
        val dateList = mutableListOf<Any>()

        for (i in dateTimeList.size-1 downTo 0) {
            val currentItem = dateTimeList[i]
            if (currentItem is Message) {
                val dateTime = DateTimeUtils.convertTimestampToDate(currentItem.sendAt.toLong())
                if (!dateList.contains(dateTime)) {
                    dateList.add(dateTime)
                }
            }
            dateList.add(dateTimeList[i])
        }

        return dateList.reversed()
    }
}