package com.brett.mp3tag;

import java.io.File;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

public class TagDisplayer {

	public void displayFile(String fileName) {
		File testFile = new File(fileName);

		AudioFile af = null;
		try {
			af = AudioFileIO.read(testFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(fileName);
		Tag tag = af.getTag();
		showFields(tag);
	}

	public void showFields(Tag tag ) {

		System.out.println("      artist:\t" + tag.getFirst(FieldKey.ARTIST));
		System.out.println("       title:\t" + tag.getFirst(FieldKey.TITLE));
		System.out.println("       album:\t" + tag.getFirst(FieldKey.ALBUM));
		System.out.println("     comment:\t" + tag.getFirst(FieldKey.COMMENT));
		System.out.println("        year:\t" + tag.getFirst(FieldKey.YEAR));
		System.out.println("track number:\t" + tag.getFirst(FieldKey.TRACK));
		System.out.println(" disc number:\t" + tag.getFirst(FieldKey.DISC_NO));
		System.out.println("       genre:\t" + tag.getFirst(FieldKey.GENRE));
		System.out.println("     artwork:");
		List<Artwork> list = tag.getArtworkList();
		for (Artwork art: list) {
			System.out.println("\t\t"+ art);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TagDisplayer td = new TagDisplayer();
		for (String arg: args) {
			td.displayFile(arg);
		}
	}

}
