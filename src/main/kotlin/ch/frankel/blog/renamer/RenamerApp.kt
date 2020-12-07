package ch.frankel.blog.renamer

import org.eclipse.swt.SWT.FILL
import org.eclipse.swt.SWT.LEFT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridData.FILL_HORIZONTAL
import org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import org.greenrobot.eventbus.EventBus
import java.io.File

fun main() {
    val eventBus = EventBus.getDefault()
    eventBus.register(PathModel)
    RenamerApp.run(eventBus)
}

object RenamerApp {

    private val display = Display()
    private val shell = Shell(display).apply {
        layout = GridLayout(4, false)
    }

    fun run(eventBus: EventBus) {
        createGui(eventBus)
        startEventLoop()
    }

    private fun startEventLoop() {
        shell.open()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
        display.dispose()
    }

    private fun createGui(eventBus: EventBus) {
        label("Folder:", shell)
        DirectoryTextField(GridData(FILL_HORIZONTAL).also { it.horizontalSpan = 2 }, shell, eventBus)
        FolderPickerButton(GridData(HORIZONTAL_ALIGN_END), shell, eventBus)
        label("Regex:", shell)
        PatternTextField(GridData(FILL_HORIZONTAL), shell, eventBus)
        label("Replacement:", shell)
        ReplacementTextField(GridData(FILL_HORIZONTAL), shell, eventBus)
        FileTable(
            GridData(FILL, FILL, true, true, 4, 1),
            shell,
            eventBus,
        )
        RenamerButton(GridData(HORIZONTAL_ALIGN_END).also { it.horizontalSpan = 4 }, shell, eventBus)
    }
}

internal fun File?.children() = this?.listFiles()
    ?.filter { !it.isHidden && it.isFile }
    ?.toList()

private fun label(text: String, parent: Composite) = Label(parent, LEFT).apply { this.text = text }