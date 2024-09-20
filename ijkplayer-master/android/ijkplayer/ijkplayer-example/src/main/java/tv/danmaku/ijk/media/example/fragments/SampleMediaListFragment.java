/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.activities.VideoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecList;

public class SampleMediaListFragment extends Fragment {
    private ListView mFileListView;
    private SampleMediaAdapter mAdapter;

    public static SampleMediaListFragment newInstance() {
        SampleMediaListFragment f = new SampleMediaListFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_file_list, container, false);
        mFileListView = (ListView) viewGroup.findViewById(R.id.file_list_view);
        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        mAdapter = new SampleMediaAdapter(activity);

        mFileListView.setAdapter(mAdapter);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                SampleMediaItem item = mAdapter.getItem(position);
                String name = item.mName;
                String url = item.mUrl;
                VideoActivity.intentTo(activity, url, name);
            }
        });

        mFileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, final long id) {
                SampleMediaItem item = mAdapter.getItem(position);

                Context ctx = mFileListView.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(item.mName);

                // 创建 EditText 作为输入框
                final EditText input = new EditText(ctx);
                input.setText(item.mUrl);
                builder.setView(input);

                // 设置确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 更新数据
                        String editedText = input.getText().toString();
                        item.mUrl = editedText;

                        writeJsonFile();
                        mAdapter.notifyDataSetChanged();  // 通知适配器更新 ListView
                    }
                });

                // 设置取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 显示对话框
                builder.show();

                return true;
            }
        });


//        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
//        MediaCodecInfo[] codecInfos = codecList.getCodecInfos();
//
//        int max_item = codecInfos.length > 20 ? 20:codecInfos.length;
//
//        for(int i =0; i < max_item; ++i){
//            MediaCodecInfo codecInfo = codecInfos[i];
//            String info = codecInfo.getName() + "-";
//
//            mAdapter.addItem( info, "MediaCodecList");
//        }


//        try {
//            MediaCodec codec = MediaCodec.createByCodecName("");
//            codec.setInputSurface();
//            mAdapter.addItem(codec.getName(), "codec");
//
//        } catch (IOException e) {
//            mAdapter.addItem(e.getMessage(), "codec err");
//        }

        //mAdapter.addItem("xxx", "0801-154600");
//        mAdapter.addItem("http://192.168.6.200:9044/live/avit/4M/4000000/4000000_llhls.m3u8", "hls");
//        mAdapter.addItem("http://192.168.6.200:29090/vod/OP001/14232/testmss_8000000.m3u8", "vod");
//        mAdapter.addItem("http://192.168.8.152:9044/live/avit/4M/4000000/4000000_llhls.m3u8", "hls");
//        mAdapter.addItem("http://192.168.8.152:29090/vod/OP001/14232/testmss_8000000.m3u8", "vod");
//        mAdapter.addItem("udp://238.123.45.6:30000?buffer_size=1024000", "udp 8M");
//        mAdapter.addItem("udp://238.123.45.6:30000?buffer_size=2048000", "udp 8M");
//        mAdapter.addItem("avitrtc://238.110.0.1:30000?serverip=192.168.8.152&serverport=14042", "4K");
//        mAdapter.addItem("avitrtc://238.110.0.2:30000?serverip=192.168.8.152&serverport=14043", "8M");
//        mAdapter.addItem("avitrtc://238.110.0.3:30000?serverip=192.168.8.152&serverport=14044", "4M");
//

        // 读取 JSON 文件
        String jsonString = readJsonFile();
        if(!jsonString.isEmpty())
        {
            try {
                // 将字符串转换为 JSONObject
                JSONObject jsonObject = new JSONObject(jsonString);

                // 获取 "channel" 数组
                JSONArray peopleArray = jsonObject.getJSONArray("channel");

                // 遍历数组
                for (int i = 0; i < peopleArray.length(); i++) {
                    JSONObject personObject = peopleArray.getJSONObject(i);

                    // 获取每个对象的字段
                    String url = personObject.getString("url");
                    String name = personObject.getString("name");

                    mAdapter.addItem(url, name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(mAdapter.getCount() == 0)
        {
            showlog("mAdapter.getCount == 0, use default");

            mAdapter.addItem("avitrtc://238.110.0.1:30000?max_buffer=800&min_buffer=500&enable_video_buffer=1&serverip=192.168.8.152&serverport=14042", "超高清50fps");
            mAdapter.addItem("avitrtc://238.110.0.1:30000?max_buffer=500&min_buffer=200&enable_video_buffer=1&serverip=192.168.8.152&serverport=14042", "超高清50fps");
            mAdapter.addItem("avitrtc://238.110.0.1:30000?max_buffer=200&min_buffer=50&enable_video_buffer=0&serverip=192.168.8.152&serverport=14042", "超高清50fps");

            mAdapter.addItem("avitrtc://238.110.0.2:30000?max_buffer=800&min_buffer=500&enable_video_buffer=1&serverip=192.168.8.152&serverport=14043", "超高清25fps");
            mAdapter.addItem("avitrtc://238.110.0.2:30000?max_buffer=500&min_buffer=200&enable_video_buffer=1&serverip=192.168.8.152&serverport=14043", "超高清25fps");
            mAdapter.addItem("avitrtc://238.110.0.2:30000?max_buffer=200&min_buffer=50&enable_video_buffer=0&serverip=192.168.8.152&serverport=14043", "超高清25fps");

            mAdapter.addItem("avitrtc://238.110.0.2:30000?max_buffer=800&min_buffer=500&enable_video_buffer=1&serverip=192.168.8.152&serverport=14044", "超高清264 50M");
            mAdapter.addItem("avitrtc://238.110.0.3:30000?max_buffer=500&min_buffer=200&enable_video_buffer=1&serverip=192.168.8.152&serverport=14044", "超高清264 50M");
            mAdapter.addItem("avitrtc://238.110.0.3:30000?max_buffer=200&min_buffer=50&enable_video_buffer=0&serverip=192.168.8.152&serverport=14044", "超高清264 50M");
        }
    }

    public void showlog(String log){
        Context ctx = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("日志");

        // 创建 EditText 作为输入框
        final EditText input = new EditText(ctx);
        input.setText(log);
        builder.setView(input);

        // 显示对话框
        builder.show();
    }

    // 读取 JSON 文件
    public String readJsonFile() {
        String json = "";
        try {
            InputStream is = getContext().openFileInput("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


            //showlog("read json: " + json);

        } catch (IOException e) {
            showlog(e.toString());
        }
        return json;
    }

    // 写入 JSON 文件
    public void writeJsonFile() {
        try {
            OutputStream os = getContext().openFileOutput("data.json", Context.MODE_PRIVATE); // 写入 data.json 文件

            //showlog("write to " + getContext().getFilesDir().getAbsolutePath());

            JSONArray channelArray = new JSONArray();

            for(int i = 0; i < mAdapter.getCount(); ++i)
            {
                SampleMediaItem item = mAdapter.getItem(i);
                JSONObject channel = new JSONObject();

                channel.put("name", item.mName);
                channel.put("url", item.mUrl);

                channelArray.put(channel);
            }

            JSONObject jsonObject = new JSONObject();
            // 将 JSON 数组添加到 JSON 对象
            jsonObject.put("channel", channelArray);
            String json = jsonObject.toString();
            //showlog("write json: " + json);
            os.write(json.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            showlog(e.toString());
            return;
        } catch (JSONException e) {
            showlog(e.toString());
            return;
        }
    }

    final class SampleMediaItem {
        String mUrl;
        String mName;

        public SampleMediaItem(String url, String name) {
            mUrl = url;
            mName = name;
        }
    }

    final class SampleMediaAdapter extends ArrayAdapter<SampleMediaItem> {
        public SampleMediaAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
        }

        public void addItem(String url, String name) {
            add(new SampleMediaItem(url, name));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.mNameTextView = (TextView) view.findViewById(android.R.id.text1);

                //viewHolder.mUrlTextView.setVisibility(View.INVISIBLE);
            }

            SampleMediaItem item = getItem(position);
            if(item != null){
                viewHolder.mNameTextView.setText(item.mName);
                viewHolder.mNameTextView.setTextSize(24);

            }else {
                viewHolder.mNameTextView.setText("error");
            }

            return view;
        }

        final class ViewHolder {
            public TextView mNameTextView;
        }
    }
}
