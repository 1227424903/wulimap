package com.map.wulimap.util;


import android.content.Intent;

import android.net.Uri;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

//文件操作工具
public final class FileUtil {
    static FileInputStream fin;
    static InputStreamReader isr;
    static BufferedReader br;
    static String line = "";
    static FileOutputStream fout;
    static OutputStreamWriter osw;
    static BufferedWriter bw;


    public static boolean xiugaiwenjianming(String oldname, String newname) {
        if (newname.equals(oldname))
            return true;

        File oldfile = new File(oldname);
        if (!(oldfile.exists()))
            return false;

        File newfile = new File(newname);
        if (newfile.exists()) {
            return false;
        }

        return (oldfile.renameTo(newfile));
    }


    public static boolean shanchuwenjian(String name) {
        File file = new File(name);
        if (!(file.exists()))
            return false;

        if (file.isDirectory()) {
            return false;
        }

        return (file.delete());
    }


    public static boolean chuanjianmulu(String path) {
        String[] dir = path.split("/");
        String dist = dir[0];
        boolean result = true;
        if (dir.length > 0) {
            for (int i = 1; i < dir.length; ++i) {
                dist = dist + "/" + dir[i];
                File mkdir = new File(dist);
                if (!(mkdir.exists()))
                    result = mkdir.mkdir();
            }

            return result;
        }
        return false;
    }


    public static boolean shanchumulu(String path) {
        File dir = new File(path);
        if (!(dir.exists()))
            return false;

        return deleteDir(dir);
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!(success))
                    return false;
            }
        }

        return dir.delete();
    }


    public static boolean shifouweimulu(String name) {
        File file = new File(name);
        if (!(file.exists()))
            return false;

        return (file.isDirectory());
    }


    public static boolean shifouyingcangwenjian(String name) {
        File file = new File(name);
        if (file.exists())
            return file.isHidden();

        return false;
    }


    public static boolean wenjianshifoucunzai(String name) {
        return new File(name).exists();
    }


    public static String quwenjianbianma(String filename) {
        File file = new File(filename);
        String charset = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fis);
            in.mark(4);
            byte[] first3bytes = new byte[3];
            in.read(first3bytes);
            in.reset();
            if ((first3bytes[0] == -17) && (first3bytes[1] == -69) && (first3bytes[2] == -65))
                charset = "utf-8";
            else if ((first3bytes[0] == -1) && (first3bytes[1] == -2))
                charset = "unicode";
            else if ((first3bytes[0] == -2) && (first3bytes[1] == -1))
                charset = "utf-16be";
            else if ((first3bytes[0] == -1) && (first3bytes[1] == -1))
                charset = "utf-16le";
            else
                charset = "GBK";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return charset;
    }


    public static String durushuruwenben(String filename, String charset) {
        String res = "";
        if (new File(filename).exists())
            try {
                FileInputStream fin = new FileInputStream(filename);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                res = new String(buffer, 0, length, charset);
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return res;
    }


    public static boolean xiechuwenbenwenjian(String filename, String message, String charset) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);
            byte[] bytes = message.getBytes(charset);
            fout.write(bytes);
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static byte[] duruzijiewenjian(String filename) {
        byte[] buffer = null;
        if (new File(filename).exists())
            try {
                FileInputStream fin = new FileInputStream(filename);
                int length = fin.available();
                buffer = new byte[length];
                fin.read(buffer);
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return buffer;
    }


    public static boolean xiechuzijiewenjian(String filename, byte[] bytes) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);
            fout.write(bytes);
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static long quwenjianchicun(String filePath) {
        File file = new File(filePath);
        long blockSize = -9010657068486492160L;
        try {
            if (file.isDirectory())
                blockSize = getFileSizes(file);
            else
                blockSize = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockSize;
    }

    private static long getFileSize(File file) throws Exception {
        long size = -9010657068486492160L;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    private static long getFileSizes(File f) throws Exception {
        long size = -9010657068486492160L;
        File[] flist = f.listFiles();
        for (int i = 0; i < flist.length; ++i)
            if (flist[i].isDirectory())
                size += getFileSizes(flist[i]);
            else
                size += getFileSize(flist[i]);


        return size;
    }


    private static boolean writeStreamToFile(InputStream stream, File file) {
        int read;
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];

            while ((read = stream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            try {
                output.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            read = 0;


        } finally {
            try {
                output.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public static String quwenjianguanjianci(String Path, String keyword) {
        String result = "";

        File[] files = new File(Path).listFiles();
        File[] arr$ = files;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; ++i$) {
            File f = arr$[i$];

            if (f.getName().indexOf(keyword) >= 0) {
                result = f.getPath() + "\n" + result;
            }
        }
        return result;
    }


    public static String xunzhaowenjianhouzhuiming(String Path, String Extension) {
        String result = "";
        File[] files = new File(Path).listFiles();
        File[] arr$ = files;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; ++i$) {
            File f = arr$[i$];

            if ((f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) &&
                    (!(f.isDirectory()))) {
                result = f.getPath() + "\n" + result;
            }
        }

        return result;
    }


    public static boolean fuzhiwenjian(String sourcePath, String targetPath) {
        if (new File(sourcePath).exists()) {
            try {
                int bytesum = 0;
                int byteread = 0;
                InputStream inStream = new FileInputStream(sourcePath);
                FileOutputStream fs = new FileOutputStream(targetPath);
                byte[] buffer = new byte[1444];

                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }


    public static boolean chuangjianwenjian(String path) {
        boolean result = false;
        File f = new File(path);
        if (!(f.exists()))
            try {
                result = f.createNewFile();
            } catch (IOException ioex) {
            }
        else
            result = true;

        return result;
    }


    public static boolean dakaiwenbenwenjiandu(String filename, String charset) {
        if (new File(filename).exists())
            try {
                fin = new FileInputStream(filename);
                isr = new InputStreamReader(fin, charset);
                br = new BufferedReader(isr);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        return false;
    }


    public static boolean guanbidu() {
        try {
            br.close();
            fin.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String duyihang() {
        try {
            if ((FileUtil.line = br.readLine()) != null)
                return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static boolean dakaiwenbenwenjianxie(String filename, String charset) {
        if (new File(filename).exists())
            try {
                fout = new FileOutputStream(filename);
                osw = new OutputStreamWriter(fout, charset);
                bw = new BufferedWriter(osw);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        return false;
    }


    public static boolean guanbixie() {
        try {
            bw.close();
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean xieyihang(String message) {
        try {
            bw.newLine();
            bw.write(message);
            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String quzimulu(String dir) {
        String pa = "";
        File f = new File(dir);
        File[] ff = f.listFiles();
        for (int i = 0; i < ff.length; ++i)
            if (ff[i].isDirectory())
                pa = pa + ff[i].getAbsolutePath() + "|";

        return pa;
    }


    public static String quwenjianxiugaishijian(String path) {
        File f = new File(path);
        long time = f.lastModified();

        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String modifiedtime = format.format(date);
        return modifiedtime;
    }


    private static Intent openFile(String filePath) {
        File file = new File(filePath);
        if ((file == null) || (!(file.exists())) || (file.isDirectory()))
            return null;

        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();

        if ((end.equals("m4a")) || (end.equals("mp3")) || (end.equals("mid")) || (end.equals("xmf")) || (end.equals("ogg")) || (end.equals("wav")))
            return getAudioFileIntent(filePath);
        if ((end.equals("3gp")) || (end.equals("mp4")))
            return getAudioFileIntent(filePath);
        if ((end.equals("jpg")) || (end.equals("gif")) || (end.equals("png")) || (end.equals("jpeg")) || (end.equals("bmp")))
            return getImageFileIntent(filePath);
        if (end.equals("apk"))
            return getApkFileIntent(filePath);
        if (end.equals("ppt"))
            return getPptFileIntent(filePath);
        if (end.equals("xls"))
            return getExcelFileIntent(filePath);
        if (end.equals("doc"))
            return getWordFileIntent(filePath);
        if (end.equals("pdf"))
            return getPdfFileIntent(filePath);
        if (end.equals("chm"))
            return getChmFileIntent(filePath);
        if (end.equals("txt"))
            return getTextFileIntent(filePath, false);

        return getAllIntent(filePath);
    }

    private static Intent getAllIntent(String param) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    private static Intent getApkFileIntent(String param) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    private static Intent getVideoFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(67108864);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    private static Intent getAudioFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(67108864);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    private static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    private static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    private static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    private static Intent getExcelFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    private static Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    private static Intent getChmFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    private static Intent getTextFileIntent(String param, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    private static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}