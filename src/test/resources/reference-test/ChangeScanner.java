package net.sf.jabref.collab;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import net.sf.jabref.*;
import net.sf.jabref.groups.*;
import net.sf.jabref.imports.*;


public class ChangeScanner extends Thread {
    
    
    /**
     * Finds the entry in neu best fitting the specified entry in old. If no entries get a score
     * above zero, an entry is still returned.
     * @param old EntrySorter
     * @param neu EntrySorter
     * @param index int
     * @return BibtexEntry
     */
    private BibtexEntry bestFit(EntrySorter old, EntrySorter neu, int index) {
        double comp = -1;
        int found = 0;
        loop: for (int i=0; i<neu.getEntryCount(); i++) {
            double res = Util.compareEntriesStrictly(old.getEntryAt(index),
            neu.getEntryAt(i));
            if (res > comp) {
                comp = res;
                found = i;
            }
            if (comp > 1)
                break loop;
        }
        return neu.getEntryAt(found);
    }
    
}
