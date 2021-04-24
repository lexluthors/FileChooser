package com.thl.filechooser;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FileTourController {

    private File currentFile;
    private File rootFile;
    private List<FileInfo> currenFileInfoList;
    private List<File> currentFolderList = new ArrayList<>();
    private boolean isRootFile = true;
    private boolean showFile = true;
    private boolean showHideFile = false;
    //是否显示外置sd卡
    private boolean showExternalSdCard = false;
    private String chooseType;
    private int sdcardIndex;


    private Context mContext;

    public FileTourController(Context context, String currentPath, String chooseType, boolean showHideFile, boolean showFile, boolean showExternalSdCard) {
        this.showHideFile = showHideFile;
        this.chooseType = chooseType;
        this.showFile = showFile;
        this.currentFile = new File(currentPath);
        this.mContext = context;
        this.showExternalSdCard = showExternalSdCard;
        rootFile = getRootFile();
        System.out.println("FileTourController.getRootFile " + rootFile.getAbsolutePath());
        if (currentFile == null) {
            this.currentFile = rootFile;
        } else if (!currentFile.exists()) {
            this.currentFile = rootFile;
        } else
            isRootFile = false;

        if (!currentFile.getAbsolutePath().equals(getRootFile().getAbsolutePath())) {
            currentFolderList.add(rootFile);
            ArrayList<File> fileList = new ArrayList<>();
            File tempFile = currentFile;
            while (!tempFile.getParent().equals(rootFile.getAbsolutePath())) {
                fileList.add(tempFile.getParentFile());
                tempFile = tempFile.getParentFile();
            }
            for (int i = fileList.size() - 1; i >= 0; i--) {
                currentFolderList.add(fileList.get(i));
            }
        }

        currenFileInfoList = searchFile(this.currentFile);
        currentFolderList.add(this.currentFile);
    }

    public FileTourController(Context context) {
        this.mContext = context;
        rootFile = getRootFile();
        this.currentFile = rootFile;
        currenFileInfoList = searchFile(this.currentFile);
        currentFolderList.add(this.currentFile);
    }

    public boolean isShowFile() {
        return showFile;
    }

    public void setShowFile(boolean showFile) {
        this.showFile = showFile;
    }

    public boolean isShowHideFile() {
        return showHideFile;
    }

    public void setShowHideFile(boolean showHideFile) {
        this.showHideFile = showHideFile;
    }

    public List<File> getCurrentFolderList() {
        return currentFolderList;
    }

    public List<FileInfo> getCurrenFileInfoList() {
        return currenFileInfoList;
    }

    public File getRootFile() {
        if (sdcardIndex == 1) {
            return getSDcard1();
        } else {
            return getSDcard0();
        }
    }

    public void switchSdCard(int sdcardIndex) {
        if (sdcardIndex == 0) {
            rootFile = getSDcard0();

        } else {
            rootFile = getSDcard1();
        }


        this.currentFile = rootFile;
        currenFileInfoList = new ArrayList<>();
        currentFolderList = new ArrayList<>();
        currenFileInfoList = searchFile(this.currentFile);
        currentFolderList.add(this.currentFile);
    }

    public File getSDcard0() {
        if (showExternalSdCard) {
            return new File(getStoragePath(mContext, false));
        } else {
            return new File(getSDCardPath());
        }
    }

    public File getSDcard1() {
        if (showExternalSdCard) {
            if (getStoragePath(mContext, true) == null)
                return new File(getStoragePath(mContext, false));
            return new File(getStoragePath(mContext, true));
        } else {
            return new File(getSDCardPath());
        }
    }

    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isRootFile() {
        if (isRootFile(currentFile))
            isRootFile = true;
        else
            isRootFile = false;
        return isRootFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public List<FileInfo> addCurrentFile(File file) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        currentFile = file;
        currentFolderList.add(file);
        fileInfoList = searchFile(file);
        this.currenFileInfoList = fileInfoList;
        return fileInfoList;
    }

    public List<FileInfo> resetCurrentFile(int position) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        while (currentFolderList.size() - 1 > position) {
            currentFolderList.remove(currentFolderList.size() - 1);
        }
        if (currentFolderList.size() != 0)
            currentFile = new File(currentFolderList.get(currentFolderList.size() - 1).getAbsolutePath());
        else
            currentFile = rootFile;
        fileInfoList = searchFile(currentFile);
        this.currenFileInfoList = fileInfoList;
        return fileInfoList;
    }

    public List<FileInfo> searchFile(File file) {
        this.currentFile = file;
        List<FileInfo> fileInfoList = new ArrayList<>();


        File childFiles[] = file.listFiles();
        if (childFiles != null)
            for (int i = 0; i < childFiles.length; i++) {
                FileInfo fileInfo = new FileInfo();
                File childFile = childFiles[i];
                String name = childFile.getName();
                if (name.length() > 0 && String.valueOf(name.charAt(0)).equals(".") && !showHideFile) {
                    continue;
                }
                fileInfo.setFileName(name);
                String time = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(childFile.lastModified()));
                fileInfo.setCreateTime(time);
                fileInfo.setFilePath(childFile.getAbsolutePath());
                if (childFile.isDirectory()) {
                    fileInfo.setFolder(true);
                    fileInfo.setFileType(FileInfo.FILE_TYPE_FOLDER);
                } else {
                    fileInfo.setFolder(false);
                    if ("mp4".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "3gp".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "wmv".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "ts".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "rmvb".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "mov".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "m4v".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "mkv".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "avi".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "3gpp".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "3gpp2".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "flv".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "divx".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "f4v".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "rm".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "asf".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "ram".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "mpg".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "mpeg".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "dat".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "webm".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "qsv".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "v8".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "swf".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "m2v".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "asx".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "ra".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "ndivx".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "xvid".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "m3u8".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_VIDEO);
                    else if ("mp3".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "aac".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "amr".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "ogg".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "wma".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "wav".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "flac".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())) ||
                            "ape".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_AUDIO);
                    else if ("apk".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_APK);
                    else if ("zip".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_ZIP);
                    else if ("rar".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_RAR);
                    else if ("jpeg".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_JPEG);
                    else if ("jpg".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_JPG);
                    else if ("png".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_PNG);
                    else if ("webp".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_WEBP);
                    else if ("gif".equalsIgnoreCase(getFileTypeName(childFile.getAbsolutePath())))
                        fileInfo.setFileType(FileInfo.FILE_TYPE_GIF);
                    else
                        fileInfo.setFileType(FileInfo.FILE_TYPE_FILE);
                }
                if (this.showFile) {
                    fileInfoList.add(fileInfo);
                } else {
                    if (fileInfo.isFolder())
                        fileInfoList.add(fileInfo);
                }
            }
        return listToSortByName(chooseType(chooseType, fileInfoList));
    }

    public static List<FileInfo> chooseType(String chooseType, List<FileInfo> infoList) {
        List<FileInfo> list = new ArrayList<>();
        for (FileInfo fileInfo : infoList) {
            if (chooseType.equals(FileInfo.FILE_TYPE_FOLDER)) {
                list.add(fileInfo);
//                if (fileInfo.isFolder()) {
//                }
            } else {
                if (fileInfo.isFolder()) {
                    list.add(fileInfo);
                } else {
                    if (chooseType.equals(FileInfo.FILE_TYPE_ALL) || chooseType.equals(FileInfo.FILE_TYPE_FILE)) {
                        list.add(fileInfo);
                    } else if (FileInfo.FILE_TYPE_PACKAGE.equals(chooseType)) {
                        //把zip和rar文件加入进去
                        if (FileInfo.FILE_TYPE_ZIP.equals(fileInfo.getFileType()) || FileInfo.FILE_TYPE_RAR.equals(fileInfo.getFileType())) {
                            list.add(fileInfo);
                        }
                    } else if (FileInfo.FILE_TYPE_IMAGE.equals(chooseType)) {
                        //把图片加进去
                        if (FileInfo.FILE_TYPE_JPEG.equals(fileInfo.getFileType())
                                || FileInfo.FILE_TYPE_JPG.equals(fileInfo.getFileType())
                                || FileInfo.FILE_TYPE_WEBP.equals(fileInfo.getFileType())
                                || FileInfo.FILE_TYPE_GIF.equals(fileInfo.getFileType())
                                || FileInfo.FILE_TYPE_PNG.equals(fileInfo.getFileType())) {
                            list.add(fileInfo);
                        }
                    } else if (chooseType.equals(fileInfo.getFileType())) {
                        list.add(fileInfo);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 排序后的对象集合
     *
     * @param list
     * @return
     */
    public List<FileInfo> listToSortByName(List<FileInfo> list) {
        if (list == null || list.size() == 0) {
            return list;
        }
        List<FileInfo> fileList = new ArrayList<>();
        List<FileInfo> folderList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isFolder()) {
                folderList.add(list.get(i));
            } else {
                fileList.add(list.get(i));
            }
        }

        Map<String, FileInfo> map = new HashMap<String, FileInfo>();
        String[] names = new String[folderList.size()];
        for (int i = 0; i < folderList.size(); i++) {
            String name = folderList.get(i).getFileName();
            names[i] = name;
            map.put(name, folderList.get(i));
        }

        Comparator<Object> comparator = Collator.getInstance(Locale.ENGLISH);//中文改成：CHINA
        Arrays.sort(names, comparator);


        Map<String, FileInfo> map2 = new HashMap<String, FileInfo>();
        String[] names2 = new String[fileList.size()];
        for (int i = 0; i < fileList.size(); i++) {
            String name = fileList.get(i).getFileName();
            names2[i] = name;
            map2.put(name, fileList.get(i));
        }

        Comparator<Object> comparator2 = Collator.getInstance(Locale.ENGLISH);//中文改成：CHINA
        Arrays.sort(names2, comparator2);

        folderList.clear();
        for (String name : names) {
            if (map.containsKey(name)) {
                folderList.add(map.get(name));
            }
        }
        fileList.clear();
        for (String name : names2) {
            if (map2.containsKey(name)) {
                fileList.add(map2.get(name));
            }
        }
        list.clear();
        list.addAll(folderList);
        list.addAll(fileList);
        return list;
    }

    public List<FileInfo> backToParent() {
        currentFile = currentFile.getParentFile();
        if (isRootFile(currentFile))
            isRootFile = true;
        else
            isRootFile = false;
        currentFolderList.remove(currentFolderList.size() - 1);
        return resetCurrentFile(currentFolderList.size());
    }

    public boolean isRootFile(File file) {
        return rootFile.getAbsolutePath().equals(file.getAbsolutePath());
    }

    private String getParentName(String path) {
        int end = path.lastIndexOf("/") + 1;
        return path.substring(0, end);
    }

    private String getFileTypeName(String path) {
        int start = path.lastIndexOf(".") + 1;
        if (start == 0)
            return "";
        return path.substring(start);
    }


    /**
     * Get SD card path by CMD.
     */
    public static String getSDCardPath() {
//        String cmd = "cat /proc/mounts";
        String sdcard = null;
//        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
//        BufferedReader bufferedReader = null;
//        try {
//            Process p = run.exec(cmd);// 启动另一个进程来执行命令
//            bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
//            String lineStr;
//            while ((lineStr = bufferedReader.readLine()) != null) {
//                if (lineStr.contains("sdcard")
//                        && lineStr.contains(".android_secure")) {
//                    String[] strArray = lineStr.split(" ");
//                    if (strArray.length >= 5) {
//                        sdcard = strArray[1].replace("/.android_secure", "");
//                        return sdcard;
//                    }
//                }
//                if (p.waitFor() != 0 && p.exitValue() == 1) {
//                    // p.exitValue()==0表示正常结束，1：非正常结束
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (bufferedReader != null) {
//                    bufferedReader.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        sdcard = Environment.getExternalStorageDirectory().getPath();
        return sdcard;
    }
}
