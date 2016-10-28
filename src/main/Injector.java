package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Injector
{
    private static void copyFile(String pathIn, String pathOut) throws IOException
    {
        File infile = new File(pathIn);
        File outfile = new File(pathOut);

        FileInputStream instream = new FileInputStream(infile);
        FileOutputStream outstream = new FileOutputStream(outfile);

        byte[] buffer = new byte[1024];

        int length;

        while ((length = instream.read(buffer)) > 0)
        {
            outstream.write(buffer, 0, length);
        }

        instream.close();
        outstream.close();
    }

    private void extractZip(String zip) throws IOException
    {
        InputStream is = this.getClass().getResourceAsStream(zip);
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry = zis.getNextEntry();
        entry = zis.getNextEntry();

        String destination = "./temp/";
        byte[] buf = new byte[1024];
        while (entry != null)
        {
            String name = entry.getName();

            int n;
            FileOutputStream fileoutputstream;

            String path = destination + name;
            File newFile = new File(path);
            newFile.getParentFile().mkdirs();
            if (name.charAt(name.length() - 1) == '/')
            {
                zis.closeEntry();
                entry = zis.getNextEntry();
                continue;
            }

            if (!newFile.exists())
            {
                newFile.createNewFile();
            }

            fileoutputstream = new FileOutputStream(path);

            while ((n = zis.read(buf, 0, 1024)) > -1)
                fileoutputstream.write(buf, 0, n);

            fileoutputstream.close();
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
    }

    public void inject(boolean bNew) throws NoHsAppException, NoFiles9FolderException, IOException, InterruptedException, URISyntaxException
    {
        extractZip("Universal-Inject-Generator-master.zip");

        // first, copy hs.app from files9 folder to input folder of inject
        // generator

        //find the files9 folder first
        File f9 = new File("./files9/");
        if(!f9.exists())
        {
            throw new NoFiles9FolderException();
        }
        //make sure it has hs.app in it
        f9 = new File("./files9/hs.app");
        if(!f9.exists())
        {
            throw new NoHsAppException();
        }

        copyFile("./files9/hs.app", "./temp/Universal-Inject-Generator-master/input/hs.app");

        // second, copy the right fbi.cia into the input folder
        String folder = "old-FBI";

        if (bNew)
        {
            folder = "new-FBI";
        }
        copyFile("./temp/Universal-Inject-Generator-master/" + folder + "/fbi.cia",
                "./temp/Universal-Inject-Generator-master/input/fbi.cia");

        // third, run go.bat
        Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "go.bat" }, null,
                new File("./temp/Universal-Inject-Generator-master/"));

        File f = new File("./temp/Universal-Inject-Generator-master/fbi_inject_with_banner.app");
        int counter = 0;
        while (true) // wait till file exists before trying to move it
        {
            counter++;
            if (f.exists() || counter > 200000)
            {
                break;
            }
        }

        // kill cmd window so we can delete temp files
        Runtime.getRuntime().exec("taskkill /f /im cmd.exe");

        // give it a chance to close
        TimeUnit.SECONDS.sleep(1);

        // fourth, copy the new fbi app to files9 folder
        copyFile("./temp/Universal-Inject-Generator-master/fbi_inject_with_banner.app",
                "./files9/fbi_inject_with_banner.app");

        // fifth, delete the temp folder
        deleteDirectory(new File("./temp/"));
    }

    // the following function is taken directly from user oyo on StackExchange
    // http://stackoverflow.com/questions/3775694/deleting-folder-from-java
    private boolean deleteDirectory(File directory)
    {
        if (directory.exists())
        {
            File[] files = directory.listFiles();
            if (null != files)
            {
                for (int i = 0; i < files.length; i++)
                {
                    if (files[i].isDirectory())
                    {
                        deleteDirectory(files[i]);
                        System.out.println("deleting " + files[i].getName());
                    }
                    else
                    {
                        files[i].delete();
                        System.out.println("deleting " + files[i].getName());
                    }
                }
            }
        }
        return (directory.delete());
    }

}
