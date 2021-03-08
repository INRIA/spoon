#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
    This is a simple GUI app for spoon-smpl.
"""

import sys
import time
import datetime
import subprocess
import os
import re
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
from PyQt5 import QtCore
from PyQt5.QtCore import Qt

class Main(QMainWindow):
 
    def __init__(self):
        super().__init__()
        
        self.runmodes = ["check", "checksub", "rewrite", "compile", "patch", "ctl", "gentest"]
        self.current_mode = "patch"
        self.is_switching_mode = False
        
        self.initUI()
        
        self.timer = QtCore.QTimer()
        self.timer.timeout.connect(self.on_timer)
        self.timer.start(100)
        
        self.queued_shell_command = None
        self.queued_shell_command_timeout = 0
    
    def shell_exec(self, cmdstr):
        then = time.time()
        result = subprocess.check_output(cmdstr, stderr=subprocess.STDOUT, shell=True)
        elapsed = time.time() - then
        return (result, elapsed)
    
    def on_timer(self):
        if self.queued_shell_command is not None and self.queued_shell_command_timeout <= time.time():
            output, elapsed = self.shell_exec(self.queued_shell_command)
            self.output_text.setPlainText(self.queued_shell_command + "\n\n" + output.decode("utf-8") + "\n\nTime: {:.3f}".format(elapsed))
            self.queued_shell_command = None
    
    def wrap_layout(self, x):
        wid = QWidget(self)
        wid.setLayout(x)
        x.setContentsMargins(0,0,0,0)
        return wid
    
    def initUI(self):
        self.box_left = QHBoxLayout()
        self.box_main = QVBoxLayout()
        
        self.smpl_text = QTextEdit()
        self.smpl_text.setStyleSheet("font: normal 12pt monospace")
        self.smpl_text.setTabStopWidth(12)
        self.smpl_text.keyPressEvent
        
        self.java_text = QTextEdit()
        self.java_text.setStyleSheet("font: normal 12pt monospace")
        self.java_text.setTabStopWidth(12)
        
        if os.path.exists("/tmp/smplgui_py_smpl_text.cocci"):
            self.smpl_text.setPlainText(open("/tmp/smplgui_py_smpl_text.cocci").read()[0:-1])
        
        if os.path.exists("/tmp/smplgui_py_java_text.java"):
            self.java_text.setPlainText(open("/tmp/smplgui_py_java_text.java").read()[0:-1])
        
        self.output_text = QTextEdit()
        self.output_text.setStyleSheet("font: normal 12pt monospace")
        self.output_text.setTabStopWidth(12)
        
        self.output_text.setPlainText("SmPL to the left, Java to the right.\nF5 to execute.\nF6 to select mode.")
        
        self.box_left.addWidget(self.smpl_text)
        self.box_left.addWidget(self.java_text)
        
        self.box_main.addWidget(self.wrap_layout(self.box_left))
        self.box_main.addWidget(self.output_text)
        
        self.setCentralWidget(self.wrap_layout(self.box_main))
        self.setGeometry(100,100,1000,600)
        self.setWindowTitle("smplgui")
        self.installEventFilter(self)
        self.show()
    
    def eventFilter(self, obj, event):
        if type(event) != QKeyEvent or event.type() != 6:
            return False
        
        if self.is_switching_mode and event.key() >= 16777264 and event.key() <= (16777264 + len(self.runmodes)):
            self.is_switching_mode = False
            
            self.smpl_text.setDisabled(False)
            self.java_text.setDisabled(False)
            
            self.current_mode = self.runmodes[event.key() - 16777264]
            self.output_text.setPlainText("Mode set to {}.\nF5 to execute.".format(self.current_mode))
            return True
        elif event.key() == 16777268:
            if (self.current_mode == "gentest"):
                self.generate_test()
            else:
                self.save_and_run(self.current_mode)
            
            return True
        elif event.key() == 16777269:
            self.is_switching_mode = True
            
            self.smpl_text.setDisabled(True)
            self.java_text.setDisabled(True)
            
            instructions = list()
            instructions.append("Choose mode by pressing the corresponding key:")
            
            for i in range(len(self.runmodes)):
                instructions.append("F{}: {}".format(i+1, self.runmodes[i]))
            
            self.output_text.setPlainText("\n".join(instructions))
            return True
        else:
            return False
    
    def save_and_run(self, action, run_now=False):
        if self.queued_shell_command is None:
            smpl_file = open("/tmp/smplgui_py_smpl_text.cocci", "w+")
            smpl_file.write(self.smpl_text.toPlainText() + "\n")
            smpl_file.close()
            
            java_file = open("/tmp/smplgui_py_java_text.java", "w+")
            java_file.write(self.java_text.toPlainText() + "\n")
            java_file.close()
            
            cmdstr = "./tools/smplcli.sh " + action + " --smpl-file /tmp/smplgui_py_smpl_text.cocci --java-file /tmp/smplgui_py_java_text.java; exit 0"
            
            if run_now:
                output, elapsed = self.shell_exec(cmdstr)
                self.output_text.setPlainText(cmdstr + "\n\n" + output.decode("utf-8") + "\n\nTime: {:.3f}".format(elapsed))
            else:
                self.queued_shell_command = cmdstr
                self.queued_shell_command_timeout = time.time() + 0.1
                self.output_text.setPlainText("")
    
    def generate_test(self):
        self.save_and_run("patch", True)
        output = re.sub(r"(?s).+?\n(?=class)", "", self.output_text.toPlainText())
        
        self.output_text.setPlainText("[name]\n\n[contract]\n\n[patch]\n" + self.smpl_text.toPlainText() + "\n\n[input]\n" + self.java_text.toPlainText() + "\n\n[expected]\n" + output)

def main():
    app = QApplication(sys.argv)
    main = Main()
    main.show()
 
    sys.exit(app.exec_())
 
if __name__ == "__main__":
    main()
