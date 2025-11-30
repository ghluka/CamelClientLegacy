package me.ghluka.camel.utils

import java.nio.charset.StandardCharsets

class ShaderUtils {
    companion object {

        fun loadShader(path: String): String {
            val stream = ShaderUtils::class.java.getResourceAsStream("/assets/camel/$path")
                ?: throw RuntimeException("Shader not found: $path")

            return try {
                stream.readBytes().toString(StandardCharsets.UTF_8)
            } catch (e: Exception) {
                throw RuntimeException(e)
            } finally {
                stream.close()
            }
        }
    }
}
