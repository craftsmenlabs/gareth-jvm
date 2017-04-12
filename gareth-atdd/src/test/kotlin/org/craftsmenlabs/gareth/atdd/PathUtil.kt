package org.craftsmenlabs.gareth.atdd

import java.io.File


object PathUtil {


    fun getProjectRootDir() = getDir("gareth-atdd/")

    fun getAtddProjectDir() = getDir("")

    private fun getDir(toStrip: String): String {
        val packageAsPath = PathUtil::class.java.getPackage().getName().replace("\\.".toRegex(), "/")
        val path = PathUtil::class.java.getResource(".").getFile()
        return path.replace("${toStrip}target/test-classes/$packageAsPath", "").replace("\\/\\/".toRegex(), "\\/")
    }


    fun getJarfile(path: String): String {
        val dir = File(path)
        if (!dir.isDirectory)
            throw IllegalArgumentException("$dir is not a directory")
        val files = dir.listFiles({ file: File -> file.name.contains("gareth") && file.name.endsWith("jar") })
        if (files.size != 1)
            throw IllegalStateException("Expected only one jar file in dir $path, but found ${files.size}")
        return files[0].canonicalPath
    }
}