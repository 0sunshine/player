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

import android.app.Activity;
import android.content.Context;
import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.activities.VideoActivity;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
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


//        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
//        MediaCodecInfo[] codecInfos = codecList.getCodecInfos();
//
//        int max_item = codecInfos.length > 10 ? 10:codecInfos.length;
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

        mAdapter.addItem("xxx", "0801-154600");
        mAdapter.addItem("http://192.168.6.200:9044/live/avit/4M/4000000/4000000_llhls.m3u8", "hls");
        mAdapter.addItem("http://192.168.6.200:29090/vod/OP001/14232/testmss_8000000.m3u8", "vod");
        mAdapter.addItem("http://192.168.8.152:9044/live/avit/4M/4000000/4000000_llhls.m3u8", "hls");
        mAdapter.addItem("http://192.168.8.152:29090/vod/OP001/14232/testmss_8000000.m3u8", "vod");
        mAdapter.addItem("udp://238.110.0.2:30000", "udp 8M");
        mAdapter.addItem("udp://238.123.45.6:30000", "udp 8M");
        mAdapter.addItem("udp://238.123.45.6:30000?buffer_size=1024000", "udp 8M");
        mAdapter.addItem("udp://238.123.45.6:30000?buffer_size=2048000", "udp 8M");
        mAdapter.addItem("udp://238.123.45.6:30000?buffer_size=4096000", "udp 8M");
        mAdapter.addItem("avitrtc://238.110.0.1:30000?serverip=192.168.8.152&serverport=14042", "4K");
        mAdapter.addItem("avitrtc://238.110.0.2:30000?serverip=192.168.8.152&serverport=14043", "8M");
        mAdapter.addItem("avitrtc://238.110.0.3:30000?serverip=192.168.8.152&serverport=14044", "4M");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear3/prog_index.m3u8", "bipbop basic 640x480 @ 1 Mbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear4/prog_index.m3u8", "bipbop basic 960x720 @ 2 Mbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear0/prog_index.m3u8", "bipbop basic 22.050Hz stereo @ 40 kbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8", "bipbop advanced master playlist");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear1/prog_index.m3u8", "bipbop advanced 416x234 @ 265 kbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear2/prog_index.m3u8", "bipbop advanced 640x360 @ 580 kbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear3/prog_index.m3u8", "bipbop advanced 960x540 @ 910 kbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear4/prog_index.m3u8", "bipbop advanced 1289x720 @ 1 Mbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear5/prog_index.m3u8", "bipbop advanced 1920x1080 @ 2 Mbps");
        mAdapter.addItem("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear0/prog_index.m3u8", "bipbop advanced 22.050Hz stereo @ 40 kbps");
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.mNameTextView = (TextView) view.findViewById(android.R.id.text1);
                viewHolder.mUrlTextView = (TextView) view.findViewById(android.R.id.text2);
            }

            SampleMediaItem item = getItem(position);
            viewHolder.mNameTextView.setText(item.mName);
            viewHolder.mUrlTextView.setText(item.mUrl);

            return view;
        }

        final class ViewHolder {
            public TextView mNameTextView;
            public TextView mUrlTextView;
        }
    }
}
