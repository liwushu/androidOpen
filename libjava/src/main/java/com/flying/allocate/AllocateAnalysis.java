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

public class AllocateAnalysis {
    private static final String FILE_NAME = "./libjava/allocations.txt";
    //private static final String FILE_NAME = "./libjava/tmp.txt";
    private static HashMap<String,Long> sAllocationMap = new HashMap<>();
    private static ArrayList<OutputInfo> sOutputInfo = new ArrayList<>();

    public static void main(String[] args) {
        File file = new File(FILE_NAME);

        if(!file.exists()) {
            System.out.println("file: " +file.getAbsolutePath()+" not exists");
            return;
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(file,"r");
            String readBuffer;
            AllocationInfo allocationInfo = new AllocationInfo();
            while((readBuffer =raf.readLine()) != null) {
                if(readBuffer.length() == 0) {
                    continue;
                }
                parseSize(allocationInfo,readBuffer);
                parseTotalSize(allocationInfo,readBuffer);
                parseSoInfo(allocationInfo,readBuffer);
                parseEndStacktrace(allocationInfo,readBuffer);
            }
            printAllocationInfo();
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

    private static void parseSize(AllocationInfo allocationInfo,String readBuffer) {
        if(readBuffer.trim().startsWith(AllocationUtils.SIZE_TAG)) {
            String[] sizeValue = readBuffer.split(AllocationUtils.SPLIT_TAG);
            if(sizeValue != null && sizeValue.length == 2) {
                long size = Long.parseLong(sizeValue[1].trim());
                allocationInfo.size = size;
            }else{
                System.out.println("size error");
            }
        }
    }

    private static void  parseTotalSize(AllocationInfo allocationInfo,String readBuffer) {
        if(readBuffer.contains(AllocationUtils.TOTAL_SIZE_TAG)) {
            String[] sizeValue = readBuffer.split(AllocationUtils.SPLIT_TAG);
            if(sizeValue != null && sizeValue.length == 2) {
                long size = Long.parseLong(sizeValue[1].trim());
                allocationInfo.totalSize = size;
            }else{
                System.out.println("total size error");
            }
        }
    }

    private static void parseSoInfo(AllocationInfo allocationInfo,String readBuffer) {
        if(readBuffer.contains(AllocationUtils.SPLIT_START_TAG) && readBuffer.contains(AllocationUtils.SPLIT_END_TAG)) {
            int startIndex = readBuffer.indexOf(AllocationUtils.SPLIT_START_TAG);
            int endIndex = readBuffer.indexOf(AllocationUtils.SPLIT_END_TAG);
            String soName = readBuffer.substring(startIndex,endIndex);
            allocationInfo.beginStackSoFullName = soName;
            long value = 0;
            if(sAllocationMap.containsKey(soName)) {
                value = sAllocationMap.get(soName);
            }
            value += allocationInfo.size;
            sAllocationMap.put(soName,value);
        }
    }


    private static void printAllocationInfo() {
        Iterator<Map.Entry<String,Long>> it = sAllocationMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,Long> entry = it.next();
            //System.out.println(entry.getKey()+"   size: "+entry.getValue());
            OutputInfo outputInfo = new OutputInfo();
            outputInfo.soName = entry.getKey();
            outputInfo.size = entry.getValue();
            sOutputInfo.add(outputInfo);
        }

        sOutputInfo.sort(new Comparator<OutputInfo>() {
            @Override
            public int compare(OutputInfo outputInfo, OutputInfo t1) {
                return (int) (t1.size - outputInfo.size);
            }
        });

        Iterator<OutputInfo> outputInfoIterator = sOutputInfo.iterator();
        while (outputInfoIterator.hasNext()) {
            OutputInfo outputInfo = outputInfoIterator.next();
            System.out.println(outputInfo.soName +"  size: "+outputInfo.size);
        }

    }
}
