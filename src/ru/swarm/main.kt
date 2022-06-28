package ru.swarm

import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import java.io.FileWriter
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.ScrollPaneConstants

var window = JFrame("Yntegrator")
var toolkit: Toolkit = Toolkit.getDefaultToolkit()
var panel = JPanel()
var process = JButton("process")
var mode = JComboBox<String>(arrayOf("home", "opt", "usr"))
//var accessMode = JComboBox<String>(arrayOf("current", "root", "everyone"))
var accessMode = JComboBox<String>(arrayOf("disabled function"))
var path = JTextField(15)
var binaries = JList<String>()
var split = JScrollPane(binaries)
var eraseOriginal = JCheckBox("erase original directory")
var linkOnly = JCheckBox("create only links")
var add = JButton("add")
var delete = JButton("delete")
var edit = JButton("edit")
var width = 300
var height = 400
var xPos = toolkit.screenSize.width/2-width/2
var yPos = toolkit.screenSize.height/2-height/2
var bins = ArrayList<String>()

class DialogWindowActions(val dialog: JDialog) : WindowListener {
    var closed = false
    override fun windowOpened(e: WindowEvent?) {

    }

    override fun windowClosing(e: WindowEvent?) {
        //dialog.isAlwaysOnTop = false
        closed = true
        dialog.isVisible = false
        window.isFocusable = true
        window.isVisible = true

    }

    override fun windowClosed(e: WindowEvent?) {
        //dialog.isVisible = false
        //dialog.isAlwaysOnTop = false
        closed = true
        dialog.isVisible = false
        window.isFocusable = true
        window.isVisible = true
    }

    override fun windowIconified(e: WindowEvent?) {
        if(!closed) dialog.isVisible = true
    }

    override fun windowDeiconified(e: WindowEvent?) {
    }

    override fun windowActivated(e: WindowEvent?) {
    }

    override fun windowDeactivated(e: WindowEvent?) {
        if(!closed) dialog.isVisible = true
    }

}

class AddDialog() : JDialog(){
    var ok = JButton("ok")
    var cancel = JButton("cancel")
    var field = JTextField(15)
    var panel = JPanel()
    private val da = DialogWindowActions(this)
    init {
        title = "add executable file"
        window.isFocusable = false
        this.setLocation(xPos+50, yPos+100)
        this.setSize(200, 100)
        this.add(panel)
        panel.add(field)
        panel.add(cancel)
        panel.add(ok)
        field.toolTipText = "relative path from chose directory"
        ok.addActionListener(Ok(da, field))
        cancel.addActionListener(Cancel(da))
        this.isAlwaysOnTop = true
        this.isVisible = true
        this.isResizable = false
        this.addWindowListener(da)
    }

    class Cancel(var da: DialogWindowActions) : AbstractAction() {override fun actionPerformed(e: ActionEvent?) {
        da.closed = true
        da.dialog.isVisible = false
        window.isFocusable = true
        window.isVisible = true

    }}
    class Ok(var da: DialogWindowActions, val field: JTextField) : AbstractAction() {override fun actionPerformed(e: ActionEvent?) {
        bins.add(field.text)
        var out = arrayOfNulls<String>(bins.size)
        binaries.setListData(bins.toArray(out))
        field.text = ""
        da.closed = true
        da.dialog.isVisible = false
        window.isFocusable = true
        window.isVisible = true
    }}

}

class EditDialog() : JDialog(){
    var ok = JButton("ok")
    var cancel = JButton("cancel")
    var field = JTextField(15)
    var panel = JPanel()
    private val da = DialogWindowActions(this)
    init {
        title = "edit file path"
        window.isFocusable = false
        this.setLocation(xPos+50, yPos+100)
        this.setSize(200, 100)
        this.add(panel)
        panel.add(field)
        panel.add(cancel)
        panel.add(ok)
        field.text = bins[binaries.selectedIndex]
        field.toolTipText = "relative path from chose directory"
        ok.addActionListener(Ok(da, field))
        cancel.addActionListener(Cancel(da))
        this.isAlwaysOnTop = true
        this.isVisible = true
        this.isResizable = false
        this.addWindowListener(da)
    }

    class Cancel(var da: DialogWindowActions) : AbstractAction() {override fun actionPerformed(e: ActionEvent?) {
        da.closed = true
        da.dialog.isVisible = false
        window.isFocusable = true
        window.isVisible = true

    }}
    class Ok(var da: DialogWindowActions, private val field: JTextField) : AbstractAction() {override fun actionPerformed(e: ActionEvent?) {
        bins[binaries.selectedIndex] = field.text
        var out = arrayOfNulls<String>(bins.size)
        binaries.setListData(bins.toArray(out))
        field.text = ""
        da.closed = true
        da.dialog.isVisible = false
        window.isFocusable = true
        window.isVisible = true
    }}

}

class Add : AbstractAction() {
    override fun actionPerformed(e: ActionEvent?) {
        AddDialog()
    }

}

class Delete : AbstractAction() {
    override fun actionPerformed(e: ActionEvent?) {
        bins.removeAt(binaries.selectedIndex)
        var out = arrayOfNulls<String>(bins.size)
        binaries.setListData(bins.toArray(out))
    }

}

class Edit : AbstractAction() {
    override fun actionPerformed(e: ActionEvent?) {
        EditDialog()
    }

}

class Process : AbstractAction() {

    private fun delete(file: File) {
        println(11)

        if (file.isFile) {
            file.delete()
        } else {
            for (name in file.list()) {
                if (File(name).isFile) {
                    File(name).delete()
                } else {
                    delete(File(file.path+"/"+file.name+"/"+name))
                    File(name).delete()
                }
            }
        }
    }

    private fun copy(source: File, location: File?) {
        println(10)
        println(location)
        println(source)
        Runtime.getRuntime().exec("cp -rf '$source' '$location'")
    }

    private fun link(source: File, location: File) {
        println(9)

        println(location)
        println(source)
        Runtime.getRuntime().exec("ln -rf '$source' '$location'")
    }

    private fun script(source: File, location: File) {
        println(8)

        //Runtime.getRuntime().exec("mkdir bin sbin")
        //Runtime.getRuntime().exec("touch $location")
        println(location)
        println(source)
            //location.mkdirs()
            //location.mkdir()
            var result = location.createNewFile()
            var target = File(location.absolutePath.split(".")[0])
            println(target)
        if (!result) {
                location.mkdirs()

                target.createNewFile()
            }
        //Runtime.getRuntime().exec("touch $location")
        var out = FileWriter(target)
        out.write("#!/bin/bash\n")
        var segments = source.toString().split("/");
        out.write(location().toString()+"/"+segments[segments.size-2]+"/"+segments[segments.size-1])//source.toString())
        out.flush()
        out.close()
    }

    private fun binLocation() : File? {
        println(7)

        return when (mode.selectedIndex) {
            0 -> {
                return File("/home/" + System.getProperty("user.name") + "/bin")
            }
            1 -> {
                return File("/bin")
            }
            2 -> {
                return File("/usr/bin")
            }
            else -> {return null}
        }
    }
    //var mode = JComboBox<String>(arrayOf("home", "opt", "usr", "var"))

    private fun location() : File? {
        println(6)

        when(mode.selectedIndex) {
            0 -> {
                return File("/home/" + System.getProperty("user.name") + "/sbin")
            }
            1 -> {
                return File("/opt")
            }
            2 -> {
                return File("/usr/sbin")
            }
        }
        return null
    }

    override fun actionPerformed(e: ActionEvent?) {

        //var executablesLocations = ArrayList<String>() frozen forever, maybe
        println(1)
        if(linkOnly.isSelected) {
            link(File(path.text), binLocation()!!)
            for (name in bins) {
                var file = File(path.text+"/"+name)
                println(file)
                //file.mkdirs()
                //file.mkdir()
                //file.createNewFile()
                if (file.isFile) {
                    println(5)

                    link(File(path.text+name), File(binLocation().toString()+"/"+name))
                } else {
                    script(File(path.text+name), File(binLocation().toString()+"/"+name))
                }
            }
            println(2)

        } else {
            copy(File(path.text), location())
            for (name in bins) {
                var file = File(name)
                if (file.isFile) {
                    println(4)

                    link(File(path.text+name), File(binLocation().toString()+"/"+name))
                } else {
                    script(File(path.text+name), File(binLocation().toString()+"/"+name))
                }
            }
            if(eraseOriginal.isSelected) {
                delete(File(path.name))
            }
        }
        println(3)


        /*
        actually that function will never be realized, because I am lazy ass ;)
        when(accessMode.selectedIndex) {
            0 -> {

            }
            1 -> {

            }
            2 -> {

            }
        }*/

        path.text = ""
        bins = java.util.ArrayList<String>()
        var out = arrayOfNulls<String>(bins.size)
        binaries.setListData(bins.toArray(out))
    }

}


fun main(ags: Array<String>) {
    window.setSize(width, height)
    window.setLocation(xPos, yPos)
    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    window.add(panel)
    panel.add(mode)
    panel.add(accessMode)
    panel.add(path)
    panel.add(process)
    panel.add(eraseOriginal)
    panel.add(linkOnly)
    panel.add(split)
    panel.add(add)
    panel.add(edit)
    panel.add(delete)
    add.addActionListener(Add())
    edit.addActionListener(Edit())
    delete.addActionListener(Delete())
    process.addActionListener(ru.swarm.Process())

    panel.toolTipText = "that is application created to easy add any applications\n into your linux distribution, enjoy XD"
    mode.toolTipText = "here you must select mode of application integration,\n some of modes requiring to be launched by root"
    accessMode.toolTipText = "here you selecting who must have access to your apps"
    path.toolTipText = "directory of your app/apps,\n it will be copied to the new location if other options not selected"
    process.toolTipText = "press to bring changes into reality"
    eraseOriginal.toolTipText = "original location with everything inside will be erased\n if other options not selected"
    linkOnly.toolTipText = "don't move/copy/erase source files,\n creating only links or scripts"
    split.toolTipText = "list of binaries to be added to the system,\n" +
            " if you store only path, it will create link, if something another,\n" +
            " it will create script, that is the useful property for '.jar's as example"
    binaries.toolTipText =  "list of binaries to be added to the system,\n" +
            " if you store only path, it will create link, if something another,\n" +
            " it will create script, that is the useful property for '.jar's as example"
    add.toolTipText = "press to open the dialog of adding new executable file\n" +
            "and you can store here one line script"
    delete.toolTipText = "selected binary will be canceled from the list"
    edit.toolTipText = "selected binary from the list will be opened in the edition dialog"
    split.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
    split.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    split.size = Dimension(width-50, height/2)
    split.preferredSize = Dimension(width-50, height/2)
    split.minimumSize = Dimension(width-50, height/2)
    split.maximumSize = Dimension(width-50, height/2)
    var out = arrayOfNulls<String>(bins.size)
    binaries.setListData(bins.toArray(out))
    window.isVisible = true
    window.isResizable = false
}