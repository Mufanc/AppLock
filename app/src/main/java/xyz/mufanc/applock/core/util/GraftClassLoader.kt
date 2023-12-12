package xyz.mufanc.applock.core.util

import org.joor.Reflect

object GraftClassLoader : ClassLoader() {

    private const val TAG = "GraftClassLoader"

    private lateinit var cl: ClassLoader

    fun init(cl: ClassLoader) {
        this.cl = cl

        val mine = javaClass.classLoader!!
        val parent: ClassLoader = Reflect.on(mine).get("parent")

        Reflect.on(mine).set("parent", this)
        Reflect.on(this).set("parent", parent)
    }

    override fun findClass(name: String?): Class<*> {
        Log.d(TAG, "loading class: $name")
        return cl.loadClass(name)
    }
}
