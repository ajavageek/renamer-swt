package ch.frankel.blog.renamer

import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.RGB
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.regex.PatternSyntaxException


class FileTable(gridData: GridData, parent: Composite, eventBus: EventBus) :
    TableViewer(parent, SWT.BORDER or SWT.V_SCROLL or SWT.H_SCROLL) {

    private var root: File? = null
    private var regex = "".toRegex()
    private var replacement = ""

    init {
        eventBus.register(this)
        table.layoutData = gridData
        table.headerVisible = true
        table.linesVisible = true
        column(this, SWT.LEAD) {
            labelProvider = ColumnLabelProvider.createTextProvider { (it as File).name }
            column.text = "Name"
        }
        column(this, SWT.LEAD) {
            labelProvider = object : ColumnLabelProvider() {
                override fun getText(any: Any): String {
                    val name = (any as File).name
                    return if (root != null && regex.pattern.isNotEmpty())
                        regex.replace(name, replacement)
                    else name
                }

                override fun getBackground(any: Any): Color? {
                    val name = (any as File).name
                    return when {
                        root == null -> null
                        regex.pattern.isEmpty() -> null
                        regex.replace(name, replacement) == name -> null
                        else -> Color(Display.getCurrent(), RGB(255, 255, 0))
                    }
                }
            }
            column.text = "Candidate name"
        }
        contentProvider = FileModel()
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onPathModelUpdated(event: PathModelUpdatedEvent) {
        root = File(event.path)
        update()
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onPatternUpdated(event: PatternUpdatedEvent) {
        try {
            regex = event.pattern.toRegex()
            refresh()
        } catch (e: PatternSyntaxException) {
            // NOTHING TO DO
        }
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onReplacementUpdated(event: ReplacementUpdatedEvent) {
        replacement = event.replacement
        refresh()
    }

    @Subscribe
    @Suppress("UNUSED")
    fun onRenamed(@Suppress("UNUSED_PARAMETER") event: RenamedEvent) {
        update()
    }

    private fun update() {
        doClearAll()
        input = root.children()
            ?.sortedBy { it.name }
            ?: emptyList<File>()
        packColumns()
    }

    private fun packColumns() {
        val availableWidth = table.clientArea.width
        table.columns.forEach {
            it.width = availableWidth / 2
        }
    }

    private fun column(
        table: TableViewer,
        style: Int = SWT.LEAD,
        init: TableViewerColumn.() -> Unit
    ) = TableViewerColumn(table, style).apply(init)
}