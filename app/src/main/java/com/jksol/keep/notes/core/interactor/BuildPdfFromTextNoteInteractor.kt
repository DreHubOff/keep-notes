package com.jksol.keep.notes.core.interactor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.print.PrintAttributes
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.FileManagerRepository
import com.jksol.keep.notes.data.TextNotesRepository
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class BuildPdfFromTextNoteInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val textNotesRepository: TextNotesRepository,
    private val fileManagerRepository: FileManagerRepository,
) {

    suspend operator fun invoke(noteId: Long): File? {
        val textNote = textNotesRepository.getNoteById(noteId) ?: return null
        val pdfFile = fileManagerRepository.createSharableFile(fileName = buildPdfName(textNote))
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
            text = textNote.title,
            properties = titleStyle,
        )

        val contentStyle = TextProperties().apply {
            textSize = 12
            textColor = "#000000"
            typeface = Typeface.DEFAULT
        }
        document.text.write(
            text = textNote.content,
            properties = contentStyle,
        )

        document.finish()
        return pdfFile
    }

    private fun buildPdfName(textNote: TextNote): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        return "Note_${formatter.format(textNote.creationDate)}.pdf"
    }
}