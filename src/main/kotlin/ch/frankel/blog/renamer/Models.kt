package ch.frankel.blog.renamer

import org.eclipse.jface.viewers.IStructuredContentProvider
import org.eclipse.jface.viewers.Viewer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

object PathModel {

    private var path: String = System.getProperty("user.home")

    @Subscribe
    @Suppress("UNUSED")
    fun onDirectoryPathUpdated(event: DirectoryPathUpdatedEvent) {
        if (path != event.path) {
            EventBus.getDefault().post(PathModelUpdatedEvent(event.path))
            path = event.path
        }
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onDirectoryChosen(event: DirectoryChosenEvent) {
        if (path != event.path) {
            EventBus.getDefault().post(PathModelUpdatedEvent(event.path))
            path = event.path
        }
    }
}

class FileModel : IStructuredContentProvider {
    private var files: List<*> = listOf<Any>()
    override fun getElements(any: Any) = files.toTypedArray()
    override fun inputChanged(viewer: Viewer, oldInput: Any?, newInput: Any?) {
        files = newInput as? List<*> ?: listOf<File>()
    }
}