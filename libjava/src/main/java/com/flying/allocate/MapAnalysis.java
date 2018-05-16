package com.example.allocate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapAnalysis {
    private static final String MAP_FILE_NAME = "./libjava/map/maps";
    private static final String KEY_SUFFIEX_TAG = ".so";
    private static final String KEY_TAG = StatUtils.KEY_TAG;

    private static final int HEX_VALUE = 16;

    public static HashMap<String,MapInfo> mapInfoHashMap = new HashMap<>();
    public static ArrayList<MapInfo> mapInfoArrayList = new ArrayList<>();

    public static void main(String[] args) {
        parseMapInfo();
        print();
    }

    public static void parseMapInfo() {
        File file = new File(MAP_FILE_NAME);
        if(!file.exists()) {
            System.out.println("maps not exists");
            return;
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
            String readBuffer ;
            String keyTagValue;
            String addrValue;
            String[] addrValues;
            long startAddrValue;
            long endAddrValue;
            MapInfo mapInfo;
            while ((readBuffer = randomAccessFile.readLine()) != null) {
                if(readBuffer.length() == 0) {
                    continue;
                }

                if(readBuffer.contains(KEY_TAG) && readBuffer.contains(KEY_SUFFIEX_TAG)) {
                    int keyIndex = readBuffer.indexOf("/");
                    int index = readBuffer.indexOf('r');
                    keyTagValue = readBuffer.substring(keyIndex);
                    addrValue = readBuffer.substring(0,index-1);
                    addrValues = addrValue.split("-");
                    startAddrValue = Long.valueOf(addrValues[0],HEX_VALUE);
                    endAddrValue = Long.valueOf(addrValues[1],HEX_VALUE);
                    if(mapInfoHashMap.containsKey(keyTagValue)) {
                        mapInfo = mapInfoHashMap.get(keyTagValue);
                        if(mapInfo.addrStart>startAddrValue) {
                            mapInfo.addrStart=startAddrValue;
                        }
                        if(mapInfo.addrEnd<endAddrValue) {
                            mapInfo.addrEnd = endAddrValue;
                        }
                    }else{
                        mapInfo = new MapInfo();
                        mapInfo.soName= keyTagValue;
                        mapInfo.addrStart = startAddrValue;
                        mapInfo.addrEnd = endAddrValue;
                        mapInfoHashMap.put(keyTagValue,mapInfo);
                    }

//                        mapInfo = new MapInfo();
//                        mapInfo.soName= keyTagValue;
//                        mapInfo.addrStart = startAddrValue;
//                        mapInfo.addrEnd = endAddrValue;
//                        mapInfoArrayList.add(mapInfo);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print() {
        Iterator<Map.Entry<String,MapInfo>> iterator = mapInfoHashMap.entrySet().iterator();
        String soName;
        long startAddr;
        long endAddr;
        while (iterator.hasNext()) {
            Map.Entry<String,MapInfo> entry = iterator.next();
            MapInfo mapInfo = entry.getValue();
            soName = entry.getValue().soName;
            startAddr = entry.getValue().addrStart;
            endAddr = entry.getValue().addrEnd;
            mapInfo.memorySize = endAddr - startAddr;
            mapInfoArrayList.add(mapInfo);
            //System.out.printf("startAddr: %x   endAddrValue: %x,  value : %d soName: %s\n",startAddr,endAddr,(endAddr-startAddr)/1024,soName);
        }

        mapInfoArrayList.sort(new Comparator<MapInfo>() {
            @Override
            public int compare(MapInfo mapInfo, MapInfo t1) {
                return (int) (t1.memorySize-mapInfo.memorySize);
            }
        });

        for(MapInfo mapInfo:mapInfoArrayList) {
            System.out.println(mapInfo.soName+"   "+mapInfo.memorySize/1024+" kb");
        }
    }


    private static void print1() {
        Iterator<MapInfo> iterator = mapInfoArrayList.iterator();
        String soName;
        long startAddr;
        long endAddr;
        long size = 0;
        while (iterator.hasNext()) {
            MapInfo mapInfo = iterator.next();
            soName = mapInfo.soName;
            startAddr = mapInfo.addrStart;
            endAddr = mapInfo.addrEnd;
            size += endAddr - startAddr;
            //System.out.printf("startAddr: %x   endAddrValue: %x,  value : %d soName: %s\n",startAddr,endAddr,(endAddr-startAddr),soName);
        }

        System.out.println("size: "+size/1024+" kb");
    }
}
