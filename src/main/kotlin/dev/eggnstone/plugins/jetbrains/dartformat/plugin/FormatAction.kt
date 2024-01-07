package dev.eggnstone.plugins.jetbrains.dartformat.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import dev.eggnstone.plugins.jetbrains.dartformat.Constants
import dev.eggnstone.plugins.jetbrains.dartformat.DartFormatException
import dev.eggnstone.plugins.jetbrains.dartformat.FailType
import dev.eggnstone.plugins.jetbrains.dartformat.tools.JsonTools
import dev.eggnstone.plugins.jetbrains.dartformat.config.DartFormatConfig
import dev.eggnstone.plugins.jetbrains.dartformat.config.DartFormatPersistentStateComponent
import dev.eggnstone.plugins.jetbrains.dartformat.tools.Logger
import dev.eggnstone.plugins.jetbrains.dartformat.tools.NotificationTools
import dev.eggnstone.plugins.jetbrains.dartformat.tools.OsTools
import dev.eggnstone.plugins.jetbrains.dartformat.tools.PluginTools
import java.util.*

class FormatAction : AnAction()
{
    companion object
    {
        private const val DEBUG_FORMAT_ACTION = false
    }

    init
    {
        Logger.log("FormatAction: init")
    }

    override fun actionPerformed(e: AnActionEvent)
    {
        val project = e.getRequiredData(CommonDataKeys.PROJECT)

        val config = getConfig()
        if (config == DartFormatConfig())
        {
            val subtitle = "No formatting option enabled"
            val messages = listOf("Please check File -&gt; Settings -&gt; Other Settings -&gt; DartFormat")
            NotificationTools.notifyWarning(messages, project, subtitle)
            return
        }

        try
        {
            val startTime = Date()

            val finalVirtualFiles = mutableSetOf<VirtualFile>()
            val collectVirtualFilesIterator = CollectVirtualFilesIterator(finalVirtualFiles)
            val selectedVirtualFiles = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY)

            if (DEBUG_FORMAT_ACTION) Logger.log("${selectedVirtualFiles.size} selected files:")
            for (selectedVirtualFile in selectedVirtualFiles)
            {
                if (DEBUG_FORMAT_ACTION) Logger.log("  Selected file: $selectedVirtualFile")
                VfsUtilCore.iterateChildrenRecursively(selectedVirtualFile, this::filterDartFiles, collectVirtualFilesIterator)
            }

            var changedFiles = 0
            if (DEBUG_FORMAT_ACTION) Logger.log("${finalVirtualFiles.size} final files:")
            CommandProcessor.getInstance().runUndoTransparentAction {
                for (finalVirtualFile in finalVirtualFiles)
                {
                    if (DEBUG_FORMAT_ACTION) Logger.log("  Final file: $finalVirtualFile")
                    if (formatDartFile(finalVirtualFile, project))
                        changedFiles++
                }
            }

            val endTime = Date()
            val diffTime = endTime.time - startTime.time
            val diffTimeText = if (diffTime < 1000) "$diffTime ms" else "${diffTime / 1000.0} s"

            var finalVirtualFilesText = "${finalVirtualFiles.size} file"
            if (finalVirtualFiles.size != 1)
                finalVirtualFilesText += "s"

            val changedFilesText: String = when (changedFiles)
            {
                0 -> "Nothing"
                1 -> "1 file"
                else -> "$changedFiles files"
            }

            val lines = mutableListOf<String>()
            lines.add("Formatting $finalVirtualFilesText took $diffTimeText.")
            lines.add("$changedFilesText changed.")
            NotificationTools.notifyInfo(lines, project)
        }
        catch (e: Exception)
        {
            NotificationTools.reportThrowable(e, project)
        }
        catch (e: Error)
        {
            // catch errors, too, in order to report all problems, e.g.:
            // - java.lang.AssertionError: Wrong line separators: '...\r\n...'
            NotificationTools.reportThrowable(e, project)
        }
    }

    private fun filterDartFiles(virtualFile: VirtualFile): Boolean = virtualFile.isDirectory || PluginTools.isDartFile(virtualFile)

    private fun formatDartFile(virtualFile: VirtualFile, project: Project): Boolean
    {
        try
        {
            val fileEditor = FileEditorManager.getInstance(project).getSelectedEditor(virtualFile)
            return if (fileEditor == null)
                formatDartFileByBinaryContent(project, virtualFile)
            else
                formatDartFileByFileEditor(project, fileEditor)
        }
        catch (e: DartFormatException)
        {
            throw e
        }
        catch (e: Exception)
        {
            throw DartFormatException(FailType.ERROR, "${virtualFile.path}\n${e.message}", e)
        }
    }

    private fun formatDartFileByBinaryContent(project: Project, virtualFile: VirtualFile): Boolean
    {
        if (!virtualFile.isWritable)
        {
            if (DEBUG_FORMAT_ACTION)
            {
                Logger.log("formatDartFileByBinaryContent: $virtualFile")
                Logger.log("  !virtualFile.isWritable")
            }
            return false
        }

        val inputBytes = virtualFile.inputStream.readAllBytes()
        val inputText = String(inputBytes)
        val outputText = format(project, inputText)
        if (outputText == inputText)
        {
            if (DEBUG_FORMAT_ACTION) Logger.log("Nothing changed.")
            return false
        }

        val outputBytes = outputText.toByteArray()
        ApplicationManager.getApplication().runWriteAction {
            virtualFile.setBinaryContent(outputBytes)
        }

        if (DEBUG_FORMAT_ACTION) Logger.log("Something changed.")
        return true
    }

    private fun formatDartFileByFileEditor(project: Project, fileEditor: FileEditor): Boolean
    {
        if (fileEditor !is TextEditor)
        {
            if (DEBUG_FORMAT_ACTION)
            {
                Logger.log("formatDartFileByFileEditor: $fileEditor")
                Logger.log("  fileEditor !is TextEditor")
            }
            return false
        }

        val editor = fileEditor.editor

        val document = editor.document
        val inputText = document.text
        val outputText = format(project, inputText)
        if (outputText == inputText)
        {
            if (DEBUG_FORMAT_ACTION) Logger.log("Nothing changed.")
            return false
        }

        val fixedOutputText: String = if (outputText.contains("\r\n"))
        {
            Logger.log("#################################################")
            Logger.log("Why does the outputText contain wrong linebreaks?")
            Logger.log("#################################################")
            outputText.replace("\r\n", "\n")
        }
        else
            outputText

        ApplicationManager.getApplication().runWriteAction {
            document.setText(fixedOutputText)
        }

        if (DEBUG_FORMAT_ACTION) Logger.log("Something changed.")
        return true
    }

    private fun format(project: Project, inputText: String): String
    {
        if (inputText.isEmpty())
            return ""

        //Logger.isEnabled = false

        return ExternalDartFormat.instance.format(project, inputText)

        try
        {
            val config = getConfig()

            val configJson = config.toJson()
            if (DEBUG_FORMAT_ACTION) Logger.log("configJson: $configJson")

            val processBuilder: ProcessBuilder = if (OsTools.isWindows())
                ProcessBuilder("cmd", "/c", "dart_format", "--pipe", "--errors-as-json", "--config=$configJson")
            else
                ProcessBuilder("dart_format", "--pipe", "--errors-as-json", "--config=$configJson")

            val process = processBuilder.start()
            process.outputStream.bufferedWriter().use {
                it.write(inputText)
                it.close()
            }

            val outputBuffer = StringBuffer()
            val outputStream = process.inputStream
            val outputReader = outputStream.bufferedReader()
            val errorBuffer = StringBuffer()
            val errorStream = process.errorStream
            val errorReader = errorStream.bufferedReader()

            var waitedMillis = 0L
            while (waitedMillis < Constants.WAIT_FOR_PROCESS_TO_FINISHED_TIMEOUT_IN_SECONDS * 1000L)
            {
                var readSome = false

                if (outputStream.available() > 0)
                {
                    outputBuffer.append(outputReader.readText())
                    readSome = true
                }

                if (errorStream.available() > 0)
                {
                    errorBuffer.append(errorReader.readText())
                    readSome = true
                }

                if (readSome)
                    continue

                if (process.waitFor(Constants.WAIT_INTERVAL_IN_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS))
                    break

                waitedMillis += Constants.WAIT_INTERVAL_IN_MILLIS
            }

            if (process.isAlive)
                throw DartFormatException(FailType.ERROR, "Timeout after ${Constants.WAIT_FOR_PROCESS_TO_FINISHED_TIMEOUT_IN_SECONDS} seconds waiting for dart_format to finish.")

            if (outputStream.available() > 0)
                outputBuffer.append(outputReader.readText())

            if (errorStream.available() > 0)
                errorBuffer.append(errorReader.readText())

            val errorText = errorBuffer.toString()
            if (errorText.isNotEmpty())
            {
                val d: DartFormatException?

                try
                {
                    d = JsonTools.parseDartFormatException(errorText)
                }
                catch (e: Exception)
                {
                    throw DartFormatException(FailType.ERROR, e.toString(), e)
                }

                if (d == null)
                    throw DartFormatException(FailType.ERROR, "Failed to parse: $errorText")

                throw d
            }

            val formattedText = outputBuffer.toString()
            if (formattedText.isEmpty())
                throw DartFormatException(FailType.ERROR, "No output received.")

            return formattedText
        }
        finally
        {
            Logger.isEnabled = true
        }
    }

    private fun getConfig(): DartFormatConfig
    {
        if (DartFormatPersistentStateComponent.instance == null)
            return DartFormatConfig()

        return DartFormatPersistentStateComponent.instance!!.state
    }
}
