/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: QueryDialog.java,v 1.1 2004/11/26 01:51:15 tanderson Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */

package org.exolab.jms.tools.admin;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * A generic query dialaog, used to confirm all add/delete operations.
 * The class is just a wrapper around a JOptionMenuPane .
 *
 *
 * @version     $Revision: 1.1 $ $Date: 2004/11/26 01:51:15 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         AdminMgr
 */
public class QueryDialog {

    // The form parent this dialog belongs to
    private JFrame parent_;

    // The command status used to shutdown this window.
    private int status_;

    // The one and only instance of this object.
    static private QueryDialog instance_;

    /**
     * Creates new form QueryDialog
     *
     * @param parent The form dialog
     *
     */
    public QueryDialog(JFrame parent) {
        parent_ = parent;
    }

    /**
     * Get the one and only instance of this dialog. The dialog must first
     * have been created with the create call below.
     *
     * @return QueryDialog the one and only instance
     *
     */
    public static QueryDialog instance() {
        return instance_;
    }


    /**
     * Create the one and only instance of the Query Dialog.
     *
     * @param parent the parent of this dialog
     * @return QueryDialog the one and only instance
     *
     */
    public static QueryDialog create(JFrame parent) {
        if (instance_ == null) {
            instance_ = new QueryDialog(parent);
        }
        return instance_;
    }

    /**
     * Popup the dialog and wait for the user to either OK or Cancel. Display
     * the give string.
     *
     * @param st The string to display.
     *
     */
    public void display(String st) {
        status_ = JOptionPane.showConfirmDialog
            (parent_, st, "Confirm Deletion", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

    }

    /**
     * Will return true if the action was confirmed. i.e. the OK button was
     * pressed.
     *
     * @return boolean the action was confirmed.
     *
     */
    public boolean isConfirmed() {
        return status_ == JOptionPane.YES_OPTION;
    }

} // End QueryDialog

