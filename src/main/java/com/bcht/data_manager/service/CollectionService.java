package com.bcht.data_manager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.mapper.SearchMapper;
import com.bcht.data_manager.utils.HttpClient;
import com.bcht.data_manager.utils.MapUtils;
import com.bcht.data_manager.utils.PropertyUtils;
import com.bcht.data_manager.utils.StringUtils;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(CollectionService.class);
    public static String txt2String(StringBuilder fileResult, File file, HashMap<String, Integer> realMap, HashMap<Integer, Integer> alterMap){
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"GBK");
            BufferedReader br = new BufferedReader(isr);
            String s = "";
            int i = 0; //行数
            alterMap = null;
            while((s = br.readLine()) != null) {
                byte[] bytes = s.getBytes("UTF-8");
                s = new String(bytes, 0, bytes.length);
                int num = numsCount(s, ' '); //上传文件第一行的字段数量
                if(i == 0) { //i=0(第一行)并且s非空
//                    s = alterAction(alterMap, s, num);
                    alterMap = getAlterMap(realMap, s, num);
                    i++;
                    continue;
                }
                else {                                      // i!=0 开始读取数据部分
                    s = alterAction(alterMap, s, num);
                }
                fileResult.append(s + System.lineSeparator());
                i++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return  fileResult.toString();
    }
//    public static String txt2String(StringBuilder fileResult, File file, HashMap<String, Integer> realMap, HashMap<Integer, Integer> alterMap){
//        try{
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String s = null;
//            int i = 0; //行数
//            while((s = br.readLine()) != null) {
////                s= s.getBytes("UTF-8");
//                int num = numsCount(s, ' '); //上传文件第一行的字段数量
//                alterMap = alterIndex(realMap, s, num);
//                if(i == 0 && !StringUtil.isNullOrEmpty(s)) { //i=0(第一行)并且s非空
////                    s = alterAction(alterMap, s, num);
//                    continue;
//                }
//                else {                                      // i!=0 开始读取数据部分
//                    s = alterAction(alterMap, s, num);
//                }
//                fileResult.append(s + System.lineSeparator());
//                i++;
//            }
//            br.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return  fileResult.toString();
//    }
    public static Integer numsCount(String line, char searchChar){
        int len = line.length();
        int count = 0;
        for(int i = 0; i < len; i++){
            char tmp = line.charAt(i);
            if (tmp == searchChar) count++;
        }
        return count + 1;
    }
    public static HashMap<Integer, Integer> getAlterMap(HashMap<String, Integer> hashMap, String string , int num){
        HashMap<Integer,Integer> alterMap = new HashMap();
        int i = 0, realIndex =0;
        String name = null;
//            List l = new ArrayList<String>();
        while(i < num) //属性个数
        {
            try{
                name = string.split(" ")[i];
//                l.add(i, name);
                realIndex = hashMap.get(name);
                alterMap.put(i, realIndex);
                i++;
            }catch (Exception e){
                System.out.println("表中没有注释为 '" + name + "'的属性");
                break;
            }
        }

        return alterMap;
    }

    public static HashMap<String, Integer> getRealMap(List<String> list){
        HashMap<String, Integer> realMap = new HashMap();
        for (int i = 0; i < list.size(); i++){
            realMap.put(list.get(i), i);  //为什么这里报错?
        }
        return realMap;
    }

    public static void saveAsFileWriter(String content, String path){
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
    public static String[] listToArray(List<String> list){
        String[] array = {};
        if(list == null || list.size() ==0){
            return null;
        }
        else{
            array = (String[]) list.toArray();
        }
        return array;
    }

    public static String  alterAction(HashMap<Integer, Integer> hashmap, String string,  int num){
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
}
