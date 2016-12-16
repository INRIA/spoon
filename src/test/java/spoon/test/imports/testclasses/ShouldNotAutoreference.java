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

package spoon.test.imports.testclasses;

import java.nio.file.LinkOption;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by urli on 16/12/2016.
 */
public class ShouldNotAutoreference {

    private String toto;
    public static final Collection<LinkOption> NOFOLLOW_LINKS = Arrays.asList(new LinkOption[] {LinkOption.NOFOLLOW_LINKS});
}
