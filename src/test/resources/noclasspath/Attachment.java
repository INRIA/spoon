/*******************************************************************************
 * Copyright 2014 Federico Iosue (federico.iosue@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package it.feio.android.omninotes.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Attachment extends it.feio.android.omninotes	.commons.models.Attachment implements Parcelable {
	
	private Uri uri;


	public Attachment(Uri uri, String mime_type) {
		super();
		setUri(uri);
		setMime_type(mime_type);
	}


	public Attachment(int id, Uri uri, String name, int size, long length, String mime_type) {
		super();
		setId(id);
		setUri(uri);
		setName(name);
		setSize(size);
		setLength(length);
		setMime_type(mime_type);
	}


	private Attachment(Parcel in) {
		setId(in.readInt());
		setUri(Uri.parse(in.readString()));
		setMime_type(in.readString());
	}


	public Uri getUri() {
		return uri;
	}


	public void setUri(Uri uri) {
		this.uri = uri;
		setUriPath(uri.getPath());
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId());
		parcel.writeString(getUri().toString());
		parcel.writeString(getMime_type());
	}

	/*
	 * Parcelable interface must also have a static field called CREATOR, which is an object implementing the
	 * Parcelable.Creator interface. Used to un-marshal or de-serialize object from Parcel.
	 */
	public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {

		public Attachment createFromParcel(Parcel in) {
			return new Attachment(in);
		}


		public Attachment[] newArray(int size) {
			return new Attachment[size];
		}
	};
}
