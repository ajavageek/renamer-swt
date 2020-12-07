package ch.frankel.blog.renamer

import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Shell
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.nio.file.Files
import java.util.regex.PatternSyntaxException


class RenamerButton(gridData: GridData, shell: Shell, eventBus: EventBus) : Button(shell, SWT.PUSH) {

    private var root: File? = null
    private var regex = "".toRegex()
    private var replacement = ""

    init {
        eventBus.register(this)
        layoutData = gridData
        text = "Apply"
        addListener(SWT.Selection) {
            if (root != null && regex.pattern.isNotEmpty()) {
                root.children()
                    ?.forEach {
                        val source = it.toPath()
                        val target = source.resolveSibling(regex.replace(it.name, replacement))
                        Files.move(source, target)
                    }
                eventBus.post(RenamedEvent())
            }
        }
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onPathModelUpdated(event: PathModelUpdatedEvent) {
        root = File(event.path)
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onPatternUpdated(event: PatternUpdatedEvent) {
        try {
            regex = event.pattern.toRegex()
        } catch (e: PatternSyntaxException) {
            // NOTHING TO DO
        }
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onReplacementUpdated(event: ReplacementUpdatedEvent) {
        replacement = event.replacement
    }

    override fun checkSubclass() {}
}