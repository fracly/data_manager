package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Job;
import com.bcht.data_manager.mapper.CollectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionService extends BaseService {

    @Autowired
    private CollectionMapper collectionMapper;

    public final Logger logger = LoggerFactory.getLogger(CollectionService.class);

    public String transformFile2String(File file, HashMap<String, Integer> realMap) throws IOException {
        Map<Integer, Integer> alterMap = new HashMap();
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"GBK");
        BufferedReader br = new BufferedReader(isr);
        String line;
        int i = 0; //行数
        while((line = br.readLine()) != null) {
            byte[] bytes = line.getBytes("UTF-8");
            line = new String(bytes, 0, bytes.length);
            int columnSize = numsCount(line, ' '); //上传文件第一行的字段数量
            if(i == 0) {
                alterMap = getAlterMap(realMap, line, columnSize);
                if(alterMap == null) { return null; }
                i ++;
                continue;
            }
            else {                                      // i!=0 开始读取数据部分
                line = alterAction(alterMap, line, columnSize);
            }
            stringBuilder.append(line + System.lineSeparator());
            i++;
        }
        br.close();
        return  stringBuilder.toString();
    }

    public Integer numsCount(String line, char searchChar){
        int len = line.length();
        int count = 0;
        for(int i = 0; i < len; i++){
            char tmp = line.charAt(i);
            if (tmp == searchChar) count++;
        }
        return count + 1;
    }

    public Map<Integer, Integer> getAlterMap(HashMap<String, Integer> realMap, String line , int columnSize){
        Map<Integer, Integer> alterMap = new HashMap();
        Integer currentIndex = 0, originalIndex = 0;
        String name;
        String[] columnValues = line.split(" ");
        while(currentIndex < columnSize)
        {
            name = columnValues[currentIndex];
            originalIndex = realMap.get(name);
            if(originalIndex == null) {
                return null;
            }
            alterMap.put(currentIndex, originalIndex);
            currentIndex ++;
        }

        return alterMap;
    }

    public HashMap<String, Integer> getRealMap(List<String> list){
        HashMap<String, Integer> realMap = new HashMap();
        for (int i = 0; i < list.size(); i++){
            realMap.put(list.get(i), i);
        }
        return realMap;
    }

    public void saveAsFileWriter(String content, String path){
        FileWriter fw = null;
        try{
            fw = new FileWriter(path);
            fw.write(content);
        }catch(IOException e ){
            e.printStackTrace();
        }finally{
            try{
                fw.flush();
                fw.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public String  alterAction(Map<Integer, Integer> hashmap, String string,  int num){
        ArrayList<String> arrayList = new ArrayList(num);
        int i = 0;
        while(i < num){
            arrayList.add("");
            i++;
        }
        i = 0;
        while (i < num)
        {
//             System.out.println("真实索引值: " + hashmap.get(i) + " 名称 :" + string.split(" ")[i] );
            arrayList.set(hashmap.get(i), string.split(" ")[i]);
            i++;
        }
        System.out.println(arrayList.toString());
        String s = arrayList.toString().replace("[","").replace("]","").replace(", ", " ");
        s = s.replace(" ", "|");
        return s;
    }

    public int insert(Job job) {
        return collectionMapper.insert(job);
    }

    public List<Job> jobList(Integer creatorId, Integer status) {
        return collectionMapper.jobList(creatorId, status);
    }

    public int jobDelete(Integer jobId) {
        return collectionMapper.jobDelete(jobId);
    }
}
