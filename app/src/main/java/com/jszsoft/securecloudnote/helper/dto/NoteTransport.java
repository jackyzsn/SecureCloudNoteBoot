package com.jszsoft.securecloudnote.helper.dto;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class NoteTransport implements Parcelable {

    private String id;

    private String noteId;

    private String noteUserId;

    private String noteTimeTag;

    private String noteContent;

    public NoteTransport(String id, String noteId, String noteUserId, String noteTimeTag, String noteContent){
        this.id = id;
        this.noteId = noteId;
        this.noteUserId = noteUserId;
        this.noteTimeTag = noteTimeTag;
        this.noteContent = noteContent;
    }

    protected NoteTransport(Parcel in) {
        id = in.readString();
        noteId = in.readString();
        noteUserId = in.readString();
        noteTimeTag = in.readString();
        noteContent = in.readString();
    }

    public static final Creator<NoteTransport> CREATOR = new Creator<NoteTransport>() {
        @Override
        public NoteTransport createFromParcel(Parcel in) {
            return new NoteTransport(in);
        }

        @Override
        public NoteTransport[] newArray(int size) {
            return new NoteTransport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(noteId);
        parcel.writeString(noteUserId);
        parcel.writeString(noteTimeTag);
        parcel.writeString(noteContent);
    }
}
