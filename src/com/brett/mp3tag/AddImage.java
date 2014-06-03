package com.brett.mp3tag;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;

public class AddImage {

	public static final String ADD_IMAGE_TEMP_JPG = "AddImageTemp.jpg";

    Dimension BOUNDARY = new Dimension(640, 480);

	ImageUrlDogpileFetcher urlFetcher;
	
	ImageResizer resizer;

	private boolean resize ;

	private boolean verbose;

	private boolean useTerms;
	
	private List<String> mp3FileNames;
	
	private List<String> artworkNames;
	
	private List<String> searchTerms;
	
	private int numberOfImages = 1;
	
	
	public AddImage() {
		initialize();
	}
	
	public void addMp3File(String string) {
		mp3FileNames.add(string);
	}
	
	public void addMp3Files(List<String> argList) {
		mp3FileNames.addAll(argList);
	}

	public void addSpecificArt(List<String> argList) {
		artworkNames.addAll(argList);
	}

	public void addSpecificTerms(List<String> argList) {
		for (String arg: argList) {
			String newTerm = fixup(arg);
			if (! searchTerms.contains(newTerm)) {
				searchTerms.add(newTerm);
			}
		}
	}

	private List<String> createSearchTerms(String mp3FileName) {
		if (useTerms) {
			return searchTerms;
		}
		List<String> result = new ArrayList<String>();
		String fileName = mp3FileName;
		// split off path info...
		if (mp3FileName.contains(File.separator)) {
			fileName = mp3FileName.substring(1 + mp3FileName.lastIndexOf(File.separator));
		}
		// split off suffix...
		String [] parts = fileName.split(".mp3");
		parts = parts[0].split(" ");
		for (String term : parts) {
			term = fixup(term);
			if (!isFilteredWord(term)) {
				result.add(term);
			}
		}
		if (hasSpecificTerms()) {
			result.addAll(searchTerms);
		}
		if (verbose) {
			System.out.println("Search terms for " + mp3FileName + " = " + result);
		}
		return result;
	}


	public void execute() {
		if (hasSpecificArt()) {
			// set mp3s to specific file or URL
			processFilesBySpecificArt(mp3FileNames, artworkNames);
		}
		else if (useTerms) {
			// set all mp3 to specific artwork from URL from search.
			processAllFilesFromSearchTerms(mp3FileNames, searchTerms);
			
		}
		else {
			// do queries as necessary for earch mp3, adding search terms if used.
			for (String mp3FileName: mp3FileNames) {
				setArtwork(mp3FileName);
			}
		}
		
	}

	private String fixup(String term) {
		String result = term.replaceAll("\\W", "");
		result = result.trim();
		result = result.toLowerCase();
		return result;
	}

	private Artwork getArtworkFromSearchTerms(List<String> terms) {
		
		Artwork result = null;
		List<String> urls = urlFetcher.searchForImageUrls(terms);
		
		for (String url: urls) {
			Artwork artwork = readArtworkFromUrl(url);
			if (artwork != null) {
				result = artwork;
				if (verbose) {
					System.out.println("Found artwork from URL " + url + " for " + terms);
				}
				return result;
			}
		}
		return result;
	}
	
	private BufferedImage getImageFromUrl(String artworkUrl) {
		BufferedImage image = null;
		try {
			Thread.sleep(100);
			URL url = new URL(artworkUrl);
			image = ImageIO.read(url);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public boolean hasSpecificArt() {
		if ((artworkNames == null) || (artworkNames.isEmpty())) {
			return false;
		}
		return true;
	}

	public boolean hasSpecificTerms() {
		if ((searchTerms == null) || (searchTerms.isEmpty())) {
			return false;
		}
		return true;
	}

	public void initialize() {
		
		resize = true;

		verbose = false;

		useTerms = false;
		
		resizer = new ImageResizer();
		
		urlFetcher = new ImageUrlDogpileFetcher();
		
		mp3FileNames = new ArrayList<String>();
		
		artworkNames = new ArrayList<String>();
		
		searchTerms = new ArrayList<String>();
		
	}

	private boolean isFilteredWord(String word) {
		if (null == word)
			return true;
		String comp = word.trim().toLowerCase();
		if ("".equals(comp))
			return true;
		if ("a".equals(comp))
			return true;
		if ("an".equals(comp))
			return true;	
		if ("the".equals(comp))
			return true;
		if ("-".equals(comp))
			return true;
		if ("on".equals(comp))
			return true;
		if ("of".equals(comp))
			return true;
		return false;
	}

	private void processAllFilesFromSearchTerms(List<String> mp3Names, List<String> terms) {
		
		Artwork artwork = getArtworkFromSearchTerms(terms);

		if (artwork != null) {
			for (String mp3FileName: mp3Names) {
				setArtwork(mp3FileName, artwork);
			}
		}
		else {
			System.err.println(" Unable to find artwork for search terms: " + terms);
			System.exit(1);
		}
	}

	private void processFilesBySpecificArt(List<String> mp3Names, List<String> artNames) {
		int artSize = artNames.size();
		int mp3Size = mp3Names.size();
		// if this path is used a lot, then get artwork from name first, and apply to mp3s to avoid
		// duplicate processing when number of art < number of mp3s.
		for (int i = 0; i < mp3Size; i++) {
			int artIndex = i % artSize;
			setArtwork(mp3Names.get(i), artNames.get(artIndex));
		}
	}

	private Artwork readArtworkFromFile(String artworkFileName) {
		Artwork artwork = null;
		File artworkFile = new File(artworkFileName);
		try {
			artwork = new StandardArtwork();
			artwork.setFromFile(artworkFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			artworkFile = null;
		}
		return artwork;
	}


	private Artwork readArtworkFromUrl(String artworkUrl) {
		Artwork artwork = null;
		BufferedImage image = getImageFromUrl(artworkUrl);
		
		if (image == null) {
			return artwork;
		}
		
		if (resize) {
			if (resizer.isResizeNecessary(new Dimension(image.getWidth(), image.getHeight()), BOUNDARY)) {
				image = resizer.resizeImage(image, BOUNDARY);
				if (verbose) {
					System.out.println("Resizing image from " + artworkUrl);
				}
			}
		}
		
		try {
			// TODO: find a way without temp file.
			File tempFile = new File(ADD_IMAGE_TEMP_JPG);
			ImageIO.write(image, "jpg", tempFile);
			artwork = readArtworkFromFile(ADD_IMAGE_TEMP_JPG);
			artwork.setWidth(image.getWidth());
			artwork.setHeight(image.getHeight());
			tempFile = null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return artwork;
	}

	private MP3File readAudioFile(String fileName) {
		File testFile = new File(fileName);

		MP3File af = null;
		try {
			af = new MP3File(testFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return af;
	}


	public void setArtwork(String mp3FileName) {
		
		List<String> terms = createSearchTerms(mp3FileName);
		List<String> urls = urlFetcher.searchForImageUrls(terms);
		
		if (verbose) {
			System.out.println("Found " + urls.size() + " urls for " + mp3FileName);
		}
		
		// when artwork urls are empty here, then try to modify terms to find something...
		// Delete a random term, or delete shortest or longest term?
		// for now, delete the first term and try again.
		
		boolean hopeless = false;
		
		while ((urls.size() == 0) && !hopeless) {
			terms.remove(0);
			if (verbose) {
				System.out.println("Retry search = " + terms);
			}
			urls = urlFetcher.searchForImageUrls(terms);
			if (terms.size() < 2) {
				hopeless = true;
			}
		}
		
		setArtworkFromUrls(mp3FileName, terms, urls);
	}

	private void setArtworkFromUrls(String mp3FileName, List<String> terms, List<String> urls) {
		int count = 0;
		for (String url: urls) {
			Artwork artwork = readArtworkFromUrl(url);
			if (artwork != null) {
				if (verbose) {
					System.out.println("\nSetting " + mp3FileName + "\n\t from url = " + url + "\n\t terms = " + terms);
				}
				boolean success = false;
				if (count == 0) {
					success = setArtwork(mp3FileName, artwork);
				}
				else {
					success = addArtwork(mp3FileName, artwork);
				}
				if (success) {
					count++;
				}
				if (count >= numberOfImages) {
					return;
				}
			}
		}
		if (count == 0) {
			System.err.println("\nNo image set on " + mp3FileName + "\n");
		}
	}

	private boolean setArtwork(String mp3FileName, Artwork artwork) {
		MP3File af = readAudioFile(mp3FileName);
		Tag tag = af.getTag();
		//		ID3v1Tag         v1Tag  =  new ID3v1Tag();
		AbstractID3v2Tag v2tag  =  af.getID3v2Tag();
		ID3v24Tag        v24tag =  af.getID3v2TagAsv24();
		ID3v23Tag        v23tag =  null;

		if (v2tag instanceof ID3v23Tag) {
			v23tag = (ID3v23Tag) v2tag;
		}
		else {
			v23tag =  new ID3v23Tag(v2tag);
		}

		if (tag == null) {
			System.err.println("null list of tags for " + mp3FileName);
			return false;
		}
		List<Artwork> artworkList = tag.getArtworkList();

		if (artworkList.size() > 0) {
			tag.deleteArtworkField();
			v2tag.deleteArtworkField();
			v24tag.deleteArtworkField();
			v23tag.deleteArtworkField();
		}
		try {
			//			tag.addField(artwork);
			//			af.setTag(tag);
			////			af.setID3v1Tag(v1Tag);
			//			v2tag.addField(artwork);
			//			af.setID3v2Tag(v2tag);
			//			
			//			v24tag.addField(artwork);
			//			af.setTag(v24tag);

			v23tag.addField(artwork);
			af.setTag(v23tag);

			AudioFileIO.write(af);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	private boolean addArtwork(String mp3FileName, Artwork artwork) {
		MP3File af = readAudioFile(mp3FileName);
		Tag tag = af.getTag();
		//		ID3v1Tag         v1Tag  =  new ID3v1Tag();
		AbstractID3v2Tag v2tag  =  af.getID3v2Tag();
		ID3v24Tag        v24tag =  af.getID3v2TagAsv24();
		ID3v23Tag        v23tag =  null;

		if (v2tag instanceof ID3v23Tag) {
			v23tag = (ID3v23Tag) v2tag;
		}
		else {
			v23tag =  new ID3v23Tag(v2tag);
		}

		if (tag == null) {
			System.err.println("null list of tags for " + mp3FileName);
			return false;
		}
		List<Artwork> artworkList = tag.getArtworkList();

		try {
			//			tag.addField(artwork);
			//			af.setTag(tag);
			////			af.setID3v1Tag(v1Tag);
			//			v2tag.addField(artwork);
			//			af.setID3v2Tag(v2tag);
			//			
			//			v24tag.addField(artwork);
			//			af.setTag(v24tag);

			v23tag.addField(artwork);
			af.setTag(v23tag);

			AudioFileIO.write(af);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void setArtwork(String mp3FileName, String artworkName) {
		if (artworkName.startsWith("http")) {
			setArtworkFromUrl(mp3FileName, artworkName);
		}
		else {
			setArtworkFromFile(mp3FileName, artworkName);
		}

	}

	private void setArtworkFromFile(String mp3FileName, String artworkFileName) {
		AudioFile af = readAudioFile(mp3FileName);
		Artwork artwork = readArtworkFromFile(artworkFileName);

		Tag tag = af.getTag();
		try {
			tag.addField(artwork);
			AudioFileIO.write(af);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setArtworkFromUrl(String mp3FileName, String artworkUrl) {
		MP3File af = readAudioFile(mp3FileName);
		Artwork artwork = readArtworkFromUrl(artworkUrl);
		Tag tag = af.getTag();
		try {
			tag.addField(artwork);
			af.setID3v1Tag(tag);
			af.setTag(tag);
			AudioFileIO.write(af);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setResize(boolean b) {
		resize = b;
	}

	public void setUseTerms(boolean b) {
		useTerms = b;
	}

	public void setVerbose(boolean b) {
		verbose = b;
	}

	public void setNumberOfPics(List<String> argList) {
		if ((argList != null) && (argList.size() > 0)) {
			this.numberOfImages = Integer.parseInt(argList.get(0));
		}

	}

}
