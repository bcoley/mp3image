package com.brett.mp3tag;

import java.util.ArrayList;
import java.util.List;

public class AddImageMain {

    AddImage ia = new AddImage();

    public boolean isOpt(String arg) {
        if (arg.startsWith("-")) {
            return true;
        }
        return false;
    }

    public boolean hasArgList(String opt) {
        if ("-t".equals(opt)) {
            return true;
        }
        if ("-f".equals(opt)) {
            return true;
        }
        if ("-a".equals(opt)) {
            return true;
        }
        if ("-i".equals(opt)) {
            return true;
        }
        return false;
    }

    public void process(String[] args) {
        int i = 0;
        List<String> argList = new ArrayList<String>();
        while (i < args.length) {
            if (isOpt(args[i])) {
                String opt = args[i];
                i++;
                if (hasArgList(opt)) {
                    argList = new ArrayList<String>();
                    while((i < args.length) && !isOpt(args[i])) {
                        argList.add(args[i]);
                        i++;
                    }
                }
                processArgs(opt, argList);
                argList = new ArrayList<String>();
            }
            else { // must be an mp3 file name
                processArg(args[i]);
                i++;
            }
        }
    }

    private void processArg(String string) {
        System.out.println("processArg " + string);
        ia.addMp3File(string);

    }

    private void processArgs(String opt, List<String> argList) {
        System.out.println("processArgs opt = " + opt);
        for (String arg: argList) {
            System.out.println("  arglist = " + arg);
        }
        if ("-v".equals(opt)) {
            ia.setVerbose(true);
        }
        else if ("-ut".equals(opt)) {
            ia.setUseTerms(true);
        }
        else if ("-n".equals(opt)) {
            ia.setResize(false);
        }
        else if ("-f".equals(opt)) {
            processMp3Files(argList);
        }
        else if ("-a".equals(opt)) {
            processArtworkEntries(argList);
        }
        else if ("-t".equals(opt)) {
            processSearchTerms(argList);
        }
        else if ("-i".equals(opt)) {
            processNumberOfPics(argList);
        }
        else {
            System.err.println("Unrecognized option " + opt);
            showUsage();
            System.exit(1);
        }

    }

    private void showUsage() {
        System.out.println("Addimage - a program to add artwork to mp3 files.\n");
        System.out.println("\tFor example, given an mp3 file, \"Nirvana - All Apologies.mp3\",");
        System.out.println("\tthe default action is then to query the internet for nirvana+all+apologies");
        System.out.println("\tand update the mp3 file's artwork from an image found there.\n");
        System.out.println("Parameters:");
        System.out.println("[-f] <list of mp3 files>\n\tIf other options are specified, use -f to identify the mp3 files.\n");
        System.out.println("Optional parms:\n");
        System.out.println("-i <number of images to add to each mp3 file>");
        System.out.println("-t <terms to add to search>");
        System.out.println("\tFor example, if your mp3 files are named by song title,\n\tuse this parm to specify artist or to otherwise control the search.\n");
        System.out.println("-ut\n\tUse terms only, that is, do not search by mp3 file titles.\n\tIf this option is specified, -t is required.\n ");
        System.out.println("-a <list of artwork files or urls for artwork>\n\tIf these do not match the number of input files,");
        System.out.println("\tthey will be reused as needed.\n");
        System.out.println("-v\n\tVerbose mode.  Display messages about processing details.\n");
        System.out.println("-n\n\tDon't resize images.  The default is to scale to 640x480 preserving aspect ratio.\n");
    }

    private void processSearchTerms(List<String> argList) {
        ia.addSpecificTerms(argList);

    }

    private void processArtworkEntries(List<String> argList) {
        ia.addSpecificArt(argList);
    }

    private void processMp3Files(List<String> argList) {
        ia.addMp3Files(argList);

    }

    private void processNumberOfPics(List<String> argList) {
        ia.setNumberOfPics(argList);

    }

    public static void main(String[] args) {

        AddImageMain aiMain = new AddImageMain();

        if (args.length == 0) {
            aiMain.showUsage();
            System.exit(1);
        }
        aiMain.process(args);
        aiMain.ia.execute();
    }

}
