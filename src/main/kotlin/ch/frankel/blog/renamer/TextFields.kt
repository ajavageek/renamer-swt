package ch.frankel.blog.renamer

import org.eclipse.swt.SWT.*
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class FiringTextFieldWrapper(gridData: GridData, parent: Composite, eventBus: EventBus) :
    Text(parent, SINGLE or LEFT or BORDER) {

    abstract fun createEvent(event: ModifyEvent): Any

    init {
        layoutData = gridData
        this.addModifyListener {
            eventBus.post(createEvent(it))
        }
    }

    override fun checkSubclass() {}
}

class DirectoryTextField(gridData: GridData, parent: Composite, eventBus: EventBus) : FiringTextFieldWrapper(gridData, parent, eventBus) {

    init {
        eventBus.register(this)
    }

    override fun createEvent(event: ModifyEvent) = DirectoryPathUpdatedEvent((event.source as Text).text)

    @Subscribe
    @Suppress("UNUSED")
    fun onDirectoryChosen(event: DirectoryChosenEvent) {
        text = event.path
    }
}

class ReplacementTextField(gridData: GridData, parent: Composite, eventBus: EventBus) : FiringTextFieldWrapper(gridData, parent, eventBus) {
    override fun createEvent(event: ModifyEvent) = ReplacementUpdatedEvent((event.source as Text).text)
}

class PatternTextField(gridData: GridData, parent: Composite, eventBus: EventBus) : FiringTextFieldWrapper(gridData, parent, eventBus) {
    override fun createEvent(event: ModifyEvent) = PatternUpdatedEvent((event.source as Text).text)
}