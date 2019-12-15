/*
 * Copyright (C) 2014 Valerio Bozzolan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package anupam.acrylic;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

class GraphicsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(
    			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressWarnings("unused")
	@Override
    public void setContentView(View view) {
        if (false) { // set to true to test Picture
            ViewGroup vg = new PictureLayout(this);
            vg.addView(view);
            view = vg;
        }
        super.setContentView(view);
    }
}

