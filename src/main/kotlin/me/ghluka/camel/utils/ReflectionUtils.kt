package me.ghluka.camel.utils

import java.lang.reflect.Field
import java.lang.reflect.Method

class ReflectionUtils {
    companion object {
        fun invoke(obj: Any, methodName: String): Boolean {
            try {
                val method: Method = obj.javaClass.getDeclaredMethod(methodName, *arrayOfNulls(0))
                method.setAccessible(true)
                method.invoke(obj, arrayOfNulls<Any>(0))
                return true
            } catch (_: Exception) {
                return false
            }
        }

        fun field(obj: Any, name: String): Any? {
            try {
                val field: Field = obj.javaClass.getDeclaredField(name)
                field.setAccessible(true)
                return field.get(obj)
            } catch (_: Exception) {
                return null
            }
        }

        fun asField(obj: Any, name: String): Field? {
            try {
                val field: Field = obj.javaClass.getDeclaredField(name)
                field.setAccessible(true)
                return field
            } catch (_: Exception) {
                return null
            }
        }
    }
}
