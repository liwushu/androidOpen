package com.flying.allocate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SMapsAnalysis {
    private static final String KEY_TAG = StatUtils.KEY_TAG;
    private static final int HEX_VALUE = 16;
    private static final int SO_NAME_SIZE = 80;

    private static Stats[] stats = new Stats[31];

    private static HashMap<String,Stats> statsHashMap = new HashMap<>();
    private static ArrayList<Stats> sOutputInfo = new ArrayList<>();
    private static ArrayList<Stats> sSoOutputInfo = new ArrayList<>();
    private static ArrayList<Stats> sSoMmapOutInfo = new ArrayList<>();
    private static ArrayList<Stats> sMallocOutInfo = new ArrayList<>();
    private static int sSo_size = 0;
    private static int sSo_rss_size =0;
    private static int sMalloc_size = 0;
    private static int native_heap = 0;
    private static int so_mmap = 0;

    public static void main(String[] args) {
        parseMaps();
        outputPrint();

        MapAnalysis.parseMapInfo();
        MapAnalysis.print();

        compare();
    }

    private static void parseMaps() {
        //File file = new File("./libjava/smaps");
        File file = new File("./libjava/map/smaps");
        if(!file.exists()) {
            System.out.println("file not exist");
            return;
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(file,"r");
            String readBuffer;
            Stats stats = new Stats();
            while((readBuffer =raf.readLine()) != null) {
                if(readBuffer.length() == 0) {
                    continue;
                }
                parseHeader(stats,readBuffer);
                parseRssSize(stats,readBuffer);
                parsePssSize(stats,readBuffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void parseEndStacktrace(AllocationInfo allocationInfo,String readBuffer) {
        if(readBuffer.contains(AllocationUtils.END_STACK_TRACE)) {
            allocationInfo.reset();
        }
    }

    private static void parseHeader(Stats stats,String readBuffer) {
        if(readBuffer.trim().contains(StatUtils.HEAD_TAG)) {
            int index = readBuffer.indexOf(StatUtils.HEAD_TAG);
            String value = readBuffer.substring(index);
            stats.statName = value;
        }else {
            if(readBuffer.trim().contains(StatUtils.HEAT_TAG1) && !readBuffer.contains(StatUtils.NAME_PREFIX)){
                stats.statName = StatUtils.HEAT_TAG1;
                int index = readBuffer.indexOf('r');
                String addrValue = readBuffer.substring(0,index-1);
                String[] addrValues = addrValue.split("-");
                long startAddrValue = Long.valueOf(addrValues[0],HEX_VALUE);
                long endAddrValue = Long.valueOf(addrValues[1],HEX_VALUE);
                stats.startAddr = startAddrValue;
                stats.endAddr = endAddrValue;
            }
        }
    }

    private static void  parseRssSize(Stats stats,String readBuffer) {
        if(stats.statName == null || stats.statName.length() == 0) {
            return;
        }
        if(readBuffer.startsWith(StatUtils.RSS_TAG)) {
            String[] sizeValue = readBuffer.split(StatUtils.SIZE_SPLIT);
            if(sizeValue != null && sizeValue.length == 2) {
                String value = sizeValue[1].trim();
                int index = value.indexOf(StatUtils.SIZE_END);
                int size = Integer.valueOf(value.substring(0,index-1));
                stats.rss = size;
            }else{
                System.out.println("total size error");
            }
        }
    }

    private static void  parsePssSize(Stats stats,String readBuffer) {
        if(stats.statName == null || stats.statName.length() == 0) {
            return;
        }
        if(readBuffer.startsWith(StatUtils.PSS_TAG)) {
            String[] sizeValue = readBuffer.split(AllocationUtils.SPLIT_TAG);
            if(sizeValue != null && sizeValue.length == 2) {
                String value = sizeValue[1].trim();
                int index = value.indexOf(StatUtils.SIZE_END);
                int size = Integer.valueOf(value.substring(0,index-1));
                stats.pss = size;
            }else{
                System.out.println("total size error");
                return;
            }
            if(stats.statName.contains(StatUtils.HEAT_TAG1)) {
                sMallocOutInfo.add(new Stats(stats));
            }

            if (statsHashMap.containsKey(stats.statName)) {
                Stats stats1 = statsHashMap.get(stats.statName);
                stats1.rss += stats.rss;
                stats1.pss += stats.pss;
            }else {
                statsHashMap.put(stats.statName,new Stats(stats));
            }
            stats.reset();
        }
    }

    private static void outputPrint() {
        Set<Map.Entry<String,Stats>> set = statsHashMap.entrySet();
        Iterator<Map.Entry<String,Stats>> it = set.iterator();
        while(it.hasNext()) {
            Map.Entry<String,Stats> entry = it.next();
            Stats stats = entry.getValue();
            if(stats.pss != 0) {
                if(stats.statName.contains(".so")) {
                    sSoOutputInfo.add(entry.getValue());
                    sSo_size += entry.getValue().pss;
                    if(stats.statName.contains(KEY_TAG)) {
                        sSoMmapOutInfo.add(entry.getValue());
                    }
                }else if(stats.statName.equals(StatUtils.HEAT_TAG1)){
                    sMalloc_size += entry.getValue().pss;
                    //sMallocOutInfo.add(stats);
                }else {
                    sOutputInfo.add(entry.getValue());
                }


            }
        }

//        sOutputInfo.sort(new Comparator<Stats>() {
//            @Override
//            public int compare(Stats stats1, Stats stats2) {
//                return stats2.rss - stats1.rss;
//            }
//        });
//
        sSoOutputInfo.sort(new Comparator<Stats>() {
            @Override
            public int compare(Stats stats1, Stats stats2) {
                return stats2.pss - stats1.pss;
            }
        });
//
//        sSoMmapOutInfo.sort(new Comparator<Stats>() {
//            @Override
//            public int compare(Stats stats1, Stats stats2) {
//                return stats2.pss - stats1.pss;
//            }
//        });
        System.out.println("Native heap: "+sMalloc_size);
        System.out.println(".so mmap: "+sSo_size);
        //print(sSoOutputInfo);
        print(sSoMmapOutInfo);
        //print(sOutputInfo);
        printMalloc(sMallocOutInfo);
    }


    private static void print(ArrayList<Stats> list) {
        Iterator<Stats> outputInfoIterator = list.iterator();
        while (outputInfoIterator.hasNext()) {
            Stats outputInfo = outputInfoIterator.next();
//            System.out.println(outputInfo);
            System.out.printf("%s   pss=%d kb \n",getSoName(outputInfo.statName,SO_NAME_SIZE),outputInfo.pss);
        }
    }

    private static void printMalloc(ArrayList<Stats> list) {
        Iterator<Stats> outputInfoIterator = list.iterator();
        while (outputInfoIterator.hasNext()) {
            Stats outputInfo = outputInfoIterator.next();
            System.out.printf("start: %x  end: %x %s name: %s\n",outputInfo.startAddr,outputInfo.endAddr,outputInfo.pss,outputInfo.statName);
        }
    }

    public static class Stats{
        String statName;
        long startAddr;
        long endAddr;
        int pss;
        int rss;
        int swappablePss;
        int privateDirty;
        int sharedDirty;
        int privateClean;
        int sharedClean;
        int swappedOut;
        int swappedOutPss;

        public Stats() {

        }

        public Stats(Stats stats) {
            this.statName = stats.statName;
            this.pss = stats.pss;
            this.rss = stats.rss;
            this.swappablePss = stats.swappablePss;
            this.privateDirty = stats.privateDirty;
            this.sharedDirty = stats.sharedDirty;
            this.sharedClean = stats.sharedClean;
            this.swappedOut = stats.swappedOut;
            this.swappablePss = stats.swappablePss;
            this.startAddr = stats.startAddr;
            this.endAddr = stats.endAddr;
        }

        @Override
        public String toString() {
            return statName+
                    "  startAddr: "+startAddr+
                    "  endAdd: "+endAddr+
                    "     pss=" +pss;
        }

        public void reset() {
            statName = null;
            pss = 0;
            rss = 0;
            swappablePss = 0;
            privateDirty = 0;
            sharedDirty = 0;
            privateClean = 0;
            sharedClean = 0;
            swappedOut = 0;
            swappedOutPss = 0;
        }
    }

    private static String getSoName(String str,int min_length) {
        String format = "%-" + (min_length < 1 ? 1 : min_length) + "s";
        return String.format(format, str);
    }


    private static void compare() {
        Iterator<MapInfo> iterator = MapAnalysis.mapInfoArrayList.iterator();
        while(iterator.hasNext()) {
            MapInfo mapInfo = iterator.next();
            long start = mapInfo.addrStart;
            long end = mapInfo.addrEnd;
            for(Stats stats:sMallocOutInfo) {
                if((start>=stats.startAddr)&& (end<=stats.endAddr)) {
                    System.out.printf("start: %x  end: %x  %d  name: %s\n",stats.startAddr,stats.endAddr,stats.statName,stats.statName);
                }
            }
        }
    }
}
