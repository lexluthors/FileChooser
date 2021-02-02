package com.thl.filechooser;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class FileAdapter extends CommonAdapter<FileInfo> {

    //    private int sign = -1;
    private int chooseCount = 1;
    private String chooseType;
    private FileChooserActivity.SelectAllListener selectAllListener;
    //List<FileInfo> selectList = new ArrayList<>();
    //选中的都放在这里
    LinkedHashMap<String, FileInfo> selectMap = new LinkedHashMap<>();

    public FileAdapter(Context context, ArrayList<FileInfo> dataList, int resId, String chooseType, int chooseCount, FileChooserActivity.SelectAllListener selectAllListener) {
        super(context, dataList, resId);
        this.chooseType = chooseType;
        this.chooseCount = chooseCount;
        this.selectAllListener = selectAllListener;
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder, final FileInfo data, final int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.fileName);
        TextView textTime = (TextView) holder.itemView.findViewById(R.id.fileTime);
        RelativeLayout rlt_item = (RelativeLayout) holder.itemView.findViewById(R.id.rlt_item);
        textView.setText(data.getFileName());
        textTime.setText(data.getCreateTime());

        ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.fileIcon);
        View divider = holder.itemView.findViewById(R.id.divider);
        if (FileInfo.FILE_TYPE_VIDEO.equals(data.getFileType())) {
            imageView.setImageResource(R.drawable.format_video);
        } else if (FileInfo.FILE_TYPE_AUDIO.equals(data.getFileType())) {
            imageView.setImageResource(R.drawable.format_music);
        } else if (FileInfo.FILE_TYPE_APK.equals(data.getFileType())) {
            imageView.setImageResource(R.drawable.format_app);
        } else if (FileInfo.FILE_TYPE_ZIP.equals(data.getFileType()) || FileInfo.FILE_TYPE_RAR.equals(data.getFileType())) {
            imageView.setImageResource(R.drawable.format_compress);
        } else if (FileInfo.FILE_TYPE_JPEG.equals(data.getFileType())
                || FileInfo.FILE_TYPE_JPG.equals(data.getFileType())
                || FileInfo.FILE_TYPE_WEBP.equals(data.getFileType())
                || FileInfo.FILE_TYPE_GIF.equals(data.getFileType())
                || FileInfo.FILE_TYPE_PNG.equals(data.getFileType())) {
            imageView.setImageResource(R.drawable.format_picture);
        } else if (FileInfo.FILE_TYPE_FOLDER.equals(data.getFileType())) {
            imageView.setImageResource(R.drawable.format_folder);
        } else {
            imageView.setImageResource(R.drawable.format_other);
        }


        if (position != dataList.size() - 1) {
            divider.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.GONE);
        }

        ImageView fileChoose = (ImageView) holder.itemView.findViewById(R.id.fileChoose);
        if (data.isCheck()) {
            fileChoose.setImageResource(R.drawable.log_choose_checkbox_on);
        } else {
            fileChoose.setImageResource(R.drawable.log_choose_checkbox_off);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position, data);
                }
            }
        });

//        if(data.getFileType().equals(chooseType))

        if (chooseType.equals(FileInfo.FILE_TYPE_ALL)) {
            fileChoose.setVisibility(View.VISIBLE);
            fileChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyData(position);
                }
            });
        } else if (chooseType.equals(FileInfo.FILE_TYPE_FOLDER)) {
            boolean folder = data.isFolder();
            if (folder) {
                fileChoose.setVisibility(View.VISIBLE);
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyData(position);
                    }
                });
            } else {

                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                fileChoose.setVisibility(View.GONE);
            }
        } else if (chooseType.equals(FileInfo.FILE_TYPE_FILE)) {
            boolean folder = data.isFolder();
            if (!folder) {
                fileChoose.setVisibility(View.VISIBLE);
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyData(position);
                    }
                });
            } else {
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                fileChoose.setVisibility(View.GONE);
            }
        } else if (chooseType.equals(FileInfo.FILE_TYPE_IMAGE)) {

            if (FileInfo.FILE_TYPE_JPEG.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_JPG.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_WEBP.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_GIF.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_PNG.equals(data.getFileType())) {

                fileChoose.setVisibility(View.VISIBLE);
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyData(position);
                    }
                });
            } else {
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                fileChoose.setVisibility(View.GONE);
            }

        } else if (chooseType.equals(FileInfo.FILE_TYPE_PACKAGE)) {
            if (FileInfo.FILE_TYPE_ZIP.equals(data.getFileType()) || FileInfo.FILE_TYPE_RAR.equals(data.getFileType())) {
                fileChoose.setVisibility(View.VISIBLE);
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyData(position);
                    }
                });
            } else {
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                fileChoose.setVisibility(View.GONE);
            }
        } else {
            if (chooseType.equals(data.getFileType())) {
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyData(position);
                    }
                });
                fileChoose.setVisibility(View.VISIBLE);
            } else {
                fileChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                fileChoose.setVisibility(View.GONE);
            }
        }
    }

    private ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, FileInfo data);
    }

    public void notifyClick(FileInfo data, int position) {
        if (chooseType.equals(FileInfo.FILE_TYPE_ALL)) {
            notifyData(position);
        } else if (chooseType.equals(FileInfo.FILE_TYPE_FOLDER)) {
            boolean folder = data.isFolder();
            if (folder) {
                notifyData(position);
            }
        } else if (chooseType.equals(FileInfo.FILE_TYPE_FILE)) {
            boolean folder = data.isFolder();
            if (!folder) {
                notifyData(position);
            }
        } else if (chooseType.equals(FileInfo.FILE_TYPE_IMAGE)) {
            if (FileInfo.FILE_TYPE_JPEG.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_JPG.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_WEBP.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_GIF.equals(data.getFileType())
                    || FileInfo.FILE_TYPE_PNG.equals(data.getFileType())) {
                notifyData(position);
            }
        } else if (chooseType.equals(FileInfo.FILE_TYPE_PACKAGE)) {
            if (FileInfo.FILE_TYPE_ZIP.equals(data.getFileType()) || FileInfo.FILE_TYPE_RAR.equals(data.getFileType())) {
                notifyData(position);
            }
        } else if (chooseType.equals(data.getFileType())) {
            notifyData(position);
        }
    }

    public int getSign() {
        return selectMap.size();
    }

    public List<FileInfo> getChooseFilePath() {
        return new ArrayList<>(selectMap.values());
    }

    public void notifyData() {
        notifyDataSetChanged();
    }

    public void notifyData(int position) {
        if (dataList.get(position).isCheck()) {
            dataList.get(position).setCheck(false);
            selectMap.remove(dataList.get(position).getFilePath());
        } else {
            if (selectMap.size() >= chooseCount) {
                Toast.makeText(context, "最多选择" + chooseCount + "个", Toast.LENGTH_LONG).show();
                return;
            }
            dataList.get(position).setCheck(true);
            selectMap.put(dataList.get(position).getFilePath(), dataList.get(position));
        }
        //取消添加一个就要重新判断是不是全选
        selectAllListener.onSelectAll();
        notifyDataSetChanged();
    }

    // 全选时调用
    public void notifyAllData(boolean isSelectAll) {
        int size = dataList.size();
        selectMap.clear();
        for (int i = 0; i < size; i++) {
            if(chooseType.equals(FileInfo.FILE_TYPE_FILE)){
                //选择文件，不选文件夹
                if(!dataList.get(i).isFolder()){
                    //如果是文件
                    if(isSelectAll){
                        dataList.get(i).setCheck(true);
                        selectMap.put(dataList.get(i).getFilePath(), dataList.get(i));
                    }else{
                        dataList.get(i).setCheck(false);
                    }
                }
            }else if(chooseType.equals(FileInfo.FILE_TYPE_FOLDER)){
                //选择文件夹，不选文件
                if(dataList.get(i).isFolder()){
                    //如果是文件
                    if(isSelectAll){
                        dataList.get(i).setCheck(true);
                        selectMap.put(dataList.get(i).getFilePath(), dataList.get(i));
                    }else{
                        dataList.get(i).setCheck(false);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public void setData(List<FileInfo> infoList) {
        int size = infoList.size();
        for (int i = 0; i < size; i++) {
            for (String key : selectMap.keySet()) {
                if (key.contentEquals(infoList.get(i).getFilePath())) {
                    //相同的路径,说明已经被选中了
                    infoList.get(i).setCheck(true);
                }
            }
        }
//        List<FileInfo> list = new ArrayList<>();
//        for (FileInfo fileInfo : infoList) {
//            if (fileInfo.isFolder()){
//                list.add(fileInfo);
//            }else{
//                if(chooseType.equals(fileInfo.getFileType())){
//                    list.add(fileInfo);
//                }
//            }
//        }
        super.setData(FileTourController.chooseType(chooseType, infoList));
    }


}
