package ch.frankel.blog.renamer

import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File


class FolderPickerButton(gridData: GridData, shell: Shell, eventBus: EventBus) : Button(shell, SWT.PUSH) {

    private var currentDirectory: File? = null

    init {
        eventBus.register(this)
        layoutData = gridData
        text = "Browse..."
        addListener(SWT.Selection) {
            val directoryDialog = DirectoryDialog(shell)
            directoryDialog.filterPath = currentDirectory?.path
            val folder = directoryDialog.open()
            if (folder != null) {
                currentDirectory = File(folder)
                eventBus.post(DirectoryChosenEvent(folder))
            }
        }
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onPathModelUpdated(event: PathModelUpdatedEvent) {
        currentDirectory = File(event.path)
    }

    override fun checkSubclass() {}
}