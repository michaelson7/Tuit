package com.nkwazi_tech.tuit_app.Fragments;

import com.nkwazi_tech.tuit_app.Classes.Constant;

import java.io.File;

public class Methods {
    public static void load_Directory_Files(File directories){
        File[] filelist = directories.listFiles();
        if (filelist != null && filelist.length>0){
            for (int i=0;i<filelist.length;i++){
                if (filelist[i].isDirectory()){
                    load_Directory_Files(filelist[i]);
                }
                else{
                    String name = filelist[i].getName().toLowerCase();
                    for (String extension: Constant.videoExtensions){
                        if (name.endsWith(extension)){
                            Constant.allMediaList.add(filelist[i]);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void load_Books(File directories){
        File[] filelist = directories.listFiles();
        if (filelist != null && filelist.length>0){
            for (int i=0;i<filelist.length;i++){
                if (filelist[i].isDirectory()){
                    load_Books(filelist[i]);
                }
                else{
                    String name = filelist[i].getName().toLowerCase();
                    for (String extension: Constant.bookExtensions){
                        if (name.endsWith(extension)){
                            Constant.allMediaList.add(filelist[i]);
                            break;
                        }
                    }
                }
            }
        }
    }
}
