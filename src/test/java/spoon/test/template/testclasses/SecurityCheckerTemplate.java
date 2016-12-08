/*
 * Copyright (C) 2006-2016 INRIA and contributors
 *  Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and abiding by the rules of distribution of free software. You can use, modify and/or redistribute the software under the terms of the CeCILL-C license as circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.test.template.testclasses;

import spoon.reflect.code.CtLiteral;
import spoon.template.TemplateParameter;

/**
 * Created by urli on 08/12/2016.
 */
public class SecurityCheckerTemplate {
    public TemplateParameter<ContextHelper> _ctx_;
    public CtLiteral<String> _p_;

    public void matcher1() {
        if (!_ctx_.S().hasPermission(_p_.S())) {
            throw new SecurityException();
        }
    }
}
