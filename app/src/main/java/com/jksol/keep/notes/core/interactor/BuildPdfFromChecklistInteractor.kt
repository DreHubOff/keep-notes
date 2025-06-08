package com.jksol.keep.notes.core.interactor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.print.PrintAttributes
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.FileManagerRepository
import com.jksol.keep.notes.di.qualifier.BulletPointSymbol
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class BuildPdfFromChecklistInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    @BulletPointSymbol private val bulletPointSymbol: String,
    private val checklistRepository: ChecklistRepository,
    private val fileManagerRepository: FileManagerRepository,
) {

    suspend operator fun invoke(checklistId: Long): File? {
        val checklist = checklistRepository.getChecklistById(checklistId) ?: return null
        val pdfFile = fileManagerRepository.createSharableFile(buildPdfName(checklist))

        val document = SimplyPdf
            .with(context, pdfFile)
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A4)
            .margin(Margin.default)
            .firstPageBackgroundColor(Color.WHITE)
            .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
            .build()

        val titleStyle = TextProperties().apply {
            textSize = 16
            textColor = "#000000"
            typeface = Typeface.DEFAULT_BOLD
        }
        document.text.write(
            text = checklist.title,
            properties = titleStyle,
        )

        val uncheckedItemStyle = TextProperties().apply {
            this.bulletSymbol = this@BuildPdfFromChecklistInteractor.bulletPointSymbol
            textSize = 12
            textColor = "#000000"
            typeface = Typeface.DEFAULT
        }
        val checkedItems = mutableListOf<ChecklistItem>()
        checklist
            .items
            .sortedBy { it.listPosition }
            .forEach { item ->
                if (item.isChecked) {
                    checkedItems += item
                    return@forEach
                }
                document.text.write(text = item.title, properties = uncheckedItemStyle)
            }

        val checkedItemStyle = TextProperties().apply {
            this.bulletSymbol = this@BuildPdfFromChecklistInteractor.bulletPointSymbol
            strikethrough = true
            textSize = 12
            textColor = "#000000"
            typeface = Typeface.DEFAULT
        }
        checkedItems.forEach { item ->
            document.text.write(text = item.title, properties = checkedItemStyle)
        }

        document.finish()
        return pdfFile
    }

    private fun buildPdfName(checklist: Checklist): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        return "Checklist_${formatter.format(checklist.creationDate)}.pdf"
    }
}